package i5.las2peer.services.servicePackage.models;

/**
 * @author sathvik
 */

public class SemanticToken extends Token {

	private String mAnnotations;
	private double mConfidenceVal;
	
	public SemanticToken(long id, String annotations) {
		super(id, annotations);
		mAnnotations = annotations;
	}
	
	public void setConfidenceVal(double confidenceVal) {
		mConfidenceVal = confidenceVal;
	}

	public String getAnnotaitons() {
		return mAnnotations;
	}
	
	public double getConfidenceVal() {
		return mConfidenceVal;
	}

}
