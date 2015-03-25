package i5.las2peer.services.servicePackage.utils;

import i5.las2peer.services.servicePackage.datamodel.DataEntity;
import i5.las2peer.services.servicePackage.models.EntityResource;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

public class Application {

	public static Map<Long, DataEntity> postId2postObj = new HashMap<Long, DataEntity>();

	public static HashMap<Object, Integer> word2freq = new HashMap<Object, Integer>();

	public static HashMap<String, Integer> totalEntityFreq = new HashMap<String, Integer>();
	public static HashMultiset<String> QUERY_ENTITIES;
	public static Multiset<String> entities;

	public static HashMultimap<Long, EntityResource> ENTITY_FREQ_MAP = HashMultimap
			.create();
	public static HashMultimap<Long, EntityResource> IEF_FREQ_MAP = HashMultimap
			.create();
	public static Map<Long, Double> EFIRF_MAP = new HashMap<Long, Double>();

	public static double totalNoOfResources = 0;
	public static String algoName = "";

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

	// public static void createTermFreqMap(long postid, String text,
	// long parentId, long userid) {
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
	// totalcount = word2freq.get((String) word) != null ? word2freq
	// .get((String) word) + count : 0;
	// word2freq.put((String) word, totalcount);
	//
	// if (count > THRESHOLD_WORD_FREQ1) {
	// resource = new Resource(postid, text);
	// resource.setTerm((String) word);
	// resource.setTermFreq(count);
	// resource.setParentId(parentId);
	// postid2Resource1.put(postid, resource);
	// if (parentId > 0) {
	// parentId2postId1.put(parentId, postid);
	// }
	// System.out.println("USERID::" + userid);
	// if (userid > 0) {
	// postId2userId1.put(postid, userid);
	// }
	// }
	// }
	//
	// }

	// public static void createFilteredQnAMap() {
	// Set<Long> keyset = postid2Resource1.keySet();
	// // System.out.println("Keys..." + keyset.size());
	// for (Long parentid : keyset) {
	// try {
	//
	// Set<Long> ids = parentId2postId1.get(parentid);
	// if (ids != null && ids.size() > 0) {
	// q2a1.put(parentid, ids);
	// // System.out.println(parentid + " " + ids);
	// }
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	// // System.out.println( printMap( q2a));
	// }

	public static double round(double value, int places) {
		if (places < 0)
			throw new IllegalArgumentException();

		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}

	// public static void createInverseResFreqMap() {
	// System.out.println("Creating Inverse Freq Map...");
	// try {
	// for (Map.Entry entry : postid2Resource1.entries()) {
	// Long postid = (Long) entry.getKey();
	// Set<i5.las2peer.services.servicePackage.models.Resource> resources =
	// (Set<i5.las2peer.services.servicePackage.models.Resource>)
	// postid2Resource1
	// .get(postid);
	// double sum_tfirf = 0; // TFIRF of individual resource combined,
	// // for
	// // the entire post.
	// for (i5.las2peer.services.servicePackage.models.Resource res : resources)
	// {
	// int termFreq = res.getTermFreq();
	//
	// double irfweight = round(
	// (double) Math.log(totalNoOfResources
	// / word2freq.get(res.getTerm())), 2);
	// sum_tfirf = sum_tfirf + (termFreq * irfweight);
	//
	// res.setTermFreqInverseResFreq(termFreq * irfweight);
	// // IRF_FREQ_MAP.put(postid, res);
	// // parentId2postId.put(postid, res.getParentId());
	// }
	// postid2tfirf.put(postid, sum_tfirf);
	//
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// System.out.println("Creating Inverse Freq Map completed...");
	// }

	// Combine tfirf and entityFreq_inverseResFreq with alpha weightings.
	// public static String rankTheResources(double alpha) {
	// System.out.println("Executing Modeling strategey...");
	// ScoringContext scontext = new ScoringContext(new ModelingStrategy1());
	// scontext.executeStrategy();
	// return scontext.getExperts();
	// }

	// TODO:Refactor
	public static void createTagEntityFreqMap(long postid, String annotations) {

		if (annotations != null) {

			// TODO:Use mapper to map from json to the class, instead of parsing
			JsonParser parser = new JsonParser();
			JsonArray jarr = (JsonArray) parser.parse(annotations);
			JsonArray tagArr = null;
			double confidenceVal = 0;
			EntityResource entResource;

			entities = HashMultiset.create();
			HashMap<String, EntityResource> tag2tagRes = new HashMap<String, EntityResource>();
			for (int i = 0; i < jarr.size(); i++) {
				JsonElement obj = ((JsonObject) jarr.get(i)).get("tags");
				// System.out.println(postid + " " + obj);
				if (obj != null && obj.isJsonNull() == false) {
					tagArr = obj.getAsJsonArray();
					for (JsonElement elem : tagArr) {
						String entity = elem.getAsString();
						entities.add(entity);

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
			for (String query_entity : QUERY_ENTITIES.elementSet()) {
				if (totalEntityFreq.get(query_entity) != null
						&& tag2tagRes.get(query_entity) != null) {
					totalcount = totalEntityFreq.get(query_entity)
							+ tag2tagRes.get(query_entity).getEntityFreq();
				}

				// Used for IEFreq
				if (totalcount > 0) {
					totalEntityFreq.put(query_entity, totalcount);
				}

				ENTITY_FREQ_MAP
						.put(postid, tag2tagRes.get(query_entity));
			}

			tag2tagRes.clear();
		}

	}

	public static void createIEFMap() {
		try {
			for (Map.Entry entry : ENTITY_FREQ_MAP.entries()) {
				long postid = (long) entry.getKey();
				Set<EntityResource> resources = (Set<EntityResource>) ENTITY_FREQ_MAP
						.get(postid);
				double sum_efirf = 0; // EFIRF of individual resource combined,
										// for
										// the entire post.
				for (EntityResource entityRes : resources) {
					if (entityRes != null) {
						int entityFreq = entityRes.getEntityFreq();
						double confidenceVal = entityRes.getConfidenceVal();
						double irfweight = 0;
						if (totalEntityFreq.containsKey(entityRes
								.getEntity())) {
							irfweight = round(
									(double) Math
											.log(totalNoOfResources
											/ totalEntityFreq.get(entityRes
															.getEntity())),
									2);
						}
						// Each Entity is associated with confidence Value.
						sum_efirf = sum_efirf
								+ (entityFreq * irfweight * confidenceVal);

						entityRes.setEntityFreqInverseResFreq(entityFreq
								* irfweight);
						IEF_FREQ_MAP.put(postid, entityRes);
					}
					// parentId2postId.put(postid, res.getParentId());
				}
				EFIRF_MAP.put(postid, sum_efirf);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String readFile(String path, Charset encoding)
			throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}

	public static void reset() {

		word2freq.clear();

		totalEntityFreq.clear();
		ENTITY_FREQ_MAP.clear();
		IEF_FREQ_MAP.clear();
		EFIRF_MAP.clear();
		totalNoOfResources = 0;


		if (QUERY_ENTITIES != null)
			QUERY_ENTITIES.clear();

		if (entities != null)
			entities.clear();

	}

}
