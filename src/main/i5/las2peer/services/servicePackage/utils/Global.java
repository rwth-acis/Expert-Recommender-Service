package i5.las2peer.services.servicePackage.utils;

import i5.las2peer.services.servicePackage.database.UserEntity;
import i5.las2peer.services.servicePackage.graph.JUNGGraphCreator;
import i5.las2peer.services.servicePackage.models.EntityResource;
import i5.las2peer.services.servicePackage.models.Resource;
import i5.las2peer.services.servicePackage.scoring.ModelingStrategy1;
import i5.las2peer.services.servicePackage.scoring.ScoringContext;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * @author sathvik TODO://Replace all temporary variables.
 */

public class Global {
	// public static Map<String, User> userId2userObj = new HashMap<String,
	// User>();
	public static Map<Long, UserEntity> userId2userObj1 = new HashMap<Long, UserEntity>();
	public static HashMultiset QUERY_WORDS;
	public static int THRESHOLD_WORD_FREQ1 = 0;
	public static HashMap<Object, Integer> word2freq = new HashMap<Object, Integer>();
	public static HashMultimap<Long, Resource> postid2Resource1 = HashMultimap
			.create();
	public static HashMap<Long, Set<Long>> q2a1 = new HashMap<Long, Set<Long>>();
	public static Map<Long, Double> postid2tfirf = new HashMap<Long, Double>();

	public static JUNGGraphCreator jcreator;

	public static HashMap<String, Integer> totalEntityFreq = new HashMap<String, Integer>();
	public static HashMultiset<String> QUERY_ENTITIES;
	public static Multiset<String> entities;

	public static HashMultimap<Long, EntityResource> ENTITY_FREQ_MAP = HashMultimap
			.create();
	public static HashMultimap<Long, EntityResource> IEF_FREQ_MAP = HashMultimap
			.create();
	public static Map<Long, Double> EFIRF_MAP = new HashMap<Long, Double>();

	public static Map<Long, Double> userid2score = new HashMap<Long, Double>();

	public static HashMap<Long, Resource> postid2Resource = new HashMap();

	// TODO:Change the name without name clash.
	public static HashMap<Long, Long> postId2userId1 = new HashMap<Long, Long>();
	public static HashMultimap<Long, Long> parentId2postId1 = HashMultimap
			.create();

	public static double totalNoOfResources = 0;

