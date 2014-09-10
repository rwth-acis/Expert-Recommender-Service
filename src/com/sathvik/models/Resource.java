package com.sathvik.models;

public class Resource {
	int postid;
	String text;
	int term_count;
	String term;
	
	public Resource(int id, String text) {
		this.postid = id;
		this.text = text;
	}
	
	public void setTerm(String term) {
		this.term = term;
	}
	
	public void setTermFreq(int count) {
		this.term_count = count;
	}
	
	public int getPostId() {
		return postid;
	}
	
	public String getText() {
		return text;
	}
	
	public int getTermFreq() {
		return term_count;
	}
	
	public String getTerm() {
		return term;
	}
}
