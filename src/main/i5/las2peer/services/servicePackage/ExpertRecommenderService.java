package i5.las2peer.services.servicePackage;

import i5.las2peer.api.Service;
import i5.las2peer.restMapper.HttpResponse;
import i5.las2peer.restMapper.MediaType;
import i5.las2peer.restMapper.RESTMapper;
import i5.las2peer.restMapper.annotations.Consumes;
import i5.las2peer.restMapper.annotations.ContentParam;
import i5.las2peer.restMapper.annotations.GET;
import i5.las2peer.restMapper.annotations.POST;
import i5.las2peer.restMapper.annotations.Path;
import i5.las2peer.restMapper.annotations.PathParam;
import i5.las2peer.restMapper.annotations.Produces;
import i5.las2peer.restMapper.annotations.QueryParam;
import i5.las2peer.restMapper.annotations.Version;
import i5.las2peer.restMapper.annotations.swagger.ApiInfo;
import i5.las2peer.restMapper.annotations.swagger.ApiResponse;
import i5.las2peer.restMapper.annotations.swagger.ApiResponses;
import i5.las2peer.restMapper.annotations.swagger.ResourceListApi;
import i5.las2peer.restMapper.annotations.swagger.Summary;
import i5.las2peer.restMapper.tools.ValidationResult;
import i5.las2peer.restMapper.tools.XMLCheck;
import i5.las2peer.services.servicePackage.database.DatabaseHandler;
import i5.las2peer.services.servicePackage.database.entities.DataEntity;
import i5.las2peer.services.servicePackage.database.entities.DataInfoEntity;
import i5.las2peer.services.servicePackage.database.entities.EvaluationMetricsEntity;
import i5.las2peer.services.servicePackage.database.entities.ExpertEntity;
import i5.las2peer.services.servicePackage.database.entities.GraphEntity;
import i5.las2peer.services.servicePackage.database.entities.QueryEntity;
import i5.las2peer.services.servicePackage.database.entities.SemanticTagEntity;
import i5.las2peer.services.servicePackage.database.entities.UserAccEntity;
import i5.las2peer.services.servicePackage.database.entities.UserClickDetails;
import i5.las2peer.services.servicePackage.database.entities.UserEntity;
import i5.las2peer.services.servicePackage.exceptions.ERSException;
import i5.las2peer.services.servicePackage.lucene.indexer.DbSematicsIndexer;
import i5.las2peer.services.servicePackage.lucene.indexer.DbTextIndexer;
import i5.las2peer.services.servicePackage.lucene.indexer.LuceneMysqlIndexer;
import i5.las2peer.services.servicePackage.lucene.searcher.LuceneSearcher;
import i5.las2peer.services.servicePackage.metrics.EvaluationMeasure;
import i5.las2peer.services.servicePackage.scorer.CommunityAwareHITSStrategy;
import i5.las2peer.services.servicePackage.scorer.CommunityAwarePageRankStrategy;
import i5.las2peer.services.servicePackage.scorer.DataModelingStrategy;
import i5.las2peer.services.servicePackage.scorer.HITSStrategy;
import i5.las2peer.services.servicePackage.scorer.PageRankStrategy;
import i5.las2peer.services.servicePackage.scorer.ScoreStrategy;
import i5.las2peer.services.servicePackage.scorer.ScoringContext;
import i5.las2peer.services.servicePackage.textProcessor.PorterStemmer;
import i5.las2peer.services.servicePackage.textProcessor.QueryAnalyzer;
import i5.las2peer.services.servicePackage.textProcessor.StopWordRemover;
import i5.las2peer.services.servicePackage.utils.AlgorithmType;
import i5.las2peer.services.servicePackage.utils.Application;
import i5.las2peer.services.servicePackage.utils.ERSBundle;
import i5.las2peer.services.servicePackage.utils.ExceptionMessages;
import i5.las2peer.services.servicePackage.utils.LocalFileManager;
import i5.las2peer.services.servicePackage.utils.UserMapSingleton;
import i5.las2peer.services.servicePackage.utils.semanticTagger.RelatedPostsExtractor;
import i5.las2peer.services.servicePackage.utils.semanticTagger.TagExtractor;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.TopDocs;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

