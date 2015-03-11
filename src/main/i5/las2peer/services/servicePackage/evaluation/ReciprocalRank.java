package i5.las2peer.services.servicePackage.evaluation;

import i5.las2peer.services.servicePackage.database.UserEntity;
import i5.las2peer.services.servicePackage.utils.Global;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author sathvik
 */

public class ReciprocalRank {
	List<Long> reputations = new ArrayList<Long>();
	public static final String mrrFilePath = "mrr.txt";
	double rr = 0;

	public ReciprocalRank() {

	}

	public double getValue() {
		return rr;
	}

	public void computeReciprocalRank(Map<String, Double> expertuser2score) {
		Set<String> expertkeys = expertuser2score.keySet();
		int i = 0;
		for (String userid : expertkeys) {

			if (i < 10) {
				UserEntity user = Global.userId2userObj1.get(userid);
				if (user != null) {
					reputations.add(user.getReputation());
				}
				// System.out.println(" Userid " + user.getUserId() + " Rep..."
				// + user.getReputation());
			} else {
				break;
			}
			i++;
		}
		rr = getPositionOfHighestReputaiton();
	}

	public double getPositionOfHighestReputaiton() {
		if (reputations != null && reputations.size() > 0) {
			double max = Collections.max(reputations);
			return 1.0 / (reputations.indexOf(max) + 1);
		}
		return -1;
	}

	public void save() {
		try (PrintWriter out = new PrintWriter(new BufferedWriter(
				new FileWriter(mrrFilePath, false)))) {
			out.println("MRR: " + rr);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
