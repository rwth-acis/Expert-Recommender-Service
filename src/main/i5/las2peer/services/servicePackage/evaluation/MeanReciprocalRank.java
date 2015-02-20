package i5.las2peer.services.servicePackage.evaluation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author sathvik
 */

public class MeanReciprocalRank {
	List<Double> reputations = new ArrayList();
	public MeanReciprocalRank() {

	}

	public String getReciprocalRank(Map<String, Double> expertuser2score) {
		// Set<String> expertkeys = expertuser2score.keySet();
		// int i = 0;
		// for (String userid : expertkeys) {
		//
		// if (i < 10) {
		// User user = Global.userId2userObj.get(userid);
		// reputations.add(Double.parseDouble(user.getReputation()));
		// System.out.println(" Userid " + user.getUserId() + " Rep..."
		// + user.getReputation());
		// } else {
		// break;
		// }
		// i++;
		// }

		return getPositionOfHighestReputaiton();
	}

	public String getPositionOfHighestReputaiton() {
		double max = Collections.max(reputations);
		double rank = 1.0 / (reputations.indexOf(max) + 1);
		return String.valueOf(rank);
	}
}
