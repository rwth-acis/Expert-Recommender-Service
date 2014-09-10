package com.sathvik.textprocessing;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.sathvik.models.Resource;
import com.sathvik.utils.Utils;

public class SAXHandler extends DefaultHandler {

	private int totalNoOfPost = 0;

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {

		Resource resource;
		if (qName.equalsIgnoreCase("row")) {
			totalNoOfPost++;
			String id = attributes.getValue("Id");
			String bodyText = attributes.getValue("Body");
			String parentId = attributes.getValue("ParentId");

			// Temp, may not be required in future.
			if (parentId != null && parentId.length() > 0) {
				Utils.id2parentid.put(new Integer(id), new Integer(parentId));
			}

			// Split the text into multiset to count the frequency.
			Multiset<String> bagOfWords = HashMultiset.create(Splitter
					.on(CharMatcher.WHITESPACE).omitEmptyStrings()
					.split(bodyText));
			int count = 0;
			// Iterate the Query terms. 
			//Ignore if freq is too less.
			for (Object word : Utils.QUERY_WORDS.elementSet()) {
				count = bagOfWords.count(word);
				if (count > Utils.THRESHOLD_WORD_FREQ) {
					resource = new Resource(new Integer(id), "");
					resource.setTerm((String) word);
					resource.setTermFreq(count);
					Utils.TERM_FREQ_MAP.put((String) word, resource);
				}

				// Gson gson = new Gson();
				// Utils.println("JSON STR "+gson.toJson(Utils.TERM_FREQ_MAP.asMap()));
				// Utils.println(word+"::"+count);
			}

			// Utils.println(id);
			// Utils.println(bodyText);
			// Utils.println("COUNT: "+count);

			// Maybe Create resource Obj.
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (qName.equalsIgnoreCase("row")) {
			// Not required now
		}
	}

	@Override
	public void characters(char ch[], int start, int length)
			throws SAXException {
		String text = new String(ch, start, length);
		// Not required now.
	}

	public void endDocument() throws SAXException {
		//Utils.println("Document reached its end");
		HashMap<String, Integer> idfmap = new HashMap<String, Integer>();

		// Find Term freq for all the posts in inverted index.
		for (Object term : Utils.TERM_FREQ_MAP.keys()) {
			Collection<Resource> collections = Utils.TERM_FREQ_MAP
					.get((String) term);
			float irfweight = (float) Math.log(totalNoOfPost
					/ collections.size());
			// int irfweight = (int) Math.pow((int)
			// (totalNoOfPost/collections.size()),2);

			//Utils.println("TERM::" + term);
			//Utils.println("WEIGHT::" + irfweight);

			// idfmap.put((String)term, (int)idfweight);
			for (Resource r : collections) {
				// With irf weight
				Utils.TERM_FREQ_MAP1.put(r.getPostId(),
						new Float(r.getTermFreq()) * (int) irfweight);

				// Without irf weight.
				// Utils.TERM_FREQ_MAP1.put(r.getPostId(), r.getTermFreq());
			}

		}

		Map<Integer, Float> termfreq_map = new HashMap<Integer, Float>();
		for (Integer postid : Utils.TERM_FREQ_MAP1.keys()) {
			Collection<Float> term_freq = Utils.TERM_FREQ_MAP1.get(postid);
			
			//Normalizing by sum of all term count.
			float sum_freq = 0;
			for (float i : term_freq)
				sum_freq += i;

			float sum = 0;
			for (Float freq : term_freq) {
				if (freq != sum_freq) {
					//Utils.print("FREQ: " + freq + "::");
					//Utils.println("MAX FREQ: " + sum_freq);
				}
				sum = sum + (freq / sum_freq);
				// sum = sum + freq;
			}
			termfreq_map.put(postid, sum);
			// Utils.println("Sum of Term Freq of postId: "+postid+": "+sum);
		}

		Map<Integer, Float> sorted_termfreq_map = Utils.sortMapByValue(
				termfreq_map, true);
		ArrayList<Integer> parentIds = new ArrayList<Integer>();
		Utils.expert_post_ids = sorted_termfreq_map.keySet();
		Utils.println("No of candidate experts found: "+Utils.expert_post_ids.size());
		int i = 0;
		for (Integer postid : Utils.expert_post_ids) {
			if (i < Utils.NO_OF_NODES) {
				// Utils.println("Sum of Term Freq of postId: "+postid+": "+termfreq_map.get(postid));
				if (Utils.id2parentid.containsKey(postid)) {
					int parentid = Utils.id2parentid.get(postid);
					if (parentid != 0) {
						parentIds.add(new Integer(parentid));
					}
				}
			}
			i++;
		}

		Utils.println("Visualizing few experts and related candidates... ");
		// RelationCreator rCreator = new RelationCreator(parentIds);

		SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
		ClassLoader classLoader = getClass().getClassLoader();
		try {
			SAXParser saxParser = saxParserFactory.newSAXParser();
			RelationCreator handler = new RelationCreator(parentIds);
			saxParser.parse(classLoader.getResourceAsStream("res/Posts.xml"), handler);

		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}

	}

}
