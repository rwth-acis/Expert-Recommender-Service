package com.sathvik.textprocessing;

import java.util.ArrayList;

import com.sathvik.utils.Utils;

/**
 * @author sathvik, sathvikmail@gmail.com
 * 
 *         This is a prototype implementation of ERS module for thesis. Create 2
 *         maps. TermWeightsMap + EntityWeightsMap
 *
 */

public class Main {

	public static void main(String args[]) {
		if (args.length > 0) {
			Utils.query = args[0];
		}

		Utils.createTermWeightMap();
		Utils.createEntityWeightMap();
				
		//Utils.tester = Utils.sortMapByValue(Utils.postId2termsWeight, true);
		
		Utils.postId2finalWeight = Utils.addValues(Utils.postId2termsWeight,
				Utils.postId2entitiesWeight);
		Utils.postId2finalWeight = Utils.sortMapByValue(Utils.postId2finalWeight, true);
		
		Visualizer visualizer = new Visualizer();
		visualizer.visualize();
	
		//Utils.printMap(Utils.postId2finalWeight, 20);
		//compare(new ArrayList(Utils.postId2finalWeight.keySet()), new ArrayList(Utils.tester.keySet()));
	}
	
	
	
}
