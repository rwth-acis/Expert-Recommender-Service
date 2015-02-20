package i5.las2peer.services.servicePackage.models;

/**
 * @author sathvik
 */

public class MyVertex {
	String label;
	String score;

	MyVertex(String label, String score) {
		this.label = label;
		this.score = score;
	}
	
	public String getLabel() {
		return this.label;
	}
	
	public String getScore() {
		return this.score;
	}
}
