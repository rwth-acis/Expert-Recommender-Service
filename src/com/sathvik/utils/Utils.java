package com.sathvik.utils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.sathvik.models.EntityResource;
import com.sathvik.models.Resource;
import com.sathvik.models.TagMePOJO;
import com.sathvik.textprocessing.Main;
import com.sathvik.textprocessing.PorterStemmer;
import com.sathvik.textprocessing.SAXHandler;
import com.sathvik.textprocessing.SemanticTagParser;
import com.sathvik.textprocessing.StopWord;
import com.sathvik.textprocessing.TagMe;

/**
 * @author sathvik, sathvikmail@gmail.com
 * 
 *         This is a prototype implementation of ERS module for thesis.
 *
 */

public final class Utils {
	
	private Utils() {
		
	}
	
	/*
	 * All variables and methods are temporary for the prototype.
	 * 
	 * 
	 * */
	
	public static final int NO_OF_NODES = 5;
	public static final String TAG_ME_KEY = "174baff695d027d98674a1ebcf84d50c";
	public static final String TAGME_URL = "http://tagme.di.unipi.it/tag";

	public static Multiset QUERY_WORDS;
	public static int THRESHOLD_WORD_FREQ = 2;
	public static int THRESHOLD_TERM_FREQ = 0;
	public static double CONFIDENCE_THRESHOLD = 0.06;
	
	public static ArrayList<TagMePOJO> QUERY_TAG_OBJS;
	public static ArrayList<String> QUERY_TAG_TITLES = new ArrayList<String>();

	public static HashMultimap<String, Resource> TERM_FREQ_MAP = HashMultimap
			.create();

	public static HashMultimap<Integer, Double> TERM_FREQ_MAP1 = HashMultimap
			.create();
	
	
	public static HashMultimap<String, EntityResource> ENTITY2FREQ = HashMultimap
			.create();
	
	public static HashMultimap<Integer, Double> POSTID2WEIGHT = HashMultimap
			.create();
	
	public static Map<Integer, Double> postId2termsWeight = new HashMap<Integer,Double>();
	public static Map<Integer, Double> postId2entitiesWeight = new HashMap<Integer,Double>();
	public static Map<Integer, Double> postId2finalWeight = new LinkedHashMap<Integer,Double>();
	
	public static Map<Integer, Double> tester = new LinkedHashMap<Integer,Double>();
			

	public static String query = "Who is the expert to know proteins?";

	// This is temp, may not require in future.
	public static HashMap<Integer, Integer> id2parentid = new HashMap<Integer, Integer>();

	public static Set<Integer> expert_post_ids;
	
	static SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
	static ClassLoader classLoader = Main.class.getClassLoader();

