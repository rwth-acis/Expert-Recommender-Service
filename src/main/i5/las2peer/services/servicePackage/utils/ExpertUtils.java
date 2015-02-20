package i5.las2peer.services.servicePackage.utils;

import i5.las2peer.services.servicePackage.graph.JUNGGraphCreator;
import i5.las2peer.services.servicePackage.graph.RelationshipEdge;
import i5.las2peer.services.servicePackage.models.Resource;
import i5.las2peer.services.servicePackage.scoring.HITSStrategy;
import i5.las2peer.services.servicePackage.scoring.PageRankStrategy;
import i5.las2peer.services.servicePackage.scoring.ScoringContext;
import i5.las2peer.services.servicePackage.scoring.UserModelingStrategy;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.gson.Gson;

import edu.uci.ics.jung.io.GraphMLWriter;

/**
 * @author sathvik TODO:// Get rid of this entire class.
 * 
 */

public class ExpertUtils {
	public HashMultiset QUERY_WORDS;

	public HashMultimap<String, Resource> TERM_FREQ_MAP = HashMultimap.create();
	public HashMultimap<String, Resource> IRF_FREQ_MAP = HashMultimap.create();
	public Map<String, Double> TFIRF_MAP = new HashMap<String, Double>();

	public HashMultimap<String, String> parentId2postId = HashMultimap.create();
	public HashMap<String, String> postId2userId = new HashMap<String, String>();
	public HashMap<Object, Integer> totalWordFreq = new HashMap<Object, Integer>();
	public int THRESHOLD_WORD_FREQ = 0;

	public HashMap<String, Set<String>> q2a = new HashMap<String, Set<String>>();
	public JUNGGraphCreator jcreator;

	public double totalNoOfResources = 0;

	public ExpertUtils() {

	}

	public void setQuery(HashMultiset text) {
		QUERY_WORDS = text;
	}

	public void setEntites(HashMultiset text) {
		Global.QUERY_ENTITIES = text;
	}

	public void printMap(Map<String, Double> map) {
		Iterator it = map.entrySet().iterator();

		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			System.out.println(pairs.getKey() + " = " + pairs.getValue());
			// it.remove(); // avoids a ConcurrentModificationException
		}

