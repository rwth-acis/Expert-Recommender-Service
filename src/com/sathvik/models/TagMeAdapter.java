package com.sathvik.models;

import java.io.IOException;
import java.util.ArrayList;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.sathvik.utils.Utils;

/**
 * @author sathvik
 * 
 * Adapter to read the json values.
 * Objects are created iff rho values are above threshold value.
 * 
 * 
 * */


public class TagMeAdapter extends TypeAdapter<TagMePOJO> {

	@Override
	public TagMePOJO read(JsonReader in) throws IOException {
		String title = "";
		double rho = 0;
		ArrayList<String> categories = new ArrayList<String>();
		
		in.beginObject();
		
		while (in.hasNext()) {
			String name = in.nextName();
			//Utils.println("TOKEN::" + name);

			switch (name) {
			case "title":
				title = in.nextString();
				break;
			case "dbpedia_categories":
				categories = new ArrayList<String>();
				in.beginArray();
				while (in.hasNext()) {
					categories.add(in.nextString());
				}
				in.endArray();
				break;
			case "rho":
				rho = in.nextDouble();
				break;
			default:
				in.skipValue();
				break;

			}
		}

		in.endObject();

		if (rho > Utils.CONFIDENCE_THRESHOLD) {
			return new TagMePOJO(title, categories.toArray(new String[categories.size()]), rho);
		}
		return null;
	}

	@Override
	public void write(JsonWriter writer, TagMePOJO arg1) throws IOException {
		if (arg1 == null) {
			writer.nullValue();
			return;
		}
	}

}