	public static <K, V extends Comparable<? super V>> LinkedHashMap<K, V> sortByValue(
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

	public static void createTermFreqMap(long postid, String text,
			long parentId, long userid) {
		Multiset<String> bagOfWords = HashMultiset.create(Splitter
				.on(CharMatcher.WHITESPACE).omitEmptyStrings().split(text));

		int totalcount = 0;

		Resource resource;
		int count = 0;
		// Iterate the Query terms.
		// Ignore if freq is too less.
		for (Object word : QUERY_WORDS.elementSet()) {
			count = bagOfWords.count(word);

			totalcount = word2freq.get((String) word) != null ? word2freq
					.get((String) word) + count : 0;
			word2freq.put((String) word, totalcount);

			if (count > THRESHOLD_WORD_FREQ1) {
				resource = new Resource(postid, text);
				resource.setTerm((String) word);
				resource.setTermFreq(count);
				resource.setParentId(parentId);
				postid2Resource1.put(postid, resource);
				if (parentId > 0) {
					parentId2postId1.put(parentId, postid);
				}
				System.out.println("USERID::" + userid);
				if (userid > 0) {
					postId2userId1.put(postid, userid);
				}
			}
		}

	}

	public static void createFilteredQnAMap() {
		Set<Long> keyset = postid2Resource1.keySet();
		// System.out.println("Keys..." + keyset.size());
		for (Long parentid : keyset) {
			try {

				Set<Long> ids = parentId2postId1.get(parentid);
				if (ids != null && ids.size() > 0) {
					q2a1.put(parentid, ids);
					// System.out.println(parentid + " " + ids);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// System.out.println(Global.printMap(Global.q2a));
	}

	// public static void createJUNGGraph() {
	// jcreator = new JUNGGraphCreator();
	// // Logger log = LoggerFactory.getLogger(ExpertUtils.class);
	// // log.info("Creating JUNG graph...");
	//
	// Stopwatch timer = Stopwatch.createStarted();
	// System.out.println("Creating graph...");
	//
	// try {
	// for (Long key : q2a1.keySet()) {
	//
	// // System.out.println("Key::" + key);
	// if (postId2userId1.containsKey(key)) {
	// long q_user_id = postId2userId1.get(key);
	// UserEntity user = Global.userId2userObj1.get(q_user_id);
	// // if (q_user_id > 0 && user != null) {
	// // // user.setRelatedPost(key);
	// // Set termObjs = postid2Resource1.get(key);
	// // if (termObjs.size() > 0) {
	// // Resource res = (Resource) termObjs.iterator().next();
	// // user.setTitle(res.getText());
	// // } else {
	// // user.setTitle("Title is empty");
	// // }
	// // }
	//
	// if (q_user_id > 0) {
	// // System.out.println("post " + key + " userid " +
	// // q_user_id);
	// jcreator.createVertex(String.valueOf(q_user_id));
	// Set<Long> values = q2a1.get(key);
	//
	// for (Long value : values) {
	// Long a_user_id = postId2userId1.get(value);
	// user = Global.userId2userObj1.get(a_user_id);
	// if (a_user_id != null) {
	// // if (Global.userId2userObj1.get(a_user_id) !=
	// // null) {
	// // user.setRelatedPost(value);
	// //
	// // Set termObjs = TERM_FREQ_MAP.get(value);
	// // if (termObjs.size() > 0) {
	// // Resource res = (Resource) termObjs.iterator()
	// // .next();
	// // user.setTitle(res.getText());
	// //
	// // } else {
	// // user.setTitle("Title is empty");
	// // }
	// // }
	// jcreator.createVertex(String.valueOf(a_user_id));
	// jcreator.createEdge(String.valueOf(q_user_id),
	// String.valueOf(a_user_id),
	// String.valueOf(value));
	// }
	// }
	// }
	// } else {
	// System.out.println("Doesnot contain this key" + key);
	// }
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// System.out.println("Graph created...");
	//
	// System.out.println("Graph created...");
	// System.out.println("Graph Creation Time... " + timer.stop());
	// // save2JungGraphML("fitness_graph_jung.graphml");
	// }

	public static double round(double value, int places) {
		if (places < 0)
			throw new IllegalArgumentException();

		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}

	public static void createInverseResFreqMap() {
		System.out.println("Creating Inverse Freq Map...");
		try {
			for (Map.Entry entry : Global.postid2Resource1.entries()) {
				Long postid = (Long) entry.getKey();
				Set<i5.las2peer.services.servicePackage.models.Resource> resources = (Set<i5.las2peer.services.servicePackage.models.Resource>) Global.postid2Resource1
						.get(postid);
				double sum_tfirf = 0; // TFIRF of individual resource combined,
										// for
										// the entire post.
				for (i5.las2peer.services.servicePackage.models.Resource res : resources) {
					int termFreq = res.getTermFreq();

					double irfweight = Global.round(
							(double) Math.log(Global.totalNoOfResources
									/ Global.word2freq.get(res.getTerm())), 2);
					sum_tfirf = sum_tfirf + (termFreq * irfweight);

					res.setTermFreqInverseResFreq(termFreq * irfweight);
					// IRF_FREQ_MAP.put(postid, res);
					// parentId2postId.put(postid, res.getParentId());
				}
				Global.postid2tfirf.put(postid, sum_tfirf);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Creating Inverse Freq Map completed...");
	}

	// Combine tfirf and entityFreq_inverseResFreq with alpha weightings.
	public static String rankTheResources(double alpha) {
		System.out.println("Executing Modeling strategey...");
		ScoringContext scontext = new ScoringContext(new ModelingStrategy1());
		scontext.executeStrategy();
		return scontext.getExperts();
	}

	// TODO:Refactor
	public static void createTagEntityFreqMap(long postid, String annotations) {

		if (annotations != null) {

			// TODO:Use mapper to map from json to the class, instead of parsing
			JsonParser parser = new JsonParser();
			JsonArray jarr = (JsonArray) parser.parse(annotations);
			JsonArray tagArr = null;
			double confidenceVal = 0;
			EntityResource entResource;

			Global.entities = HashMultiset.create();
			HashMap<String, EntityResource> tag2tagRes = new HashMap<String, EntityResource>();
			for (int i = 0; i < jarr.size(); i++) {
				JsonElement obj = ((JsonObject) jarr.get(i)).get("tags");
				// System.out.println(postid + " " + obj);
				if (obj != null && obj.isJsonNull() == false) {
					tagArr = obj.getAsJsonArray();
					for (JsonElement elem : tagArr) {
						String entity = elem.getAsString();
						Global.entities.add(entity);

						entResource = new EntityResource(postid, annotations);
						entResource.setConfidenceVal(confidenceVal);
						entResource.setEntity(entity);
						if (tag2tagRes.containsKey(entity) == false) {
							tag2tagRes.put(entity, entResource);
						} else {
							EntityResource temp = tag2tagRes.get(entity);
							temp.setEntityFreq(temp.getEntityFreq() + 1);
						}
					}
					confidenceVal = ((JsonObject) jarr.get(i)).get("rho")
							.getAsDouble();
				}
			}

			// Iterate the Query entities and create a map for only required
			// entities.
			int totalcount = 0;
			for (String query_entity : Global.QUERY_ENTITIES.elementSet()) {
				if (Global.totalEntityFreq.get(query_entity) != null
						&& tag2tagRes.get(query_entity) != null) {
					totalcount = Global.totalEntityFreq.get(query_entity)
							+ tag2tagRes.get(query_entity).getEntityFreq();
				}

				// Used for IEFreq
				if (totalcount > 0) {
					Global.totalEntityFreq.put(query_entity, totalcount);
				}

				Global.ENTITY_FREQ_MAP
						.put(postid, tag2tagRes.get(query_entity));
			}

			tag2tagRes.clear();
		}

	}

	public static void createIEFMap() {
		try {
			for (Map.Entry entry : Global.ENTITY_FREQ_MAP.entries()) {
				long postid = (long) entry.getKey();
				Set<EntityResource> resources = (Set<EntityResource>) Global.ENTITY_FREQ_MAP
						.get(postid);
				double sum_efirf = 0; // EFIRF of individual resource combined,
										// for
										// the entire post.
				for (EntityResource entityRes : resources) {
					if (entityRes != null) {
						int entityFreq = entityRes.getEntityFreq();
						double confidenceVal = entityRes.getConfidenceVal();
						double irfweight = 0;
						if (Global.totalEntityFreq.containsKey(entityRes
								.getEntity())) {
							irfweight = round(
									(double) Math
											.log(totalNoOfResources
													/ Global.totalEntityFreq.get(entityRes
															.getEntity())),
									2);
						}
						// Each Entity is associated with confidence Value.
						sum_efirf = sum_efirf
								+ (entityFreq * irfweight * confidenceVal);

						entityRes.setEntityFreqInverseResFreq(entityFreq
								* irfweight);
						Global.IEF_FREQ_MAP.put(postid, entityRes);
					}
					// parentId2postId.put(postid, res.getParentId());
				}
				Global.EFIRF_MAP.put(postid, sum_efirf);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void reset() {

		userId2userObj1.clear();

		THRESHOLD_WORD_FREQ1 = 0;
		word2freq.clear();
		postid2Resource1.clear();
		q2a1.clear();
		postid2tfirf.clear();
		totalEntityFreq.clear();
		ENTITY_FREQ_MAP.clear();
		IEF_FREQ_MAP.clear();
		EFIRF_MAP.clear();
		userid2score.clear();
		postid2Resource.clear();
		postId2userId1.clear();
		parentId2postId1.clear();

		totalNoOfResources = 0;

		if (QUERY_WORDS != null)
			QUERY_WORDS.clear();

		if (jcreator != null)
			jcreator = null;

		if (QUERY_ENTITIES != null)
			QUERY_ENTITIES.clear();

		if (entities != null)
			entities.clear();

	}

}
