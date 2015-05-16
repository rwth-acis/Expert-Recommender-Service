package i5.las2peer.services.servicePackage;

import i5.las2peer.api.Service;
import i5.las2peer.restMapper.HttpResponse;
import i5.las2peer.restMapper.RESTMapper;
import i5.las2peer.restMapper.annotations.ContentParam;
import i5.las2peer.restMapper.annotations.GET;
import i5.las2peer.restMapper.annotations.POST;
import i5.las2peer.restMapper.annotations.Path;
import i5.las2peer.restMapper.annotations.PathParam;
import i5.las2peer.restMapper.annotations.QueryParam;
import i5.las2peer.restMapper.annotations.Version;
import i5.las2peer.restMapper.tools.ValidationResult;
import i5.las2peer.restMapper.tools.XMLCheck;
import i5.las2peer.services.servicePackage.database.DatabaseHandler;
import i5.las2peer.services.servicePackage.entities.DataEntity;
import i5.las2peer.services.servicePackage.entities.DataInfoEntity;
import i5.las2peer.services.servicePackage.entities.EvaluationMetricsEntity;
import i5.las2peer.services.servicePackage.entities.ExpertEntity;
import i5.las2peer.services.servicePackage.entities.GraphEntity;
import i5.las2peer.services.servicePackage.entities.QueryEntity;
import i5.las2peer.services.servicePackage.entities.SemanticTagEntity;
import i5.las2peer.services.servicePackage.entities.UserEntity;
import i5.las2peer.services.servicePackage.graph.GraphWriter;
import i5.las2peer.services.servicePackage.graph.JUNGGraphCreator;
import i5.las2peer.services.servicePackage.indexer.DbSematicsIndexer;
import i5.las2peer.services.servicePackage.indexer.DbTextIndexer;
import i5.las2peer.services.servicePackage.indexer.LuceneMysqlIndexer;
import i5.las2peer.services.servicePackage.metrics.EvaluationMeasure;
import i5.las2peer.services.servicePackage.ocd.OCD;
import i5.las2peer.services.servicePackage.parsers.CommunityCoverMatrixParser;
import i5.las2peer.services.servicePackage.parsers.Post;
import i5.las2peer.services.servicePackage.parsers.Posts;
import i5.las2peer.services.servicePackage.parsers.User;
import i5.las2peer.services.servicePackage.parsers.Users;
import i5.las2peer.services.servicePackage.scorer.CommunityAwareHITSStrategy;
import i5.las2peer.services.servicePackage.scorer.CommunityAwarePageRankStrategy;
import i5.las2peer.services.servicePackage.scorer.HITSStrategy;
import i5.las2peer.services.servicePackage.scorer.ModelingStrategy1;
import i5.las2peer.services.servicePackage.scorer.PageRankStrategy;
import i5.las2peer.services.servicePackage.scorer.ScoreStrategy;
import i5.las2peer.services.servicePackage.scorer.ScoringContext;
import i5.las2peer.services.servicePackage.searcher.LuceneSearcher;
import i5.las2peer.services.servicePackage.textProcessor.PorterStemmer;
import i5.las2peer.services.servicePackage.textProcessor.QueryAnalyzer;
import i5.las2peer.services.servicePackage.textProcessor.StopWordRemover;
import i5.las2peer.services.servicePackage.utils.Application;
import i5.las2peer.services.servicePackage.utils.LocalFileManager;
import i5.las2peer.services.servicePackage.utils.UserMapSingleton;
import i5.las2peer.services.servicePackage.visualization.Visualizer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.codec.binary.Base64;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.TopDocs;

import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

/**
 * @author sathvik
 */

@Path("ers")
@Version("0.1")
public class ExpertRecommenderService extends Service {

    public ExpertRecommenderService() {
	// read and set properties values
	// IF THE SERVICE CLASS NAME IS CHANGED, THE PROPERTIES FILE NAME NEED
	// TO BE CHANGED TOO!
	setFieldValues();

    }

    @POST
    @Path("datasets")
    public HttpResponse uploadDataset() {
	// TODO: Allow users to upload datasets.
	return null;
    }

