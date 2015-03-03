package i5.las2peer.services.servicePackage;

import i5.las2peer.api.Service;
import i5.las2peer.restMapper.HttpResponse;
import i5.las2peer.restMapper.RESTMapper;
import i5.las2peer.restMapper.annotations.ContentParam;
import i5.las2peer.restMapper.annotations.POST;
import i5.las2peer.restMapper.annotations.Path;
import i5.las2peer.restMapper.annotations.Version;
import i5.las2peer.restMapper.tools.ValidationResult;
import i5.las2peer.restMapper.tools.XMLCheck;
import i5.las2peer.services.servicePackage.database.Data;
import i5.las2peer.services.servicePackage.database.MySqlHelper;
import i5.las2peer.services.servicePackage.database.UserEntity;
import i5.las2peer.services.servicePackage.evaluation.ElevenPointInterpolatedAveragePrecision;
import i5.las2peer.services.servicePackage.evaluation.MeanAveragePrecision;
import i5.las2peer.services.servicePackage.evaluation.Precision;
import i5.las2peer.services.servicePackage.evaluation.PrecisionRecall;
import i5.las2peer.services.servicePackage.evaluation.Recall;
import i5.las2peer.services.servicePackage.graph.GraphWriter;
import i5.las2peer.services.servicePackage.graph.JUNGGraphCreator;
import i5.las2peer.services.servicePackage.scoring.HITSStrategy;
import i5.las2peer.services.servicePackage.scoring.PageRankStrategy;
import i5.las2peer.services.servicePackage.scoring.ScoringContext;
import i5.las2peer.services.servicePackage.semanticTagger.SemanticTagger;
import i5.las2peer.services.servicePackage.textProcessor.PorterStemmer;
import i5.las2peer.services.servicePackage.textProcessor.StopWordRemover;
import i5.las2peer.services.servicePackage.utils.Global;
import i5.las2peer.services.servicePackage.visualization.GraphMl2GEXFConverter;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.base.Stopwatch;
import com.google.common.collect.HashMultiset;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

/**
 * @author sathvik
 */

@Path("recommender_framework")
@Version("0.1")
public class ExpertRecommenderService extends Service {

