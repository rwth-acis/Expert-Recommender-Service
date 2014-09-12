package com.sathvik.utils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multiset;
import com.sathvik.models.Resource;
import com.sathvik.textprocessing.PorterStemmer;

/**
 * @author sathvik, sathvikmail@gmail.com
 * 
 *         This is a prototype implementation of ERS module for thesis.
 *
 */

public class Utils {
	public static final int NO_OF_NODES = 5;
	public static final String TAG_ME_KEY = "174baff695d027d98674a1ebcf84d50c";
	public static final String TAGME_URL = "http://tagme.di.unipi.it/tag";

	public static Multiset QUERY_WORDS;
	public static int THRESHOLD_WORD_FREQ = 2;
	public static double CONFIDENCE_THRESHOLD = 0.06;
	
	public static HashMultimap<String, Resource> TERM_FREQ_MAP = HashMultimap
			.create();

	public static HashMultimap<Integer, Float> TERM_FREQ_MAP1 = HashMultimap
			.create();

	// This is temp, may not require in future.
	public static HashMap<Integer, Integer> id2parentid = new HashMap<Integer, Integer>();

	public static Set<Integer> expert_post_ids;

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

	public static String TEST_STR = "&lt;p&gt;I've been overweight most of my life and recently I've been slimming down (lost &gt; 50 lbs so far, shooting for 35-40 more) and I'm wanting to start running. I have no problems walking for miles and miles (walked 8 one day just to see how far I could go, never got tired), but when I start running my lungs are on fire after about 1/10th of a mile and I can't continue.&lt;/p&gt;&#xA;&#xA;&lt;p&gt;Is this something that will go away, is there something I can do to improve it, or should I be seeing a doctor about it?&lt;/p&gt;&#xA;&#xA;&lt;p&gt;Also might note I'm an ex-smoker of about 7 years, just quit about 5 months ago as part of my beginning a healthier lifestyle.&lt;/p&gt;&#xA;";
}