    @GET
    @Path("datasets")
    public HttpResponse getAvailableDatasets() {

	DatabaseHandler handler = new DatabaseHandler("ersdb", "root", "");
	JsonArray datasetsObj = null;
	try {
	    Dao<DataInfoEntity, Long> DatasetInfoDao = DaoManager.createDao(handler.getConnectionSource(), DataInfoEntity.class);
	    List<DataInfoEntity> datasets = DatasetInfoDao.queryForAll();

	    datasetsObj = new JsonArray();
	    for (DataInfoEntity entity : datasets) {
		String name = entity.getDatasetName();
		long id = entity.getId();
		String description = "NA";

		JsonObject jObj = new JsonObject();
		jObj.addProperty("name", name);
		jObj.addProperty("id", id);
		jObj.addProperty("description", description);

		datasetsObj.add(jObj);

	    }

	} catch (SQLException e) {
	    e.printStackTrace();
	}

	HttpResponse res = new HttpResponse(datasetsObj.toString());
	res.setStatus(200);
	return res;

    }

    // @GET
    // @Path("datasets/{datasetId}/stopwords")
    // public HttpResponse getStopWords() {
    // // TODO:Get list of stop words used for the particular dataset.
    // return null;
    //
    // }

    // @POST
    // @Path("datasets/{datasetId}/algorithms/{algorithmName}")
    // public HttpResponse applyAlgorithm() {
    // // TODO: Apply algorithm and return ids.
    // return null;
    //
    // }

    @GET
    @Path("datasets/{datasetId}/experts/{expertsId}")
    public HttpResponse getExperts(@PathParam("datasetId") String datasetId, @PathParam("expertsId") String expertsId) {
	System.out.println("expertsId:: " + expertsId);
	String databaseName = getDatabaseName(datasetId);
	if (databaseName == null) {
	    // Throw custom exception.
	}

	DatabaseHandler dbHandler = null;
	dbHandler = new DatabaseHandler(databaseName, "root", "");
	String experts = dbHandler.getExperts(Long.parseLong(expertsId));

	HttpResponse res = new HttpResponse(experts);
	res.setStatus(200);
	return res;

    }

    @GET
    @Path("datasets/{datasetId}/evaluations/{evaluationId}")
    public HttpResponse getEvaluationResults(@PathParam("datasetId") String datasetId, @PathParam("evaluationId") String evaluationId) {

	String databaseName = getDatabaseName(datasetId);
	if (databaseName == null) {
	    // Throw custom exception.
	}

	System.out.println("evaluationId:: " + evaluationId);
	DatabaseHandler dbHandler = null;
	dbHandler = new DatabaseHandler(databaseName, "root", "");
	String evaluationMeasures = dbHandler.getEvaluationMetrics(Long.parseLong(evaluationId));

	HttpResponse res = new HttpResponse(evaluationMeasures);
	res.setStatus(200);
	return res;
    }

    @GET
    @Path("datasets/{datasetId}/visualizations/{visualizationId}")
    public HttpResponse getVisulaizationData(@PathParam("visualizationId") String visId) {

	System.out.println("expertsId:: " + visId);
	DatabaseHandler dbHandler = null;
	dbHandler = new DatabaseHandler("healthcare", "root", "");
	String visGraph = dbHandler.getVisGraph(Long.parseLong(visId));

	String fileContentsString = "data:" + "text/xml" + ";base64," + Base64.encodeBase64String(visGraph.getBytes());

	HttpResponse res = new HttpResponse(fileContentsString, 200);
	res.setHeader("content-type", "text/xml");
	res.setStatus(200);

	return res;
    }

    /**
     * Method to remove stop words form the text.
     * 
     * @param text
     *            an input text to remove stop words.
     * @return res success status and stripped down text with all stopwords
     *         removed.
     */
    @POST
    @Path("stopword_remover")
    public HttpResponse removeStopWords(@ContentParam String text) {
	// TODO: handle any exceptions.
	StopWordRemover remover = new StopWordRemover(text);

	HttpResponse res = new HttpResponse(remover.getPlainText());
	res.setStatus(200);
	return res;

    }