		System.out.println("size:: " + totalWordFreq.size());
	}

	public void printMultiMap(Multimap map, String filename) {
		Gson gson = new Gson();
		if (filename != null && filename.length() > 0) {
			prinToFile(filename, gson.toJson(map.asMap()));
		} else {
			System.out.println("JSON STR " + gson.toJson(map.asMap()));
		}

		// println(word + "::" + count);
		// printHashMap(totalWordFreq);
	}

	public void prinToFile(String filename, String text) {
		PrintWriter out = null;
		try {
			out = new PrintWriter(filename);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		out.println(text);
		out.close();
	}

	public double round(double value, int places) {
		if (places < 0)
			throw new IllegalArgumentException();

		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}

	public <K, V extends Comparable<? super V>> LinkedHashMap<K, V> sortByValue(
			Map<K, V> map) {
		List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
			@Override
			public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});

		LinkedHashMap<K, V> result = new LinkedHashMap<>();
		for (Map.Entry<K, V> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}

	public <K, V extends Comparable<? super V>> Map<K, V> sortByObjValue(
			Map<K, V> map) {
		List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
			@Override
			public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});

		Map<K, V> result = new LinkedHashMap<>();
		for (Map.Entry<K, V> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}

	public String applyPageRank() {
		System.out.println("PageRank started");

		ScoringContext scontext = new ScoringContext(new PageRankStrategy(
				jcreator.getGraph()));
		return scontext.executeStrategy();
	}

	public String applyHITS() {
		ScoringContext scontext = new ScoringContext(new HITSStrategy(
				jcreator.getGraph()));
		return scontext.executeStrategy();
	}

	public String applyUserModelingStrategy() {
		ScoringContext scontext = new ScoringContext(new UserModelingStrategy());
		return scontext.executeStrategy();
	}

	// public void createJUNGGraph(MySqlConnector connector) {
	// jcreator = new JUNGGraphCreator();
	// // Logger log = LoggerFactory.getLogger(ExpertUtils.class);
	// // log.info("Creating JUNG graph...");
	//
	// Stopwatch timer = Stopwatch.createStarted();
	// System.out.println("Creating graph...");
	//
	// for (String key : q2a.keySet()) {
	//
	// // System.out.println("Key::" + key);
	// String q_user_id = postId2userId.get(key);
	// User user;
	// user = Global.userId2userObj.get(q_user_id);
	// if (q_user_id != null && user != null) {
	// user.setRelatedPost(key);
	// Set termObjs = TERM_FREQ_MAP.get(key);
	// if (termObjs.size() > 0) {
	// Resource res = (Resource) termObjs.iterator().next();
	// user.setTitle(res.getText());
	// } else {
	// user.setTitle("Title is empty");
	// }
	// }
	//
	// if (q_user_id != null) {
	// // System.out.println("post " + key + " userid " + q_user_id);
	// jcreator.createVertex(q_user_id);
	// Set<String> values = q2a.get(key);
	//
	// for (String value : values) {
	// String a_user_id = postId2userId.get(value);
	// user = Global.userId2userObj.get(a_user_id);
	// if (a_user_id != null) {
	// if (Global.userId2userObj.get(a_user_id) != null) {
	// user.setRelatedPost(value);
	//
	// Set termObjs = TERM_FREQ_MAP.get(value);
	// if (termObjs.size() > 0) {
	// Resource res = (Resource) termObjs.iterator()
	// .next();
	// user.setTitle(res.getText());
	//
	// } else {
	// user.setTitle("Title is empty");
	// }
	// }
	// jcreator.createVertex(a_user_id);
	// jcreator.createEdge(q_user_id, a_user_id, value);
	// }
	// }
	// }
	// }
	// System.out.println("Graph created...");
	//
	// System.out.println("Graph created...");
	// System.out.println("Graph Creation Time... " + timer.stop());
	// // save2JungGraphML("fitness_graph_jung.graphml");
	// }

	// TODO:Imp, Check why GraphMLWriter is not working.
	public void save2JungGraphML(String filename) {
		GraphMLWriter<String, RelationshipEdge> graphWriter = new GraphMLWriter<String, RelationshipEdge>();

		graphWriter.addEdgeData("label", "label for an edge", "0", null);

		// graphWriter.setEdgeData(jcreator.getGraph().getEndpoints(arg0));
		PrintWriter out = null;
		try {
			out = new PrintWriter(new BufferedWriter(new FileWriter(filename)));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// try {
		// graphWriter.save(jcreator.getGraph(), out);
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
	}

	// public void addTermFreq(String postid, String text, String parentId,
	// String userid) {
	// Multiset<String> bagOfWords = HashMultiset.create(Splitter
	// .on(CharMatcher.WHITESPACE).omitEmptyStrings().split(text));
	//
	// int totalcount = 0;
	//
	// Resource resource;
	// int count = 0;
	// // Iterate the Query terms.
	// // Ignore if freq is too less.
	// for (Object word : QUERY_WORDS.elementSet()) {
	// count = bagOfWords.count(word);
	//
	// totalcount = totalWordFreq.get((String) word) != null ? totalWordFreq
	// .get((String) word) + count
	// : 0;
	// totalWordFreq.put((String) word, totalcount);
	//
	// if (count > THRESHOLD_WORD_FREQ) {
	// resource = new Resource(new Integer(postid), text);
	// resource.setTerm((String) word);
	// resource.setTermFreq(count);
	// resource.setParentId(parentId);
	// TERM_FREQ_MAP.put(postid, resource);
	// if (parentId != null) {
	// parentId2postId.put(parentId, postid);
	// }
	// if (userid != null) {
	// postId2userId.put(postid, userid);
	// }
	// }
	// }
	//
	// }

	public void calPopAndCosSimilarity(String postid, String text,
			String userid, String noOfVotes, String noOfComments,
			String last_edit_date) throws ParseException {
		Multiset<String> bagOfWords = HashMultiset.create(Splitter
				.on(CharMatcher.WHITESPACE).omitEmptyStrings().split(text));

		Resource resource = null;
		double resTermCount = 0;
		double queryTermCount = 0;

		double popularity = 0;
		double cosine_sim = 0;

		// TODO, Can use Hashmap to maintain the map from String to its count.
		// Check if it is useful.
		List<Double> queryVector = new ArrayList<Double>();
		List<Double> resVector = new ArrayList<Double>();

		if (noOfVotes != null && noOfComments != null) {
			popularity = Double.parseDouble(noOfVotes)
					+ Double.parseDouble(noOfComments);
		}

		boolean isQueryTermAvailable = false;
		// Iterate the Query terms.
		// Ignore if freq is too less.
		for (Object word : QUERY_WORDS.elementSet()) {
			resTermCount = bagOfWords.count(word);
			resVector.add(resTermCount);

			if (resTermCount > 0) {
				isQueryTermAvailable = true; // Will be used to filter our the
												// resource that is not
												// required. can be done using
												// cosine sim, do it later.
			}

			queryTermCount = QUERY_WORDS.count(word);
			queryVector.add(queryTermCount);

		}

		// TODO:Separate this resource model with other resource model. That is
		// a term resource, This is entire post resource.
		resource = new Resource(new Integer(postid), text);
		resource.setPopularity(popularity);

		// Convert last edit date string to long unix time
		if (last_edit_date != null) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date parsedDate = dateFormat.parse(last_edit_date);
			long edit_date = parsedDate.getTime();

			resource.setLastEditDate(edit_date);
		}

		if (resource != null && resVector.size() > 0 && queryVector.size() > 0) {
			cosine_sim = CosineSimilarity.calculateCosineSimilarity(
					queryVector, resVector);
			resource.setCosineSimilarity(cosine_sim);
		}

		if (isQueryTermAvailable) {
			Global.postid2Resource.put(Long.parseLong(postid), resource);
			if (userid != null) {
				Global.postId2userId1.put(Long.parseLong(postid),
						Long.parseLong(userid));
			}
		}

	}

	public void createIRFMap() {
		for (Map.Entry entry : TERM_FREQ_MAP.entries()) {
			String postid = (String) entry.getKey();
			Set<Resource> resources = (Set<Resource>) TERM_FREQ_MAP.get(postid);
			double sum_tfirf = 0; // TFIRF of individual resource combined, for
									// the entire post.
			for (Resource res : resources) {
				int termFreq = res.getTermFreq();

				double irfweight = round(
						(double) Math.log(totalNoOfResources
								/ totalWordFreq.get(res.getTerm())), 2);
				sum_tfirf = sum_tfirf + (termFreq * irfweight);

				res.setTermFreqInverseResFreq(termFreq * irfweight);
				IRF_FREQ_MAP.put(postid, res);
				// parentId2postId.put(postid, res.getParentId());
			}
			TFIRF_MAP.put(postid, sum_tfirf);

		}

		// printMultiMap(IRF_FREQ_MAP,"irf.txt");
		// printTermFreqMap();

		// To sort tfirf map.
		// SORTED_TFIRF_MAP = sortByValue(TFIRF_MAP);
		// printMap(SORTED_TFIRF_MAP);
		// System.out.println("Size::"+SORTED_TFIRF_MAP.size());
		// printImmutableSortedMap(SORTED_TFIRF_MAP);

	}

	// TODO:Create Entity freq map
	// public void addEntityFreq(String postid, String annotations) {
	//
	// if (annotations != null) {
	//
	// // TODO:Use mapper to map from json to the class, instead of parsing
	// JsonParser parser = new JsonParser();
	// JsonArray jarr = (JsonArray) parser.parse(annotations);
	// JsonArray tagArr = null;
	// double confidenceVal = 0;
	// EntityResource entResource;
	//
	// Global.entities = HashMultiset.create();
	// HashMap<String, EntityResource> entity2entityRes = new HashMap<String,
	// EntityResource>();
	// for (int i = 0; i < jarr.size(); i++) {
	// JsonElement obj = ((JsonObject) jarr.get(i)).get("tags");
	// // System.out.println(postid + " " + obj);
	// if (obj != null && obj.isJsonNull() == false) {
	// tagArr = obj.getAsJsonArray();
	// for (JsonElement elem : tagArr) {
	// String entity = elem.getAsString();
	// Global.entities.add(entity);
	//
	// entResource = new EntityResource(new Integer(postid),
	// annotations);
	// entResource.setConfidenceVal(confidenceVal);
	// entResource.setEntity(entity);
	// if (entity2entityRes.containsKey(entity) == false) {
	// entity2entityRes.put(entity, entResource);
	// } else {
	// EntityResource temp = entity2entityRes.get(entity);
	// temp.setEntityFreq(temp.getEntityFreq() + 1);
	// }
	// }
	// confidenceVal = ((JsonObject) jarr.get(i)).get("rho")
	// .getAsDouble();
	// }
	// }
	//
	// // Iterate the Query entities and create a map for only required
	// // entities.
	// int totalcount = 0;
	// for (String query_entity : Global.QUERY_ENTITIES.elementSet()) {
	// if (Global.totalEntityFreq.get(query_entity) != null
	// && entity2entityRes.get(query_entity) != null) {
	// totalcount = Global.totalEntityFreq.get(query_entity)
	// + entity2entityRes.get(query_entity)
	// .getEntityFreq();
	// }
	//
	// // Used for IEFreq
	// Global.totalEntityFreq.put(query_entity, totalcount);
	//
	// Global.ENTITY_FREQ_MAP.put(postid,
	// entity2entityRes.get(query_entity));
	// }
	//
	// entity2entityRes.clear();
	// }
	//
	// }

	// public void createIEFMap() {
	// for (Map.Entry entry : Global.ENTITY_FREQ_MAP.entries()) {
	// String postid = (String) entry.getKey();
	// Set<EntityResource> resources = (Set<EntityResource>)
	// Global.ENTITY_FREQ_MAP
	// .get(postid);
	// double sum_efirf = 0; // EFIRF of individual resource combined, for
	// // the entire post.
	// for (EntityResource entityRes : resources) {
	// if (entityRes != null) {
	// int entityFreq = entityRes.getEntityFreq();
	// double confidenceVal = entityRes.getConfidenceVal();
	//
	// double irfweight = round(
	// (double) Math.log(totalNoOfResources
	// / Global.totalEntityFreq.get(entityRes
	// .getEntity())), 2);
	//
	// // Each Entity is associated with confidence Value.
	// sum_efirf = sum_efirf
	// + (entityFreq * irfweight * confidenceVal);
	//
	// entityRes.setEntityFreqInverseResFreq(entityFreq
	// * irfweight);
	// Global.IEF_FREQ_MAP.put(postid, entityRes);
	// }
	// // parentId2postId.put(postid, res.getParentId());
	// }
	// Global.EFIRF_MAP.put(postid, sum_efirf);
	//
	// }
	// }

	// Combine tfirf and entityFreq_inverseResFreq with alpha weightings.
	// public String rankTheResources(double alpha) {
	// for (Map.Entry entry : TERM_FREQ_MAP.entries()) {
	// String postid = (String) entry.getKey();
	//
	// // Set the title of the post that is associated with the user.
	// String userid = postId2userId.get(postid);
	// User user = Global.userId2userObj.get(userid);
	// if (userid != null && user != null) {
	// user.setRelatedPost(postid);
	// Set termObjs = TERM_FREQ_MAP.get(postid);
	// if (termObjs.size() > 0) {
	// Resource res = (Resource) termObjs.iterator().next();
	// user.setTitle(res.getText());
	// } else {
	// user.setTitle("Title is empty");
	// }
	// }
	//
	// double sum = 0;
	// double termFreq_inverseResFreq = TFIRF_MAP.get(postid);
	// double entityFreq_inverseResFreq = 0;
	//
	// // Entites are mapped only if terms are present, this should be
	// // changed in future when creating entity freq inverse res freq map.
	// if (Global.EFIRF_MAP.get(postid) != null) {
	// entityFreq_inverseResFreq = Global.EFIRF_MAP.get(postid);
	// }
	//
	// // Scoring rule.
	// sum = (alpha) * termFreq_inverseResFreq + (1 - alpha)
	// * entityFreq_inverseResFreq;
	//
	// Global.userid2score.put(userid, sum);
	// }
	// return getExpertsList();
	// }

	// private String getExpertsList() {
	// LinkedHashMap<String, Double> experts = Global
	// .sortByValue(Global.userid2score);
	//
	// int i = 0;
	// JSONArray jsonArray = new JSONArray();
	//
	// for (String userid : experts.keySet()) {
	// i++;
	// // Restrict result to 10 items for now.
	// if (i < 10) {
	// User user = Global.userId2userObj.get(userid);
	// user.setRank(String.valueOf(Global.userid2score.get(userid)));
	// if (user != null) {
	// jsonArray.add(user);
	// }
	// } else {
	// break;
	// }
	// }
	//
	// return jsonArray.toJSONString();
	// }

}
