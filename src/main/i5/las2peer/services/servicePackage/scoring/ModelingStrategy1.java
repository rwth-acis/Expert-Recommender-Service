package i5.las2peer.services.servicePackage.scoring;

import i5.las2peer.services.servicePackage.datamodel.UserEntity;
import i5.las2peer.services.servicePackage.utils.Application;
import i5.las2peer.services.servicePackage.xmlparsers.Resource;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import net.minidev.json.JSONArray;

import com.google.common.collect.HashMultimap;

/**
 * @author sathvik
 */

public class ModelingStrategy1 implements ScoreStrategy {
	double alpha = 0.5;
	private Map<Long, Double> userId2score = new HashMap<Long, Double>();
	LinkedHashMap<Long, Double> expert2score;
	private Map<Long, UserEntity> userId2userObj;

	public ModelingStrategy1(Map<Long, UserEntity> userId2userObj) {
		this.userId2userObj = userId2userObj;
	}
	// TODO:get rid of this.
	public static HashMultimap<Long, Resource> postid2Resource = HashMultimap
			.create();

	@Override
	public void executeAlgorithm() {
		// System.out.println("Ranking the res..");
		// try {
		// for (Map.Entry entry : postid2Resource.entries()) {
		// long postid = (Long) entry.getKey();
		//
		// // Set the title of the post that is associated with the user.
		// if (Application.postId2userId1.containsKey(postid)) {
		// long userid = Application.postId2userId1.get(postid);
		// UserEntity user = Application.userId2userObj.get(userid);
		// if (userid > 0 && user != null) {
		// // user.setRelatedPost(postid);
		// // Set termObjs = TERM_FREQ_MAP.get(postid);
		// // if (termObjs.size() > 0) {
		// // Resource res = (Resource) termObjs.iterator().next();
		// // user.setTitle(res.getText());
		// } else {
		// // user.setTitle("Title is empty");
		// }
		//
		// double sum = 0;
		// double termFreq_inverseResFreq = Application.postid2tfirf
		// .get(postid);
		//
		// double entityFreq_inverseResFreq = 0;
		//
		// // Entites are mapped only if terms are present, this should
		// // be changed in future when creating entity freq inverse
		// // res freq map.
		// if (Application.EFIRF_MAP.get(postid) != null) {
		// entityFreq_inverseResFreq = Application.EFIRF_MAP
		// .get(postid);
		// }
		//
		// // Scoring rule.
		// sum = (alpha) * termFreq_inverseResFreq + (1 - alpha)
		// * entityFreq_inverseResFreq;
		//
		// userId2score.put(userid, sum);
		// }
		// }
		//
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// System.out.println("Ranking the res completed..");
	}

	public String getExperts() {
		JSONArray jsonArray = new JSONArray();
		try {
			expert2score = Application.sortByValue(userId2score);

			int i = 0;

			for (long userid : expert2score.keySet()) {

				// Restrict result to 10 items for now.
				if (i < 10) {
					UserEntity user = userId2userObj.get(userid);
					user.setScore(userId2score.get(userid));
					if (user != null) {
						jsonArray.add(user);
					}
				} else {
					break;
				}
				i++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return jsonArray.toJSONString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * i5.las2peer.services.servicePackage.scoring.ScoreStrategy#getExpertMap()
	 */
	@Override
	public LinkedHashMap<String, Double> getExpertMap() {
		// Avoid this, try to change in the exper2score map itself.
		LinkedHashMap<String, Double> expertMap = new LinkedHashMap<String, Double>();
		for (Map.Entry<Long, Double> entry : expert2score.entrySet()) {
			expertMap.put(String.valueOf(entry.getKey()), entry.getValue());
			
		}
		return expertMap;
	}

}