    /**
     * Method to stem the text
     * 
     * @param text
     *            an input text to remove stop words.
     * @return res success status and stemmed text.
     */
    @POST
    @Path("stemmer")
    public HttpResponse stemWords(@ContentParam String text) {
	// TODO: handle any exceptions.
	StringBuilder stemmed_sentence = new StringBuilder();
	if (text != null && text.length() > 0) {
	    PorterStemmer stemmer = new PorterStemmer();
	    String[] words = text.split("\\s+");
	    for (String word : words) {
		stemmed_sentence.append(stemmer.stem(word));
		stemmed_sentence.append(" ");
	    }
	}

	HttpResponse res = new HttpResponse(stemmed_sentence.toString());
	res.setStatus(200);
	return res;

    }

    @GET
    @Path("datasets/{datasetId}/users/{userId}")
    public HttpResponse getUser(@PathParam("userId") String userId) {

	System.out.println("expertsId:: " + userId);
	DatabaseHandler dbHandler = null;
	dbHandler = new DatabaseHandler("healthcare", "root", "");
	String userDetails = dbHandler.getUser(Long.parseLong(userId));

	HttpResponse res = new HttpResponse(userDetails);
	res.setStatus(200);
	return res;
    }

    @POST
    @Path("modeling")
    public HttpResponse modelExperts(@ContentParam String text) {

	Application.algoName = "modeling1";
	String query = text;

	// Stopwatch timer = Stopwatch.createStarted();
	// TODO: Semantic analysis of the text.
	QueryAnalyzer qAnalyzer = null;
	try {
	    qAnalyzer = new QueryAnalyzer(query);
	} catch (Exception e) {
	    e.printStackTrace();
	}

	DatabaseHandler dbHandler = null;
	dbHandler = new DatabaseHandler("healthcare", "root", "");

	ConnectionSource connectionSource = null;
	connectionSource = dbHandler.getConnectionSource();
	long queryId = qAnalyzer.getId(connectionSource);

	String expert_posts = "{}";

	DbTextIndexer dbTextIndexer = null;
	DbSematicsIndexer dbSemanticsIndexer = null;

	try {

	    dbTextIndexer = new DbTextIndexer(dbHandler.getConnectionSource());
	    dbTextIndexer.buildIndex(qAnalyzer.getText());

	    dbSemanticsIndexer = new DbSematicsIndexer(dbHandler.getConnectionSource());
	    dbSemanticsIndexer.buildIndex(qAnalyzer.getText());

	    ScoringContext scontext = new ScoringContext(new ModelingStrategy1(dbTextIndexer, dbSemanticsIndexer));
	    scontext.executeStrategy();
	    expert_posts = scontext.getExperts();

	    System.out.println("Evaluating modeling technique");

	    EvaluationMeasure eMeasure = new EvaluationMeasure(scontext.getExpertMap(), dbTextIndexer.getUserMap(), "Modeling1");

	    // Compute Evaluation Measures.
	    try {
		eMeasure.computeAll();
		eMeasure.save(queryId, connectionSource);
	    } catch (IOException e) {
		e.printStackTrace();
	    }

	} catch (SQLException e) {

	    e.printStackTrace();
	    HttpResponse res = new HttpResponse("Some error occured on the server, Please contact the developer..." + e);
	    res.setStatus(404);
	    return res;
	} catch (IOException e) {
	    e.printStackTrace();
	} catch (ParseException e) {
	    e.printStackTrace();
	}

	HttpResponse res = new HttpResponse(expert_posts);
	res.setStatus(200);
	return res;
    }

