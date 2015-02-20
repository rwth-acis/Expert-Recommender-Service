package i5.las2peer.services.servicePackage.scoring;

/**
 * @author sathvik
 */

public class ScoringContext {
	private ScoreStrategy strategy;

	public ScoringContext(ScoreStrategy strategy) {
		this.strategy = strategy;
	}

	public String executeStrategy() {
		return this.strategy.executeAlgorithm();
	}
}