	public ExpertRecommenderService() {
		// read and set properties values
		// IF THE SERVICE CLASS NAME IS CHANGED, THE PROPERTIES FILE NAME NEED
		// TO BE CHANGED TOO!
		setFieldValues();

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

	@POST
	@Path("modeling")
	public HttpResponse modelExperts(@ContentParam String text) {
		// ExpertUtils utils = new ExpertUtils();
		String query = text;

		Stopwatch timer = Stopwatch.createStarted();
		// TODO: Semantic analysis of the text.
		StopWordRemover remover = new StopWordRemover(query);
		String cleanstr = remover.getPlainText();

		Global.QUERY_WORDS = HashMultiset.create(Splitter
				.on(CharMatcher.WHITESPACE).omitEmptyStrings().split(cleanstr));

		// utils.setQuery(HashMultiset.create(Splitter.on(CharMatcher.WHITESPACE)
		// .omitEmptyStrings().split(cleanstr)));

		SemanticTagger tagger = new SemanticTagger(text);

		// TODO:Handle this better, Splitting can be avoided here.
		Global.QUERY_ENTITIES = HashMultiset.create(Splitter.on(",")
				.omitEmptyStrings().split(tagger.getTags().getTags()));

		String expert_posts = "{}";

		try {
			ConnectionSource connSrc = MySqlHelper
					.createConnectionSource("healthcare");
			MySqlHelper.createUserMap(connSrc);

			MySqlHelper.createTermFreqMap(connSrc);
			MySqlHelper.createSemanticTagFreqMap(connSrc);

			Global.createInverseResFreqMap();
			Global.createIEFMap();

			expert_posts = Global.rankTheResources(0.5);
		} catch (SQLException e) {

			e.printStackTrace();
			HttpResponse res = new HttpResponse(
					"Some error occured on the server, Please contact the developer..."
							+ e);
			res.setStatus(404);
			return res;
		}

		System.out.println("Total time " + timer.stop());
		HttpResponse res = new HttpResponse(expert_posts);
		res.setStatus(200);
		return res;
	}

	@POST
	@Path("pagerank")
	public HttpResponse applyPageRank(@ContentParam String text) {

		String query = text;
		StopWordRemover remover = null;
		String cleanstr = null;
		String expert_posts = "{}";
		Stopwatch timer = null;
		try {
			// timer = Stopwatch.createStarted();
			// TODO: Semantic analysis of the text.
			remover = new StopWordRemover(query);
			cleanstr = remover.getPlainText();

			Global.QUERY_WORDS = HashMultiset.create(Splitter
					.on(CharMatcher.WHITESPACE).omitEmptyStrings()
					.split(cleanstr));

		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("TRYING TO CONNECT TO DB");

		try {
			ConnectionSource connSrc = MySqlHelper
					.createConnectionSource("healthcare");
			MySqlHelper.createUserMap(connSrc);
			MySqlHelper.createTermFreqMap(connSrc);
		} catch (SQLException e) {

			e.printStackTrace();
			HttpResponse res = new HttpResponse(
					"Some error occured on the server, Please contact the developer..."
							+ e);
			res.setStatus(404);
			return res;
		}

		Global.createFilteredQnAMap();

		JUNGGraphCreator jcreator = new JUNGGraphCreator();
		jcreator.createGraph(Global.q2a1, Global.postId2userId1);

		System.out.println("Applying Pagerank...");

		PageRankStrategy strategy = new PageRankStrategy(jcreator.getGraph());
		ScoringContext scontext = new ScoringContext(strategy);
		scontext.executeStrategy();

		expert_posts = scontext.getExperts();

		System.out.println("Calculating Precision....");
		try {
			Precision precision = new Precision(scontext.getExpertMap(), 40);
			System.out.println("PRECISION ::" + precision.getValue());

			double value = precision.getAveragePrecision();
			precision.saveAvgPrecisionToFile();

			precision.savePrecisionValuesToFile();

			Recall recall = new Recall(scontext.getExpertMap(), 40);
			recall.calculateValuesAtEveryPosition();
			recall.saveRecallValuesToFile();

			PrecisionRecall precision_recall = new PrecisionRecall(
					precision.getRoundedValues(), recall.getRoundedValues());
			precision_recall.savePrecisionRecallCSV1();

			System.out.println("AVG PRECISION ::" + value);
			MeanAveragePrecision MAP = new MeanAveragePrecision();
			System.out.println("MEAN AVG PRECISION ::" + MAP.getValue());

			ElevenPointInterpolatedAveragePrecision epap = new ElevenPointInterpolatedAveragePrecision();
			epap.calculateInterPrecisionValues(recall.getRoundedValues(),
					precision.getRoundedValues());
			epap.save();

		} catch (Exception e) {
			e.printStackTrace();
		}

		GraphWriter writer = new GraphWriter(jcreator);
		writer.saveToGraphMl("fitness_graph_jung.graphml");

		// System.out.println("Total time " + timer.stop());
		HttpResponse res = new HttpResponse(expert_posts);
		res.setStatus(200);
		return res;
	}

	@POST
	@Path("hits")
	public HttpResponse applyHITS(@ContentParam String text) {

		String query = text;

		Stopwatch timer = Stopwatch.createStarted();
		// TODO: Semantic analysis of the text.
		StopWordRemover remover = new StopWordRemover(query);
		String cleanstr = remover.getPlainText();

		Global.QUERY_WORDS = HashMultiset.create(Splitter
				.on(CharMatcher.WHITESPACE).omitEmptyStrings().split(cleanstr));
		String expert_posts = "{}";

		try {
			ConnectionSource connSrc = MySqlHelper
					.createConnectionSource("healthcare");
			MySqlHelper.createUserMap(connSrc);
			MySqlHelper.createTermFreqMap(connSrc);
		} catch (SQLException e) {

			e.printStackTrace();
			HttpResponse res = new HttpResponse(
					"Some error occured on the server, Please contact the developer..."
							+ e);
			res.setStatus(404);
			return res;
		}

		Global.createFilteredQnAMap();

		JUNGGraphCreator jcreator = new JUNGGraphCreator();
		jcreator.createGraph(Global.q2a1, Global.postId2userId1);

		System.out.println("Applying HITS...");

		HITSStrategy strategy = new HITSStrategy(jcreator.getGraph());

		ScoringContext scontext = new ScoringContext(strategy);
		scontext.executeStrategy();

		expert_posts = scontext.getExperts();

		System.out.println("Total time " + timer.stop());
		HttpResponse res = new HttpResponse(expert_posts);
		res.setStatus(200);
		return res;
	}

	@POST
	@Path("community_aware_rank")
	public HttpResponse applyCommunityAwareRank(@ContentParam String text) {

		String query = text;

		Stopwatch timer = Stopwatch.createStarted();
		// TODO: Semantic analysis of the text.
		StopWordRemover remover = new StopWordRemover(query);
		String cleanstr = remover.getPlainText();

		Global.QUERY_WORDS = HashMultiset.create(Splitter
				.on(CharMatcher.WHITESPACE).omitEmptyStrings().split(cleanstr));
		String expert_posts = "{}";

		try {
			ConnectionSource connSrc = MySqlHelper
					.createConnectionSource("healthcare");
			MySqlHelper.createUserMap(connSrc);
			MySqlHelper.createTermFreqMap(connSrc);
		} catch (SQLException e) {

			e.printStackTrace();
			HttpResponse res = new HttpResponse(
					"Some error occured on the server, Please contact the developer..."
							+ e);
			res.setStatus(404);
			return res;
		}

		Global.createFilteredQnAMap();

		JUNGGraphCreator jcreator = new JUNGGraphCreator();
		jcreator.createGraph(Global.q2a1, Global.postId2userId1);

		GraphWriter writer = new GraphWriter(jcreator);
		writer.saveToGraphMl("fitness_graph_jung.graphml");

		System.out.println("Created Graph ans saved it... ");

		System.out.println("Total time " + timer.stop());
		HttpResponse res = new HttpResponse(expert_posts);
		res.setStatus(200);
		return res;
	}

	@POST
	@Path("visulaizer")
	public HttpResponse visualize(@ContentParam String text) {

		System.out.println("Visualizing...");

		// Converting graphml format to gexf format.
		GraphMl2GEXFConverter converter = new GraphMl2GEXFConverter();
		try {
			converter.convert("fitness_graph_jung.graphml");
			converter.applyLayout();
			converter.export("fitness_graph_jung");

		} catch (IOException e) {
			e.printStackTrace();
		}

		HttpResponse res = new HttpResponse("Everything went good...");
		res.setStatus(200);
		return res;
	}

	@POST
	@Path("DbPreparer")
	public HttpResponse prepareDefaultData() {

		try {
			JAXBContext context = JAXBContext
					.newInstance(i5.las2peer.services.servicePackage.xmlparsers.Resources.class);
			Unmarshaller um = context.createUnmarshaller();
			File file = new File("res/posts.xml");

			i5.las2peer.services.servicePackage.xmlparsers.Resources resources = (i5.las2peer.services.servicePackage.xmlparsers.Resources) um
					.unmarshal(file);
			List<i5.las2peer.services.servicePackage.xmlparsers.Resource> resources_list = (ArrayList) resources
					.getResources();

			System.out.println("" + resources_list);

			MySqlHelper.createDatabase("healthcare");
			ConnectionSource connectionSrc = MySqlHelper
					.createConnectionSource("healthcare");

			// Create Data table.
			TableUtils.createTableIfNotExists(connectionSrc, Data.class);
			MySqlHelper.createAndInsertResourceDAO(resources_list,
					connectionSrc);

			// Create User table.
			context = JAXBContext
					.newInstance(i5.las2peer.services.servicePackage.xmlparsers.Users.class);
			File users_file = new File("res/Users.xml");
			Unmarshaller um1 = context.createUnmarshaller();

			System.out.println("Creating User table...");
			i5.las2peer.services.servicePackage.xmlparsers.Users users = (i5.las2peer.services.servicePackage.xmlparsers.Users) um1
					.unmarshal(users_file);
			List<i5.las2peer.services.servicePackage.xmlparsers.User> user_list = (ArrayList) users
					.getUsersList();

			System.out.println("Inserting into User table...");
			TableUtils.createTableIfNotExists(connectionSrc, UserEntity.class);
			MySqlHelper.createAndInsertUserDAO(user_list, connectionSrc);

			MySqlHelper.markExpertsForEvaluation(connectionSrc);

			// Create Semantics table and insert data.
			// TableUtils.createTableIfNotExists(connectionSrc,
			// SemanticTagEntity.class);
			// MySqlHelper.createAndInsertSemanticTags(connectionSrc);

			connectionSrc.close();

			System.out.println("Inserting into user table completed...");
		} catch (Exception e) {
			e.printStackTrace();
			HttpResponse res = new HttpResponse("Something went wrong..");
			res.setStatus(200);
			return res;
		}

		HttpResponse res = new HttpResponse("Everything went good...");
		res.setStatus(200);
		return res;

	}

	// TODO:Complete the functionalities.
	// @POST
	// @Path("recommender/DbPreparer")
	// public HttpResponse prepareExternalData(@ContentParam String databaseurl,
	// @ContentParam String filepath) {
	// System.out.println(filepath);
	//
	// String fileurl =
	// "https://www.dropbox.com/s/p85acz5qweoyg3s/fitness_users.csv?dl=1";
	//
	// String tablename = fileurl.substring(fileurl.lastIndexOf("/"),
	// fileurl.lastIndexOf("."));
	// ExpertUtils utils = new ExpertUtils();
	// MySqlConnector connector = null;
	// try {
	// connector = new MySqlConnector(utils);
	// connector.createTable(tablename);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	//
	// // TODO: Parse XML file.
	//
	// System.out.println("Preparing Db...");
	// HttpResponse res = new HttpResponse("Database created...");
	// res.setStatus(200);
	// return res;
	// }

	// @POST
	// @Path("recommender/usermodeling")
	// public HttpResponse userModelExperts(@ContentParam String text) { //
	// TODO:
	// // Change
	// // method
	// // name.
	// ExpertUtils utils = new ExpertUtils();
	// String query = text;
	//
	// Stopwatch timer = Stopwatch.createStarted();
	// // TODO: Semantic analysis of the text.
	// StopWordRemover remover = new StopWordRemover(query);
	// String cleanstr = remover.getPlainText();
	// utils.setQuery(HashMultiset.create(Splitter.on(CharMatcher.WHITESPACE)
	// .omitEmptyStrings().split(cleanstr)));
	//
	// // TODO Change the name.
	// String expert_posts = "{}";
	// MySqlConnector connector = null;
	// try {
	// connector = new MySqlConnector(utils);
	// connector.loadUsers();
	//
	// connector.calculateResProps();
	// expert_posts = utils.applyUserModelingStrategy();
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// HttpResponse res = new HttpResponse(
	// "Some error occured on the server, Please contact the developer..."
	// + e);
	// res.setStatus(404);
	// return res;
	// }
	//
	// System.out.println("Total time " + timer.stop());
	// HttpResponse res = new HttpResponse(expert_posts);
	// res.setStatus(200);
	// return res;
	// }

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

}
