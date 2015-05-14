package i5.las2peer.services.servicePackage.metrics;

import i5.las2peer.services.servicePackage.entities.UserEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author sathvik
 */

public class ReciprocalRank {
    private List<Long> reputations = new ArrayList<Long>();
    private double rr = 0;
    private Map<Long, UserEntity> userId2userObj;

    public ReciprocalRank(Map<Long, UserEntity> userId2userObj) {
	this.userId2userObj = userId2userObj;
    }

    public double getValue() {
	return rr;
    }

    public void compute(Map<String, Double> expertuser2score) {
	Set<String> expertkeys = expertuser2score.keySet();
	int count = 0;
	for (String userid : expertkeys) {
	    if (count < 10) {
		UserEntity user = userId2userObj.get(Long.valueOf(userid));
		if (user != null) {
		    reputations.add(user.getReputation());
		    // System.out.println(" Userid " + user.getUserId() +
		    // " Rep..." + user.getReputation());
		}

	    } else {
		break;
	    }
	    count++;
	}
	rr = getPositionOfHighestReputaiton();
    }

    public double getPositionOfHighestReputaiton() {
	if (reputations != null && reputations.size() > 0) {
	    long max = Collections.max(reputations);
	    return 1.0 / (reputations.indexOf(max) + 1);
	}
	return -1;
    }
}
