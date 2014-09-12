package com.sathvik.models;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class TagMePOJOContainer {

	@SerializedName("annotations")
	List<TagMePOJO> annotations;

	public List<TagMePOJO> getAnnotations() {
		return annotations;
	}

}
