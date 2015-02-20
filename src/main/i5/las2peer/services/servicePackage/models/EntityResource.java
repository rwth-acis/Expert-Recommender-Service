package i5.las2peer.services.servicePackage.models;

/**
 * @author sathvik TODO:// Avoid maintaining this bean class.
 */

public class EntityResource {
	long postid;
	String annotations;
	int entity_count = 0;
	String entity;
	double efirf;
	double confidenceVal;
	
	public EntityResource(long id, String annotations) {
		this.postid = id;
		this.annotations = annotations;
	}
	
	public void setConfidenceVal(double confidenceVal) {
		this.confidenceVal = confidenceVal;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}
	
	public void setEntityFreqInverseResFreq(double freq) {
		this.efirf = freq;
	}
	
	public void setEntityFreq(int count) {
		this.entity_count = count;
	}
	
	public long getPostId() {
		return postid;
	}
	
	public String getAnnotaitons() {
		return this.annotations;
	}
	
	public int getEntityFreq() {
		return entity_count;
	}
	
	public String getEntity() {
		return entity;
	}
	
	public double getEntityFreqInverseResFreq() {
		return efirf;
	}
	
	public double getConfidenceVal() {
		return this.confidenceVal;
	}

}
