package i5.las2peer.services.servicePackage.models;

/**
 * @author sathvik
 */

public class SemanticData {
	String annotations;
	String tags;

	public SemanticData(String ann, String tags) {
		this.annotations = ann;
		this.tags = tags;
	}

	public String getAnnotation() {
		return this.annotations;
	}

	public String getTags() {
		return this.tags;
	}
}
