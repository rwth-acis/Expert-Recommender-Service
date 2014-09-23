package com.sathvik.ers;

import com.sathvik.utils.Utils;

/**
 * @author sathvik, sathvikmail@gmail.com
 * 
 *         This is a prototype implementation of ERS module for thesis.
 *         TermWeightsMap
 *
 */

public class Main {

	public static void main(String args[]) {
		if (args.length > 0) {
			Utils.query = args[0];
		}

		Utils.createTermWeightMap();
				
	//	Visualizer visualizer = new Visualizer();
	//	visualizer.visualize();
	
		//Utils.printMap(Utils.postId2finalWeight, 20);
		//compare(new ArrayList(Utils.postId2finalWeight.keySet()), new ArrayList(Utils.tester.keySet()));
	}
	
	
	
}
