package com.sathvik.textprocessing;

import java.util.Collection;
import java.util.HashMap;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.sathvik.models.EntityResource;
import com.sathvik.models.Resource;
import com.sathvik.utils.Utils;

public class SemanticTagParser extends DefaultHandler {

	private int totalNoOfPost = 0;
	private String postId = "";
	// private ArrayList<String> tags;
	private Multiset<String> tags = HashMultiset.create();

	TagCreator tagCreator;

	public SemanticTagParser() {
		tagCreator = new TagCreator();
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {

		Resource resource;
		if (qName.equalsIgnoreCase("row")) {
			totalNoOfPost++;
			postId = attributes.getValue("postId");

			// Gson gson = new Gson();
			// Utils.println("JSON STR "+gson.toJson(Utils.TERM_FREQ_MAP.asMap()));
			// Utils.println(word+"::"+count);

			// Utils.println(id);
			// Utils.println(bodyText);
			// Utils.println("COUNT: "+count);

			// Maybe Create resource Obj.
		} else if (qName.equals("semanticTag")) {
			if (attributes.getIndex("title") != -1) {
				String title = attributes.getValue("title");
				tags.add(title);
			}
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {

		if (qName.equalsIgnoreCase("row")) {

			EntityResource entityRes;
			int count = 0;
			for (String title : Utils.QUERY_TAG_TITLES) {
				count = tags.count(title);
				if (count > Utils.THRESHOLD_TERM_FREQ) {
					if (postId != null) {
						entityRes = new EntityResource(new Integer(postId), "");
						entityRes.setEntity(title);
						entityRes.setEntityFreq(count);
						Utils.ENTITY2FREQ.put(title, entityRes);
					} else {
						Utils.println("Post id is null...");
					}
				}

			}

			tags.clear();
		}
	}

	@Override
	public void characters(char ch[], int start, int length)
			throws SAXException {
		String text = new String(ch, start, length);
		// Not required now.
	}

	public void endDocument() throws SAXException {

		// Utils.println("Document reached its end");
		HashMap<String, Double> iefmap = new HashMap<String, Double>();

		// Find entity freq for all the posts in inverted index.
		for (Object entity : Utils.ENTITY2FREQ.keys()) {
			Collection<EntityResource> collections = Utils.ENTITY2FREQ
					.get((String) entity);

			double iefweight = Math.log(totalNoOfPost / collections.size());
			//Utils.println("WEight::" + iefweight);

			iefmap.put((String) entity, iefweight);
			for (EntityResource er : collections) {
				Utils.POSTID2WEIGHT.put(er.getPostId(), er.getEntityFreq()
						* iefweight);

				// Without ief weight.
				// Utils.POSTID2WEIGHT.put(er.getPostId(),new
				// Double(er.getEntityFreq()));
			}

		}

		// Calculating final weight(normalized entity weights) for each
		// resource.

		for (Integer postid : Utils.POSTID2WEIGHT.keys()) {
			Collection<Double> entity_freqs = Utils.POSTID2WEIGHT.get(postid);

			// Normalizing by sum of all term count.

			double sum_freq = 0;
			for (double i : entity_freqs)
				sum_freq += i;

			double sum = 0;
			for (Double freq : entity_freqs) {
				// sum = sum + (freq / sum_freq);
				sum = sum + freq;
			}
			//Utils.postId2entitiesWeight.put(postid, sum);
			//Utils.println("Sum of Entity Freq of postId: " + postid + ": "
			//		+ sum);
		}

		Utils.println("Parsing semantics tags finished...");

	}
}
