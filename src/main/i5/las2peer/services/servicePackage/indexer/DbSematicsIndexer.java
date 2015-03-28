/**
 * 
 */
package i5.las2peer.services.servicePackage.indexer;

import i5.las2peer.services.servicePackage.datamodel.SemanticTagEntity;
import i5.las2peer.services.servicePackage.models.SemanticToken;
import i5.las2peer.services.servicePackage.semanticTagger.SemanticTagger;
import i5.las2peer.services.servicePackage.utils.Application;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.HashMultiset;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;

/**
 * @author sathvik
 *
 */
public class DbSematicsIndexer {
	HashMultiset<String> mEntities;
	HashMultiset<String> mQueryEntities;

	private HashMultimap<Long, SemanticToken> postId2EntityToken;
	// private HashMultimap<Long, EntityToken> postId2InverseSemanticEntity;
	private Map<Long, Double> postId2efirf;
	private HashMap<String, Integer> totalEntityFreq;

	private int totalNoResources = 0;
	private ConnectionSource mConnectionSource;

	public DbSematicsIndexer(ConnectionSource connectionSrc) {
		mConnectionSource = connectionSrc;

		postId2EntityToken = HashMultimap.create();
		// postId2InverseSemanticEntity = HashMultimap.create();
		postId2efirf = new HashMap<Long, Double>();
		totalEntityFreq = new HashMap<String, Integer>();
	}

	public void buildIndex(String queryString) {
		SemanticTagger tagger = new SemanticTagger(queryString);
		mQueryEntities = tagger.getTokens();

		try {
			Dao<SemanticTagEntity, Long> tagDao = DaoManager.createDao(
					mConnectionSource, SemanticTagEntity.class);
			List<SemanticTagEntity> tag_entites = tagDao.queryForAll();
			for (SemanticTagEntity tag_entity : tag_entites) {
				addTagEntityFreqMap(tag_entity.getPostId(),
						tag_entity.getAnnotations());
			}
			totalNoResources = tag_entites.size();
		} catch (Exception e) {
			e.printStackTrace();
		}

		createEfirfMap();

	}

	private void addTagEntityFreqMap(long postid, String annotations) {
		mEntities = HashMultiset.create();

		if (annotations != null) {

			// TODO:Use mapper to map from json to the class, instead of parsing
			JsonParser parser = new JsonParser();
			JsonArray jarr = (JsonArray) parser.parse(annotations);
			JsonArray tagArr = null;
			double confidenceVal = 0;
			SemanticToken token;

			HashMap<String, SemanticToken> tag2tagRes = new HashMap<String, SemanticToken>();
			for (int i = 0; i < jarr.size(); i++) {
				JsonElement obj = ((JsonObject) jarr.get(i)).get("tags");
				// System.out.println(postid + " " + obj);
				if (obj != null && obj.isJsonNull() == false) {
					tagArr = obj.getAsJsonArray();
					for (JsonElement elem : tagArr) {
						String entity = elem.getAsString();
						mEntities.add(entity);

						token = new SemanticToken(postid, annotations);
						token.setConfidenceVal(confidenceVal);
						token.setName(entity);
						if (tag2tagRes.containsKey(entity) == false) {
							tag2tagRes.put(entity, token);
						} else {
							SemanticToken temp = tag2tagRes.get(entity);
							temp.setFrequnecy(temp.getFreq() + 1);
						}
					}
					confidenceVal = ((JsonObject) jarr.get(i)).get("rho")
							.getAsDouble();
				}
			}

			// Iterate the Query entities and create a map for only required
			// entities.
			int totalcount = 0;
			for (String query_entity : mQueryEntities.elementSet()) {
				if (totalEntityFreq.get(query_entity) != null
						&& tag2tagRes.get(query_entity) != null) {
					totalcount = totalEntityFreq.get(query_entity)
							+ tag2tagRes.get(query_entity).getFreq();
				}

				// Used for IEFreq
				if (totalcount > 0) {
					totalEntityFreq.put(query_entity, totalcount);
				}

				postId2EntityToken.put(postid, tag2tagRes.get(query_entity));
			}

			tag2tagRes.clear();
		}

	}

	private void createEfirfMap() {
		try {
			for (Map.Entry entry : postId2EntityToken.entries()) {
				long postid = (long) entry.getKey();
				Set<SemanticToken> resources = (Set<SemanticToken>) postId2EntityToken
						.get(postid);
				double sum_efirf = 0; // EFIRF of individual resource combined,
										// for
										// the entire post.
				for (SemanticToken entityRes : resources) {
					if (entityRes != null) {
						int entityFreq = entityRes.getFreq();
						double confidenceVal = entityRes.getConfidenceVal();
						double irfweight = 0;
						if (totalEntityFreq.containsKey(entityRes.getName())) {
							irfweight = Application.round(
									(double) Math.log(totalNoResources
											/ totalEntityFreq.get(entityRes
													.getName())), 2);
						}
						// Each Entity is associated with confidence Value.
						sum_efirf = sum_efirf
								+ (entityFreq * irfweight * confidenceVal);

						entityRes.setTfIrf(entityFreq * irfweight);
						// postId2InverseSemanticEntity.put(postid, entityRes);
					}
				}
				postId2efirf.put(postid, sum_efirf);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Map<Long, Double> getEfirfMap() {
		return postId2efirf;
	}
}
