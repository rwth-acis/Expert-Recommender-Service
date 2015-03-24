package i5.las2peer.services.servicePackage.scoring;

import i5.las2peer.services.servicePackage.database.UserEntity;
import i5.las2peer.services.servicePackage.utils.Global;

import java.util.LinkedHashMap;
import java.util.Map;

import net.minidev.json.JSONArray;

/**
 * @author sathvik
 */

public class ModelingStrategy1 implements ScoreStrategy {
	double alpha = 0.5;
	LinkedHashMap<Long, Double> expert2score;

	@Override
	public void executeAlgorithm() {
		System.out.println("Ranking the res..");
		try {
			for (Map.Entry entry : Global.postid2Resource1.entries()) {
				long postid = (Long) entry.getKey();

				// Set the title of the post that is associated with the user.
				if (Global.postId2userId1.containsKey(postid)) {
					long userid = Global.postId2userId1.get(postid);
					UserEntity user = Global.userId2userObj1.get(userid);
					if (userid > 0 && user != null) {
						// user.setRelatedPost(postid);
						// Set termObjs = TERM_FREQ_MAP.get(postid);
						// if (termObjs.size() > 0) {
						// Resource res = (Resource) termObjs.iterator().next();
						// user.setTitle(res.getText());
					} else {
						// user.setTitle("Title is empty");
					}

					double sum = 0;
					double termFreq_inverseResFreq = Global.postid2tfirf
							.get(postid);

					double entityFreq_inverseResFreq = 0;

					// Entites are mapped only if terms are present, this should
					// be changed in future when creating entity freq inverse
					// res freq map.
					if (Global.EFIRF_MAP.get(postid) != null) {
						entityFreq_inverseResFreq = Global.EFIRF_MAP
								.get(postid);
					}

					// Scoring rule.
					sum = (alpha) * termFreq_inverseResFreq + (1 - alpha)
							* entityFreq_inverseResFreq;

					Global.userid2score.put(userid, sum);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Ranking the res completed..");
	}

	public String getExperts() {
		JSONArray jsonArray = new JSONArray();
		try {
			expert2score = Global
					.sortByValue(Global.userid2score);

			int i = 0;

			for (long userid : expert2score.keySet()) {

				// Restrict result to 10 items for now.
				if (i < 10) {
					UserEntity user = Global.userId2userObj1.get(userid);
					user.setScore(Global.userid2score.get(userid));
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