    /**
     * 
     * @param datasetId
     *            Long value identifying the corresponding dataset.
     * @param algorithmName
     *            String value specifying the algorithm name. pagerank,
     *            hits,communityAwareHITS.
     * @param query
     *            String value specifying the query/information need.
     * @param isEvaluation
     *            A boolean value to identify if evaluation is required or not.
     * @param isVisualization
     *            A boolean value to identify if visualization is required or
     *            not.
     * @return 200 response code and A json string containing list of ids i.e
     *         expertId, evaluationId, visualizationId.
     */
    @POST
    @Path("datasets/{datasetId}/algorithms/{algorithmName}")
    public HttpResponse applyPageRank(@PathParam("datasetId") String datasetId, @PathParam("algorithmName") String algorithmName,
	    @ContentParam String query, @QueryParam(name = "evaluation", defaultValue = "false") boolean isEvaluation,
	    @QueryParam(name = "visualization", defaultValue = "false") boolean isVisualization) {

	if (query == null) {
	    // TODO: Throw custom exception.
	}

	if (query != null && query.length() < 0) {
	    // TODO: Throw custom exception.
	}

	String expert_posts = "{}";
	QueryAnalyzer qAnalyzer = null;
	try {
	    qAnalyzer = new QueryAnalyzer(query);
	} catch (Exception e) {
	    e.printStackTrace();
	}

	String databaseName = getDatabaseName(datasetId);
	if (databaseName == null) {
	    // Throw custom exception.
	}

	DatabaseHandler dbHandler = null;
	dbHandler = new DatabaseHandler(databaseName, "root", "");

	ConnectionSource connectionSource = null;
	connectionSource = dbHandler.getConnectionSource();
	long queryId = qAnalyzer.getId(connectionSource);

	JUNGGraphCreator jcreator = null;
	LuceneSearcher searcher = null;
	GraphWriter graphWriter = null;

	try {
	    // System.out.println("Performing search...");

	    searcher = new LuceneSearcher(qAnalyzer.getText(), databaseName + "_index");
	    TopDocs docs = searcher.performSearch(qAnalyzer.getText(), Integer.MAX_VALUE);
	    searcher.buildQnAMap(docs);

	    jcreator = new JUNGGraphCreator();
	    jcreator.createGraph(searcher.getQnAMap(), searcher.getPostId2UserIdMap());
	    // System.out.println("Graph created");

	    graphWriter = new GraphWriter(jcreator);
	    graphWriter.saveToGraphMl("graph_jung.graphml");
	    graphWriter.saveToDb(queryId, connectionSource);

	} catch (Exception e) {
	    e.printStackTrace();
	    System.out.println(e);
	    HttpResponse res = new HttpResponse("Exception while searching...");
	    res.setStatus(200);
	}

	Map<Long, UserEntity> usermap = null;

	try {
	    usermap = UserMapSingleton.getInstance().getUserMap(connectionSource);
	} catch (SQLException e1) {
	    e1.printStackTrace();
	}

	if (usermap == null) {
	    // TODO: Throw custom exception.
	}

	ScoreStrategy strategy = null;
	switch (algorithmName) {
	case "pagerank":
	    // System.out.println("Applying page rank strategy...");
	    strategy = new PageRankStrategy(jcreator.getGraph(), usermap);
	    break;
	case "hits":
	    System.out.println("Applying HITS strategy...");
	    strategy = new HITSStrategy(jcreator.getGraph(), usermap);
	    break;
	case "communityAwarePagerank":
	    System.out.println("Applying community Aware PageRank strategy...");
	    OCD ocdPageRank = new OCD();
	    String covers = ocdPageRank.getCovers(graphWriter.getGraphAsString("graph_jung.graphml"));
	    CommunityCoverMatrixParser CCMP = new CommunityCoverMatrixParser(covers);
	    CCMP.parse();

	    strategy = new CommunityAwarePageRankStrategy(jcreator.getGraph(), usermap, CCMP.getNodeId2CoversMap());
	    break;
	case "communityAwareHITS":
	    System.out.println("Applying community Aware HITS strategy...");
	    OCD ocdHITS = new OCD();
	    String coversHITS = ocdHITS.getCovers(graphWriter.getGraphAsString("graph_jung.graphml"));
	    CommunityCoverMatrixParser CCMPHits = new CommunityCoverMatrixParser(coversHITS);
	    CCMPHits.parse();

	    strategy = new CommunityAwareHITSStrategy(jcreator.getGraph(), usermap, CCMPHits.getNodeId2CoversMap());

	    break;
	default:
	    break;
	}

	ScoringContext scontext = new ScoringContext(strategy);
	scontext.executeStrategy();
	expert_posts = scontext.getExperts();

	long expertsId = dbHandler.addExperts(queryId, expert_posts);

	// If evaluation is requested.
	long eMeasureId = -1;
	if (isEvaluation) {
	    EvaluationMeasure eMeasure = new EvaluationMeasure(scontext.getExpertMap(), usermap, algorithmName);

	    // Compute Evaluation Measures.
	    try {
		eMeasure.computeAll();
		eMeasure.save(queryId, connectionSource);
		eMeasureId = eMeasure.getId();
	    } catch (IOException e) {
		e.printStackTrace();
	    } catch (JsonIOException e) {
		e.printStackTrace();
	    } catch (JsonSyntaxException e) {
		e.printStackTrace();
	    }

	}

	// If visulaization is requested, Visualize the result
	// TODO: Currently, vis graph is same as actual graph, later on markers
	// may be added.
	long visId = -1;
	if (isVisualization) {
	    Visualizer visualizer = new Visualizer();
	    visualizer.saveVisGraph(graphWriter.getId(), connectionSource);
	    visId = visualizer.getId();
	}

	JsonObject jObj = new JsonObject();
	jObj.addProperty("expertsId", expertsId);
	jObj.addProperty("evaluationId", eMeasureId);
	jObj.addProperty("visualizationId", visId);

	// System.out.println("Total time " + timer.stop());
	HttpResponse res = new HttpResponse(jObj.toString());
	res.setStatus(200);
	return res;
    }

