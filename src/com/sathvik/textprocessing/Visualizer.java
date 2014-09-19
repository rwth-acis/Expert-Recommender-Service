package com.sathvik.textprocessing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import com.sathvik.utils.Utils;

public class Visualizer {
	public Visualizer() {
	}

	public void visualize() {
		
		ArrayList<Integer> parentIds = new ArrayList<Integer>();
		Utils.expert_post_ids = Utils.postId2finalWeight.keySet();
		Utils.println("No of candidate experts found: "
				+ Utils.expert_post_ids.size());
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
			saxParser.parse(classLoader.getResourceAsStream("res/Posts.xml"),
					handler);

		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
	}

}
