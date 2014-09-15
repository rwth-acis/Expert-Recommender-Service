package com.sathvik.models;

import com.google.gson.annotations.SerializedName;

public class TagMePOJO {
	@SerializedName("title")
	public String mTitle;
	
	@SerializedName("dbpedia_categories")
	//public ArrayList<String> mCategories;
	public String[] mCategories;
	
	@SerializedName("rho")
	public double confidence;
	
	public TagMePOJO(String title, String[] categories, double rho) {
		mTitle = title;
		mCategories = categories;
		confidence = rho;
	}
	
	
}