	public static String readFile(String path, Charset encoding)
			throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}

	public static void print(String str) {
		System.out.print(str);
	}

	public static void println(String str) {
		System.out.println(str);
	}

	public static String stem(String sencetence) {

		PorterStemmer stemmer = new PorterStemmer();
		String[] words = sencetence.split("\\s+");
		String stemmed_sentence = "";
		for (String word : words) {
			stemmed_sentence += stemmer.stem(word) + " ";
		}
		return stemmed_sentence;
	}

	// Generic comparator for hashmap on values.
	public static <K, V extends Comparable<V>> Map<K, V> sortMapByValue(
			Map<K, V> map, final boolean order) {
		List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(
				map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
			public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
				if (order)
					return (o2.getValue().compareTo(o1.getValue()));
				else
					return (o1.getValue().compareTo(o2.getValue()));
			}
		});

		Map<K, V> result = new LinkedHashMap<K, V>();
		for (Iterator<Map.Entry<K, V>> it = list.iterator(); it.hasNext();) {
			Map.Entry<K, V> entry = it.next();
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}
	
	public static void createTermWeightMap() {
		// Stopword removal.

		StopWord stopword = new StopWord("res/webconfs_stopwords.txt");

		String clean_sentence = stopword.removeFrom(Utils.query);

		Utils.println("Sentence after stopword removal...");
		Utils.println(clean_sentence);
		Utils.QUERY_WORDS = HashMultiset.create(Splitter
				.on(CharMatcher.WHITESPACE).omitEmptyStrings()
				.split(clean_sentence));

		SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();

		try {
			SAXParser saxParser = saxParserFactory.newSAXParser();
			SAXHandler handler = new SAXHandler();

			saxParser.parse(Main.class.getClassLoader().getResourceAsStream("res/Posts.xml"),
					handler);

		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}

	}

	public static void createEntityWeightMap() {
		// Create entityfreq map

		// Get Tags for the query.
		TagMe tagme = new TagMe("", query);
		Utils.QUERY_TAG_OBJS = tagme.getTags();

		for (TagMePOJO tags : Utils.QUERY_TAG_OBJS) {
			Utils.QUERY_TAG_TITLES.add(tags.mTitle);
			println("titles:"+tags.mTitle);
		}

		try {
			SAXParser saxParser = saxParserFactory.newSAXParser();
			SemanticTagParser handler = new SemanticTagParser();

			saxParser.parse(classLoader.getResourceAsStream("res/joined.xml"),
					handler);

		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
	}


	
	 
	// public static void saveSemanticTags(Class cls, String postid, String
	// tags,
	// ArrayList<TagMePOJO> tagmeObjs) {
	//
	// try {
	// ClassLoader classLoader = cls.getClassLoader();
	//
	// DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
	// .newInstance();
	// DocumentBuilder documentBuilder = documentBuilderFactory
	// .newDocumentBuilder();
	// Document document = documentBuilder.parse(classLoader
	// .getResourceAsStream("res/Posts_tags.xml"));
	// Element root = document.getDocumentElement();
	//
	// // Root Element
	// Element rootElement = document.getDocumentElement();
	// Element row = document.createElement("row");
	// row.setAttribute("postId", postid);
	//
	// if (tags != null) {
	// row.setAttribute("tagCollection", tags);
	// } else {
	// row.setAttribute("tagCollection", "");
	// }
	//
	// rootElement.appendChild(row);
	//
	// for (TagMePOJO obj : tagmeObjs) {
	// Element sTag = document.createElement("semanticTag");
	// sTag.setAttribute("title", obj.mTitle);
	//
	// String categories = StringUtils.join(obj.mCategories, ",");
	//
	// sTag.setAttribute("categories", categories);
	// sTag.setAttribute("rho", "" + obj.confidence);
	// row.appendChild(sTag);
	// }
	//
	// root.appendChild(row);
	//
	// DOMSource source = new DOMSource(document);
	//
	// TransformerFactory transformerFactory = TransformerFactory
	// .newInstance();
	// Transformer transformer = transformerFactory.newTransformer();
	// URL path = classLoader.getResource("res/Posts_tags.xml");
	// StreamResult result = new StreamResult(new File(
	// "src/res/Posts_tags.xml"));
	// println("SAVING..... ::" + path);
	// transformer.transform(source, result);
	//
	// } catch (Exception e) {
	// println("ERROR ::" + e);
	// e.printStackTrace();
	// }
	// }
	
	
//	public static void createEntityFreq(Set<String> queryEntities, Multiset<String> resEntities) {
//		for(String s: queryEntities) {
//			ENTITY2FREQ.put(s, resEntities.count(s));
//		}
//		
//		
//	}
	
	
	 public static Map<Integer,Double> addValues(Map<Integer,Double> a, Map<Integer,Double> b) {
		    Map<Integer,Double> ret = new HashMap<Integer,Double>(a);
		    for (Integer s : b.keySet()) {
		      if (ret.containsKey(s)) {
		        ret.put(s, b.get(s) + ret.get(s));
		      } else {
		        ret.put(s, b.get(s));
		      }
		    }
		    return ret;
	 }
	 
	 public static void printMap(Map mp, int noOfValues) {
			int count = 0 ;
		    Iterator it = mp.entrySet().iterator();
		    
		    while (it.hasNext()) {
		        Map.Entry pairs = (Map.Entry)it.next();
		        System.out.println(pairs.getKey() + " = " + pairs.getValue());
		        it.remove(); // avoids a ConcurrentModificationException
		        count ++;
		        if(noOfValues != -1 && count > noOfValues)
		        	break;
		    }
		}
	 
	 public static void compare(ArrayList<Integer>ids , ArrayList<Integer>ids1) {
			int pos = 0;
			
			for(int i : ids) {
				
				if(i != ids1.get(pos)) {
					Utils.println(ids+"::"+ids1+"::::"+pos);
				}
				pos++;
			}
			
			Utils.print("Finished...");
		}

	public static String TEST_STR = "&lt;p&gt;I've been overweight most of my life and recently I've been slimming down (lost &gt; 50 lbs so far, shooting for 35-40 more) and I'm wanting to start running. I have no problems walking for miles and miles (walked 8 one day just to see how far I could go, never got tired), but when I start running my lungs are on fire after about 1/10th of a mile and I can't continue.&lt;/p&gt;&#xA;&#xA;&lt;p&gt;Is this something that will go away, is there something I can do to improve it, or should I be seeing a doctor about it?&lt;/p&gt;&#xA;&#xA;&lt;p&gt;Also might note I'm an ex-smoker of about 7 years, just quit about 5 months ago as part of my beginning a healthier lifestyle.&lt;/p&gt;&#xA;";
}
