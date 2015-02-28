package i5.las2peer.services.servicePackage.scoring;

import java.util.LinkedHashMap;

/**
 * @author sathvik
 */

public class ScoringContext {
	private ScoreStrategy strategy;

	public ScoringContext(ScoreStrategy strategy) {
		this.strategy = strategy;
	}

	public void executeStrategy() {
		this.strategy.executeAlgorithm();
	}

	public String getExperts() {
		return this.strategy.getExperts();
	}

	public LinkedHashMap<String, Double> getExpertMap() {
		return this.strategy.getExpertMap();
	}
}
