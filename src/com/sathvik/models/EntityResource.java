package com.sathvik.models;

public class EntityResource {
	int postid;
	String text;
	int entity_count;
	String entity;
	
	public EntityResource(int id, String text) {
		this.postid = id;
		this.text = text;
	}
	
	public void setEntity(String entity) {
		this.entity = entity;
	}
	
	public void setEntityFreq(int count) {
		this.entity_count = count;
	}
	
	public int getPostId() {
		return postid;
	}
	
	public String getText() {
		return text;
	}
	
	public int getEntityFreq() {
		return entity_count;
	}
	
	public String getEntity() {
		return entity;
	}
}
