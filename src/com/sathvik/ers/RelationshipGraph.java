package com.sathvik.ers;

import java.awt.Dimension;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.sathvik.models.Resource;
import com.sathvik.ers.SimpleViewer;

public class RelationshipGraph {
	SimpleIndexedMatrix sim;
	private Set<Integer> users = new HashSet<Integer>();
	private HashMap<Integer, Resource> postId2res = new HashMap<Integer, Resource>();

	public RelationshipGraph(HashMap<Integer, Resource> Obj) {
		postId2res = Obj;

		for (Integer id : Obj.keySet()) {
			Resource r = Obj.get(id);
			if (r.getUserId() != -1) {
				users.add(r.getUserId());
			}

		}

		sim = new SimpleIndexedMatrix(users);

	}

	private void setValue(int rowVal, int colVal, double val) {
		sim.setValue(rowVal, colVal, val);
	}

	private double getValue(int rowVal, int colVal) {
		return sim.getValue(rowVal, colVal);
	}

	public void create() {
		for (Integer postid : postId2res.keySet()) {
			Resource pivotRes = postId2res.get(postid);
			int pivotParentId = pivotRes.getParentId();
			int userid = pivotRes.getUserId();

			for (Integer id : postId2res.keySet()) {
				Resource res = postId2res.get(id);
				if (userid != -1 && res.getUserId() != -1
						&& pivotParentId == res.getParentId()) {

					// Set the corresponding row n col.
					setValue(userid, res.getUserId(), pivotParentId);
					setValue(res.getUserId(), userid, pivotParentId);

				}

			}
		}

		
		//sim.print();
	}

	
	public void visualize() {
		sim.printRow(sim.find(879));
		
		SimpleViewer sv = new SimpleViewer(new Dimension(700, 700),
				SimpleViewer.class.getName());
		sv.populateGraph(sv.getGraph(), sim);
		sv.show();
	}
}
