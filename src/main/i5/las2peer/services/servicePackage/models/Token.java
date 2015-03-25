package i5.las2peer.services.servicePackage.models;

/**
 * @author sathvik
 */

public class Token {
	private long mId;
	private String mText;

	private int mCount;
	private double mTfirf;
	
	public Token(long id, String text) {
		mId = id;
		mText = text;
	}
	
	public void setText(String text) {
		mText = text;
	}
	
	public void setFrequnecy(int count) {
		mCount = count;
	}
	
	public void setTfIrf(double freq) {
		mTfirf = freq;
	}

	public long getPostId() {
		return mId;
	}
	
	public String getText() {
		return mText;
	}
	
	public int getTermFreq() {
		return mCount;
	}
	
	public double getTfIrf() {
		return mTfirf;
	}
}
