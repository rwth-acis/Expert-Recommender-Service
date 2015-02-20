package i5.las2peer.services.servicePackage.models;

/**
 * @author sathvik
 */

public class Resource {
	long postid;
	long parentId;
	String text;
	int term_count;
	String term;
	double tfirf;
	double popularity;
	double cosine_similarity;
	long last_edit_date; // Set it in millisec.
	
	public Resource(long id, String text) {
		this.postid = id;
		this.text = text;
	}
	
	public void setLastEditDate(long edit_date) {
		last_edit_date = edit_date;
	}

	public void setPopularity(double popularity) {
		this.popularity = popularity;
	}

	public void setCosineSimilarity(double cosine_similarity) {
		this.cosine_similarity = cosine_similarity;
	}

	public void setTerm(String term) {
		this.term = term;
	}
	
	public void setParentId(long parentId) {
		this.parentId = parentId;
	}
	
	public void setTermFreqInverseResFreq(double freq) {
		this.tfirf = freq;
	}
	
	public void setTermFreq(int count) {
		this.term_count = count;
	}
	
	public long getPostId() {
		return postid;
	}
	
	public String getText() {
		return this.text;
	}
	
	public int getTermFreq() {
		return term_count;
	}
	
	public String getTerm() {
		return term;
	}
	
	public double getTermFreqInverseResFreq() {
		return tfirf;
	}
	
	public long getParentId() {
		return parentId;
	}

	public double getCosineSimilarity() {
		return this.cosine_similarity;
	}

	public double getPopularity() {
		return this.popularity;
	}

	public long getLastEditDate() {
		return last_edit_date;
	}
}
