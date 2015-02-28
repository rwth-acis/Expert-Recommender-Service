package i5.las2peer.services.servicePackage.scoring;

import java.util.LinkedHashMap;

/**
 * @author sathvik
 */

public interface ScoreStrategy {
	void executeAlgorithm();

	String getExperts();

	LinkedHashMap<String, Double> getExpertMap();

}