/**
 * @author sathvik
 */

@Path("ers")
@Version("0.1")
@ApiInfo(title = "Expert Recommender Service", description = "A RESTful expert recommender service", termsOfServiceUrl = "sample-tos.io", contact = "sathvik.parekodi@rwth-aachen.de", license = "Apache License 2", licenseUrl = "http://www.apache.org/licenses/LICENSE-2.0")
public class ExpertRecommenderService extends Service {

    private Log log = LogFactory.getLog(ExpertRecommenderService.class);

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
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.TEXT_PLAIN)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Success") })
    @Summary("Returns the available dataset on the server.")
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

    @GET
    @Path("datasets/{datasetId}/experts/{expertsId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.TEXT_PLAIN)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Success") })
    @Summary("Returns the collection of experts for the specific id. Id is retrieved after applying recommendetaion algorithm on the dataset")
    public HttpResponse getExperts(@PathParam("datasetId") String datasetId, @PathParam("expertsId") String expertsId) {
	log.info("expertsId:: " + expertsId);
	String databaseName = getDatabaseName(datasetId);
	if (databaseName == null) {
	    try {
		throw new ERSException(ExceptionMessages.DATABASE_NOT_FOUND);
	    } catch (ERSException e) {
		e.printStackTrace();
	    }
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
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.TEXT_PLAIN)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Success") })
    @Summary("Returns the evaluation metrics computed if requested when applying algorithms. Evaluation Id is retrieved after applying algorithm on the dataset.")
    public HttpResponse getEvaluationResults(@PathParam("datasetId") String datasetId, @PathParam("evaluationId") String evaluationId) {

	String databaseName = getDatabaseName(datasetId);
	if (databaseName == null) {
	    try {
		throw new ERSException(ExceptionMessages.DATABASE_NOT_FOUND);
	    } catch (ERSException e) {
		e.printStackTrace();
	    }
	}

	log.info("evaluationId:: " + evaluationId);
	DatabaseHandler dbHandler = null;
	dbHandler = new DatabaseHandler(databaseName, "root", "");
	String evaluationMeasures = dbHandler.getEvaluationMetrics(Long.parseLong(evaluationId));

	HttpResponse res = new HttpResponse(evaluationMeasures);
	res.setStatus(200);
	return res;
    }

    @GET
    @Path("datasets/{datasetId}/visualizations/{visualizationId}")
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.TEXT_PLAIN)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Success") })
    @Summary("Returns the visualization graph to be consumed by the client such as sigmaJS")
    public HttpResponse getVisulaizationData(@PathParam("visualizationId") String visId) {

	log.info("expertsId:: " + visId);
	DatabaseHandler dbHandler = null;
	dbHandler = new DatabaseHandler("healthcare", "root", "");
	String visGraph = dbHandler.getVisGraph(Long.parseLong(visId));

	String fileContentsString = "data:" + "text/xml" + ";base64," + Base64.encodeBase64String(visGraph.getBytes());

	HttpResponse res = new HttpResponse(fileContentsString, 200);
	res.setHeader("content-type", "text/xml");
	res.setStatus(200);

	return res;
    }

    @GET
    @Path("datasets/{datasetId}/users/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.TEXT_PLAIN)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Success") })
    @Summary("Returns the details of the experts")
    public HttpResponse getUser(@PathParam("datasetId") String datasetId, @PathParam("userId") String userId) {

	log.info("expertsId:: " + userId);
	DatabaseHandler dbHandler = null;
	String databaseName = getDatabaseName(datasetId);
	if (databaseName == null) {
	    try {
		throw new ERSException(ExceptionMessages.DATABASE_NOT_FOUND);
	    } catch (ERSException e) {
		e.printStackTrace();
	    }
	}
	dbHandler = new DatabaseHandler(databaseName, "root", "");
	String userDetails = dbHandler.getUser(Long.parseLong(userId));

	HttpResponse res = new HttpResponse(userDetails);
	res.setStatus(200);
	return res;
    }

    @POST
    @Path("datasets/{datasetId}/algorithms/datamodeling")

    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.TEXT_PLAIN)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Success") })
    @Summary("Returns the id of the expert collection.")

    public HttpResponse modelExperts(@PathParam("datasetId") String datasetId, @ContentParam String query,
	    @QueryParam(name = "dateBefore", defaultValue = "2025-12-31") String dateBefore,
	    @QueryParam(name = "alpha", defaultValue = "0.5") double alpha) {

	Application.algoName = "datamodeling";
	// String filepath = "testQueries/query_full.txt";

	String databaseName = getDatabaseName(datasetId);
	if (databaseName == null) {
	    try {
		throw new ERSException(ExceptionMessages.DATABASE_NOT_FOUND);
	    } catch (ERSException e) {
		e.printStackTrace();
	    }
	}

	DatabaseHandler dbHandler = new DatabaseHandler(databaseName, "root", "");

	dbHandler.truncateEvaluationTable();

	if (query == null || query.length() < 0) {
	    try {
		throw new ERSException(ExceptionMessages.QUERY_NOT_VALID);
	    } catch (ERSException e) {
		e.printStackTrace();
	    }
	}

	String expertPosts = "";
	QueryAnalyzer qAnalyzer = null;
	try {
	    qAnalyzer = new QueryAnalyzer(query);
	} catch (Exception e) {
	    e.printStackTrace();
	}

	ConnectionSource connectionSource = dbHandler.getConnectionSource();
	long queryId = qAnalyzer.getId(connectionSource);

	DbTextIndexer dbTextIndexer = null;
	DbSematicsIndexer dbSemanticsIndexer = null;

	Map<Long, UserEntity> usermap = null;

	try {
	    usermap = UserMapSingleton.getInstance().getUserMap(connectionSource);
	} catch (SQLException e1) {
	    e1.printStackTrace();
	}

	long expertsId = -1;
	long eMeasureId = -1;

	try {

	    LuceneSearcher searcher = new LuceneSearcher(qAnalyzer.getText(), databaseName + "_index");
	    TopDocs docs = searcher.performSearch(qAnalyzer.getText(), Integer.MAX_VALUE);

	    dbTextIndexer = new DbTextIndexer(searcher.getTotalNumberOfDocs());
	    dbTextIndexer.buildIndex(docs, qAnalyzer.getText(), databaseName + "_index");

	    dbSemanticsIndexer = new DbSematicsIndexer(dbHandler.getConnectionSource());
	    TopDocs semanticDocs = searcher.performSemanticSearch();
	    dbSemanticsIndexer.buildIndex(semanticDocs, qAnalyzer.getText(), databaseName + "_index");

	    ScoringContext scontext = new ScoringContext(new DataModelingStrategy(dbTextIndexer, dbSemanticsIndexer, usermap, alpha));
	    scontext.executeStrategy();
	    expertPosts = scontext.getExperts();

	    expertsId = dbHandler.addExperts(queryId, expertPosts);

	    log.info("Evaluating modeling technique");

	    EvaluationMeasure eMeasure = new EvaluationMeasure(scontext.getExpertMap(), usermap, "datamodeling");

	    // Compute Evaluation Measures.
	    try {
		eMeasure.computeAll();
		eMeasure.save(queryId, connectionSource);
		eMeasureId = eMeasure.getId();
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

	dbHandler.close();

	JsonObject jObj = new JsonObject();
	jObj.addProperty("expertsId", expertsId);
	jObj.addProperty("evaluationId", eMeasureId);

	HttpResponse res = new HttpResponse(jObj.toString());
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

    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.TEXT_PLAIN)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Success") })
    @Summary("Returns the id of the expert collection, id of evaluation metrics and id of the visualization")

    public HttpResponse applyAlgorithm(@PathParam("datasetId") String datasetId, @PathParam("algorithmName") String algorithmName,
	    @ContentParam String query, @QueryParam(name = "evaluation", defaultValue = "false") boolean isEvaluation,
	    @QueryParam(name = "visualization", defaultValue = "true") boolean isVisualization,
	    @QueryParam(name = "alpha", defaultValue = "0.15d") String alpha, @QueryParam(name = "intra", defaultValue = "0.6") String intraWeight) {

	ERSBundle properties = new ERSBundle.Builder(datasetId, query, algorithmName).alpha(alpha).intraWeight(intraWeight).isEvaluation(false)
		.isVisualization(true).build();

	ScoreStrategy strategy = null;
	ScoringContext scontext = null;
	try {

	    // JDK 7 offers switch on strings instead of creating enums.
	    switch (algorithmName == null ? "" : algorithmName) {
	    case AlgorithmType.PAGE_RANK:
		log.info("Applying PageRank strategy...");
		strategy = new PageRankStrategy(properties);
		break;
	    case AlgorithmType.HITS:
		log.info("Applying HITS strategy...");
		strategy = new HITSStrategy(properties);
		break;
	    case AlgorithmType.CA_PR:
		log.info("Applying community Aware PageRank strategy...");
		strategy = new CommunityAwarePageRankStrategy(properties);
		break;
	    case AlgorithmType.CA_HITS:
		log.info("Applying community Aware HITS strategy...");
		strategy = new CommunityAwareHITSStrategy(properties);
		break;
	    default:
		log.info("Applying default strategy...");
		strategy = new PageRankStrategy(properties);
		break;
	    }
	} catch (ERSException e) {
	    HttpResponse res = new HttpResponse(e.getMessage());
	    res.setStatus(200);
	    return res;
	}
	scontext = new ScoringContext(strategy);
	scontext.executeStrategy();
	scontext.saveResults();

	JsonObject jObj = new JsonObject();
	jObj.addProperty("expertsId", strategy.getExpertsId());
	jObj.addProperty("evaluationId", strategy.getEvaluationId());
	jObj.addProperty("visualizationId", -1);

	scontext.close();

	HttpResponse res = new HttpResponse(jObj.toString());
	res.setStatus(200);
	return res;
    }

    @GET
    @Path("datasets/{datasetId}/experts/{expertsCollectionId}/expert/{expertId}/tags")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.TEXT_PLAIN)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 401, message = "Unauthorized") })
    @Summary("Returns tags associated with experts for the specific post.")
    public HttpResponse getTags(@PathParam("datasetId") String datasetId, @PathParam("expertsCollectionId") String expertCollectionId,
	    @PathParam("expertId") String expertId) {

	String databaseName = getDatabaseName(datasetId);
	if (databaseName == null) {
	    try {
		throw new ERSException(ExceptionMessages.DATABASE_NOT_FOUND);
	    } catch (ERSException e) {
		e.printStackTrace();
	    }
	}

	DatabaseHandler dbHandler = null;
	dbHandler = new DatabaseHandler(databaseName, "root", "");

	TagExtractor extractor = new TagExtractor(dbHandler, expertCollectionId, expertId);

	HttpResponse res = new HttpResponse(extractor.getTags());
	res.setStatus(200);
	return res;
    }

    @GET
    @Path("datasets/{datasetId}/experts/{expertsCollectionId}/expert/{expertId}/posts")

    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.TEXT_PLAIN)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Success") })
    @Summary("Returns the related posts of the expert user.")

    public HttpResponse getPosts(@PathParam("datasetId") String datasetId, @PathParam("expertsCollectionId") String expertCollectionId,
	    @PathParam("expertId") String expertId) {

	String databaseName = getDatabaseName(datasetId);
	if (databaseName == null) {
	    try {
		throw new ERSException(ExceptionMessages.DATABASE_NOT_FOUND);
	    } catch (ERSException e) {
		e.printStackTrace();
	    }
	}

	DatabaseHandler dbHandler = null;
	dbHandler = new DatabaseHandler(databaseName, "root", "");

	log.info("EXTRACTOR STARTED...");
	RelatedPostsExtractor extractor = new RelatedPostsExtractor(dbHandler, expertCollectionId, expertId);

	HttpResponse res = new HttpResponse(extractor.getPosts());
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
	log.info(fileContentsString);

	HttpResponse res = new HttpResponse(fileContentsString, 200);
	res.setHeader("content-type", "text/xml");

	return res;
    }

    @POST
    @Path("datasets/{datasetId}/semantics")

    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.TEXT_PLAIN)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Success") })
    @Summary("Adds semantic tags to the text.")

    public HttpResponse addSemantics(@PathParam(value = "datasetId") String datasetId) {

	String dbName = getDatabaseName(datasetId);
	DatabaseHandler dbHandler = new DatabaseHandler(dbName, "root", "");

	HttpResponse res;

	log.info("Executing semantics...");

	dbHandler.addSemanticTags();

	log.info("Executing semantics finished");

	res = new HttpResponse("Added Semantics...", 200);

	return res;
    }

    /**
     * This method is called only once when the new dataset is added. Currently,
     * it is invoked from the test case.
     * 
     * @param dataset_name
     *            Name of the dataset, whose data has to be indexed. Dataset is
     *            present in datasets directory.
     * @return 200 response code, if database operations and indexing succeeds.
     *         500 response code, if exception occurs.
     */
    @POST
    @Path("indexer")

    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.TEXT_PLAIN)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Success") })
    @Summary("Indexes the text retrieved from the database.")

    public HttpResponse prepareData(@ContentParam String dataset_name, @QueryParam(name = "inputFormat", defaultValue = "xml") String inputType) {
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

	    // if (inputType.equalsIgnoreCase("xml")) {
	    // ERSXmlParser xmlparser = new ERSXmlParser(dirPath);
	    // dbHandler.addPosts(xmlparser.getPosts());
	    // dbHandler.addUsers(xmlparser.getUsers());
	    // } else {
	    //
	    // ERSCSVParser csvparser = new ERSCSVParser(dirPath);
	    //
	    // dbHandler.addPosts(csvparser.getPosts());
	    // List<UserCSV> users = csvparser.getUsers();
	    // log.info("CSV Parser started..." + users.size());
	    //
	    // if (users != null && users.size() > 0) {
	    // dbHandler.addUsers(users);
	    // }
	    // }
	    //
	    // // Add semantics tag.
	    // dbHandler.addSemanticTags();
	    // dbHandler.markExpertsForEvaluation(connectionSrc);

	    // log.info("Database operations completed...");

	    LuceneMysqlIndexer indexer = new LuceneMysqlIndexer(dbHandler.getConnectionSource(), dataset_name + "_index");
	    log.info("Building index...");
	    indexer.buildIndex();

	    res = new HttpResponse("Indexer finished successfully");
	    res.setStatus(200);

	} catch (SQLException e) {
	    log.info("SQL EXCEPTION......" + e);
	    e.printStackTrace();
	    res = new HttpResponse("SQL Exception");
	    res.setStatus(500);
	} catch (Exception e) {
	    e.printStackTrace();
	    log.info("IO exception " + e);
	}

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
	StopWordRemover remover = new StopWordRemover(text, "en");

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

    @POST
    @Path("markExperts")
    public HttpResponse test() {

	DatabaseHandler dbHandler = new DatabaseHandler("cs", "root", "");
	try {
	    dbHandler.markExpertsForEvaluation(dbHandler.getConnectionSource());
	} catch (SQLException e) {
	    e.printStackTrace();
	}

	HttpResponse res = new HttpResponse("maked experts");
	res.setStatus(200);

	return res;
    }

    @POST
    @Path("markPostType")
    public HttpResponse markPostType() {

	DatabaseHandler dbHandler = new DatabaseHandler("nature", "root", "");
	dbHandler.markPostType();

	HttpResponse res = new HttpResponse("marked post type...");
	res.setStatus(200);

	return res;
    }

    @POST
    @Path("saveReplies")
    public HttpResponse saveReplies() {

	DatabaseHandler dbHandler = new DatabaseHandler("nature", "root", "");
	dbHandler.saveNoOfRepliesByUser();

	HttpResponse res = new HttpResponse("Saved replies...");
	res.setStatus(200);

	return res;
    }

    @POST
    @Path("markNatureExperts")
    public HttpResponse markExperts() {

	DatabaseHandler dbHandler = new DatabaseHandler("nature", "root", "");
	try {
	    dbHandler.markExpertsForEvaluationFromReplies(dbHandler.getConnectionSource());
	} catch (SQLException e) {
	    e.printStackTrace();
	}

	HttpResponse res = new HttpResponse("Marked experts...");
	res.setStatus(200);

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

    /**
     * 
     * @param datasetId
     *            An id identifying the dataset. Ids are stored in a database
     *            called ersdb.
     * 
     * @return Returns the database name associated with a particular dataset.
     */
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

    /**
     * Simple function to validate a user login.
     * Basically it only serves as a "calling point" and does not really
     * validate a user
     * (since this is done previously by LAS2peer itself, the user does not
     * reach this method
     * if he or she is not authenticated).
     * 
     */
    @POST
    @Path("validate")
    @Produces(MediaType.TEXT_XML)
    @ResourceListApi(description = "User validation")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 401, message = "Unauthorized") })
    @Summary("Simple function to validate a user login.")
    public void validateLogin(@QueryParam(name = "username", defaultValue = "anon") String username) {

	DatabaseHandler dbHandler = new DatabaseHandler("userAccount", "root", "");

	log.info("USRNAME:: " + username);
	try {
	    TableUtils.createTableIfNotExists(dbHandler.getConnectionSource(), UserAccEntity.class);
	} catch (SQLException e) {
	    e.printStackTrace();
	}

	dbHandler.addUser(username);
	log.info(username);

    }

    @POST
    @Path("datasets/{datasetId}/position")
    public void saveClickPositions(@PathParam(value = "datasetId") String datasetId,
	    @QueryParam(name = "expertsId", defaultValue = "-1") String expertsId, @QueryParam(name = "position", defaultValue = "-1") int position) {

	String databaseName = getDatabaseName(datasetId);
	if (databaseName == null) {
	    // Throw custom exception.
	}

	DatabaseHandler dbHandler = null;
	dbHandler = new DatabaseHandler(databaseName, "root", "");

	// log.info("USRNAME:: " + username);
	try {
	    TableUtils.createTableIfNotExists(dbHandler.getConnectionSource(), UserClickDetails.class);
	} catch (SQLException e) {
	    e.printStackTrace();
	}

	dbHandler.saveClickPositions(expertsId, position);
    }

    @POST
    @Path("datasets/{datasetId}/skillDistribution")
    public HttpResponse createSkillDistribution(@PathParam(value = "datasetId") String datasetId) {

	String dbName = getDatabaseName(datasetId);
	DatabaseHandler dbHandler = new DatabaseHandler(dbName, "root", "");

	HttpResponse res;
	dbHandler.createTagDistribution();

	res = new HttpResponse("Workerd fine...", 200);

	return res;
    }

    // ////////////////////////////////////////////////////////////////
    // /////// SWAGGER
    // ////////////////////////////////////////////////////////////////

    @GET
    @Path("api-docs")
    @Produces(MediaType.APPLICATION_JSON)
    public HttpResponse getSwaggerResourceListing() {
	return RESTMapper.getSwaggerResourceListing(this.getClass());
    }

    @GET
    @Path("api-docs/{tlr}")
    @Produces(MediaType.APPLICATION_JSON)
    public HttpResponse getSwaggerApiDeclaration(@PathParam("tlr") String tlr) {
	// return RESTMapper.getSwaggerApiDeclaration(this.getClass(), tlr,
	// "http://127.0.0.1:8080/ocd/");
	return RESTMapper.getSwaggerApiDeclaration(this.getClass(), tlr, "https://api.learning-layers.eu/ocd/");
    }

}
