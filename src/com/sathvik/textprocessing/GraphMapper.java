package com.sathvik.textprocessing;

import java.awt.Dimension;

import com.google.common.collect.HashMultimap;
import com.sathvik.models.Resource;
import com.sathvik.utils.Utils;
import com.sathvik.viewer.SimpleViewer;

public class GraphMapper {
	// HashMultimap<Resource,Resource> Parent2ChildNodes;
	HashMultimap<Integer, Resource> Parent2ChildNodes;

	public GraphMapper() {
		Parent2ChildNodes = HashMultimap.create();
	}

	public void putNode(int parentId, Resource child) {
		Parent2ChildNodes.put(parentId, child);
	}

	public HashMultimap<Integer, Resource> getNodeMap() {
		return Parent2ChildNodes;
	}
	
	public int getSize() {
		return Parent2ChildNodes.keySet().size();
	}

	public void showViewer() {
		SimpleViewer sv = new SimpleViewer(new Dimension(700, 700),
				SimpleViewer.class.getName());
		//Utils.println("SHOW VIEWER::::"+Parent2ChildNodes.keySet().size());
		sv.populateGraph(sv.getGraph(), Parent2ChildNodes);
		sv.show();
	}
}