    // TODO: Move this to test case.
    @POST
    @Path("querysetEvaluator")
    public HttpResponse evaluateQuerySet() {
	ConnectionSource connSrc = null;
	ArrayList<String> queries = new ArrayList<String>();

	try (BufferedReader br = new BufferedReader(new FileReader("fitness_queries_small.txt"))) {
	    for (String line; (line = br.readLine()) != null;) {
		queries.add(line);
	    }
	} catch (FileNotFoundException e1) {
	    e1.printStackTrace();
	} catch (IOException e1) {
	    e1.printStackTrace();
	}

	System.out.println("Queries populated:: " + queries.size());
	// Read the file and populate queries;
	for (String query : queries) {
	    System.out.println("Query:: " + query);
	    Application.reset();
	    StopWordRemover remover = null;
	    String cleanstr = null;
	    // Stopwatch timer = null;
	    try {
		// timer = Stopwatch.createStarted();
		// TODO: Semantic analysis of the text.
		System.out.println("Stop word remover..");
		remover = new StopWordRemover(query);
		cleanstr = remover.getPlainText();

		System.out.println("Getting tokens...");

	    } catch (Exception e) {
		e.printStackTrace();
	    }

	    // Execute strategies.
	    System.out.println("Applying Pagerank...");
	}

	HttpResponse res = new HttpResponse("All tests finished successfully");
	res.setStatus(200);
	return res;
    }

    // TODO:Refactor the path and the method.
    @GET
    @Path("download/{filename}")
    public HttpResponse getGraph(@PathParam("filename") String filename) {

	byte[] data = LocalFileManager.getFile(filename);
	String fileContentsString = null;
	try {
	    fileContentsString = new String(data, "UTF-8");
	} catch (UnsupportedEncodingException e) {
	    e.printStackTrace();
	}
	System.out.println(fileContentsString);

	HttpResponse res = new HttpResponse(fileContentsString, 200);
	res.setHeader("content-type", "text/xml");

	return res;
    }

