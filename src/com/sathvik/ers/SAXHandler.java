package com.sathvik.ers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
	public SAXHandler() {
	}
	

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {

		Resource resource;
		if (qName.equalsIgnoreCase("row")) {
			totalNoOfPost++;
			String id = attributes.getValue("Id");
			String tags = attributes.getValue("Tags");
			String bodyText = attributes.getValue("Body");
			String parentId = attributes.getValue("ParentId");
			String userId = attributes.getValue("OwnerUserId");
			
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
					if(parentId != null && parentId.length() >0) {
						resource.setParentId(Integer.parseInt(parentId));
					} else {
						//This helps in future steps when comparing parentIDs for relationship graph.
						resource.setParentId(Integer.parseInt(id));
					}
					
					if(userId != null && userId.length() > 0) {
						resource.setUserId(Integer.parseInt(userId));
					} else {
						resource.setUserId(-1);
					}
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
			double irfweight = Math.log(totalNoOfPost
					/ collections.size());
			for (Resource r : collections) {
				// With irf weight
				Utils.TERM_FREQ_MAP1.put(r.getPostId(),
						r.getTermFreq() * irfweight);
				
				//Creating map of filtered(according to query) id to Resource object. 
				Utils.postid2resObj.put(r.getPostId(), r);
				
				// Without irf weight.
				// Utils.TERM_FREQ_MAP1.put(r.getPostId(), r.getTermFreq());
			}

		}

		//Calculating final weight(normalized term weights) for each resource.
		//Map<Integer, Float> termfreq_map = new HashMap<Integer, Float>();
		for (Integer postid : Utils.TERM_FREQ_MAP1.keys()) {
			Collection<Double> term_freq = Utils.TERM_FREQ_MAP1.get(postid);
			
			//Normalizing by sum of all term count.
			double sum_freq = 0;
			for (double i : term_freq)
				sum_freq += i;

			double sum = 0;
			for (Double freq : term_freq) {
				//sum = sum + (freq / sum_freq);
				sum = sum + freq;
			}
			Utils.postId2termsWeight.put(postid, sum);
			//Utils.println("Sum of Term Freq of postId: "+postid+": "+sum);
		}
		
		//Utils.println("Finished parsing Posts...");
		
		RelationshipGraph rGraph = new RelationshipGraph(Utils.postid2resObj);
		rGraph.create();
		rGraph.visualize();

		
//		Map<Integer, Double> sorted_termfreq_map = Utils.sortMapByValue(
//				Utils.postId2termsWeight, true);
//		ArrayList<Integer> parentIds = new ArrayList<Integer>();
//		Utils.expert_post_ids = sorted_termfreq_map.keySet();
//		Utils.println("No of candidate experts found: "+Utils.expert_post_ids.size());
//		int i = 0;
//		for (Integer postid : Utils.expert_post_ids) {
//			if (i < Utils.NO_OF_NODES) {
//				// Utils.println("Sum of Term Freq of postId: "+postid+": "+termfreq_map.get(postid));
//				if (Utils.id2parentid.containsKey(postid)) {
//					int parentid = Utils.id2parentid.get(postid);
//					if (parentid != 0) {
//						parentIds.add(new Integer(parentid));
//					}
//				}
//			}
//			i++;
//		}
//
//		Utils.println("Visualizing few experts and related candidates... ");
//		// RelationCreator rCreator = new RelationCreator(parentIds);
//
//		SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
//		ClassLoader classLoader = getClass().getClassLoader();
//		try {
//			SAXParser saxParser = saxParserFactory.newSAXParser();
//			RelationCreator handler = new RelationCreator(parentIds);
//			saxParser.parse(classLoader.getResourceAsStream("res/Posts.xml"), handler);
//
//		} catch (ParserConfigurationException | SAXException | IOException e) {
//			e.printStackTrace();
//		}
	
	}

}