    /**
     * This method is called only once when the new dataset is added.
     * Currently, it is invoked from the test case.
     * 
     * @param dataset_name
     *            Name of the dataset, whose data has to be indexed. Dataset is
     *            present in datasets directory.
     * @return 200 response code, if database operations and indexing succeeds.
     *         500 response code, if exception occurs.
     */
    @POST
    @Path("indexer")
    public HttpResponse prepareData(@ContentParam String dataset_name) {
	System.out.println("Indexer called...");
	DatabaseHandler dbHandler = new DatabaseHandler(dataset_name, "root", "");
	ConnectionSource connectionSrc;
	HttpResponse res = null;
	try {
	    connectionSrc = dbHandler.getConnectionSource();
	    // Create necessary tables.
	    TableUtils.createTableIfNotExists(connectionSrc, DataEntity.class);
	    TableUtils.createTableIfNotExists(connectionSrc, UserEntity.class);
	    TableUtils.createTableIfNotExists(connectionSrc, SemanticTagEntity.class);
	    TableUtils.createTableIfNotExists(connectionSrc, QueryEntity.class);
	    TableUtils.createTableIfNotExists(connectionSrc, EvaluationMetricsEntity.class);
	    TableUtils.createTableIfNotExists(connectionSrc, GraphEntity.class);
	    TableUtils.createTableIfNotExists(connectionSrc, ExpertEntity.class);

	    String dirPath = "datasets/" + dataset_name;
	    JAXBContext context = JAXBContext.newInstance(Posts.class);
	    Unmarshaller um = context.createUnmarshaller();
	    File file = new File(dirPath + "/posts.xml");

	    Posts posts = (Posts) um.unmarshal(file);
	    List<Post> posts_list = (ArrayList) posts.getResources();
	    // Add posts details into data table
	    dbHandler.addPosts(posts_list);

	    // Add user details into table
	    context = JAXBContext.newInstance(Users.class);
	    File users_file = new File(dirPath + "/users.xml");
	    Unmarshaller um1 = context.createUnmarshaller();
	    Users users = (Users) um1.unmarshal(users_file);
	    List<User> user_list = (ArrayList) users.getUsersList();
	    dbHandler.addUsers(user_list);

	    // Add semantics tag.
	    // dbHandler.addSemanticTags();

	    dbHandler.markExpertsForEvaluation(connectionSrc);

	    System.out.println("Database operations completed...");

	    LuceneMysqlIndexer indexer = new LuceneMysqlIndexer(dbHandler.getConnectionSource(), dataset_name + "_index");
	    indexer.buildIndex();

	    res = new HttpResponse("Indexer finished successfully");
	    res.setStatus(200);

	} catch (SQLException e) {
	    e.printStackTrace();
	    res = new HttpResponse("SQL Exception");
	    res.setStatus(500);
	} catch (IOException e) {
	    e.printStackTrace();
	    res = new HttpResponse("IO Exception");
	    res.setStatus(500);
	} catch (JAXBException e) {
	    e.printStackTrace();
	    res = new HttpResponse("JAXB Exception");
	    res.setStatus(500);
	}

	return res;
    }

    /**
     * Method for debugging purposes. Here the concept of restMapping validation
     * is shown. It is important to check, if all annotations are correct and
     * consistent. Otherwise the service will not be accessible by the
     * WebConnector. Best to do it in the unit tests. To avoid being
     * overlooked/ignored the method is implemented here and not in the test
     * section.
     * 
     * @return true, if mapping correct
     */
    public boolean debugMapping() {
	String XML_LOCATION = "./restMapping.xml";
	String xml = getRESTMapping();

	try {
	    RESTMapper.writeFile(XML_LOCATION, xml);
	} catch (IOException e) {
	    e.printStackTrace();
	}

	XMLCheck validator = new XMLCheck();
	ValidationResult result = validator.validate(xml);

	if (result.isValid())
	    return true;
	return false;
    }

    /**
     * This method is needed for every RESTful application in LAS2peer. There is
     * no need to change!
     * 
     * @return the mapping
     */
    public String getRESTMapping() {
	String result = "";
	try {
	    result = RESTMapper.getMethodsAsXML(this.getClass());
	} catch (Exception e) {

	    e.printStackTrace();
	}
	return result;
    }

    private String getDatabaseName(String datasetId) {
	DatabaseHandler handler = new DatabaseHandler("ersdb", "root", "");
	String databaseName = null;
	try {
	    Dao<DataInfoEntity, Long> DatasetInfoDao = DaoManager.createDao(handler.getConnectionSource(), DataInfoEntity.class);
	    DataInfoEntity datasetEntity = DatasetInfoDao.queryForId(Long.parseLong(datasetId));
	    databaseName = datasetEntity.getDatabaseName();

	} catch (SQLException e) {
	    e.printStackTrace();
	}

	handler.close();

	return databaseName;
    }

}
