package com.sathvik.viewer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.Collection;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import y.base.Node;
import y.layout.Layouter;
import y.layout.circular.CircularLayouter;
import y.view.Graph2D;
import y.view.Graph2DLayoutExecutor;
import y.view.Graph2DView;
import y.view.NodeRealizer;

import com.google.common.collect.HashMultimap;
import com.sathvik.models.Resource;
import com.sathvik.utils.Utils;

/**
 * @author sathvik, sathvikmail@gmail.com
 * 
 *         Using YFiles to graph the parent child relationship in network.
 *
 */

public class SimpleViewer {
	JFrame frame;
	/** The yFiles view component that displays (and holds) the graph. */
	Graph2DView view;
	/** The yFiles graph type. */
	Graph2D graph;

	public SimpleViewer(Dimension size, String title) {
		view = createGraph2DView();
		graph = view.getGraph2D();
		frame = createApplicationFrame(size, title, view);
	}

	public SimpleViewer() {
		this(new Dimension(400, 300), "");
		frame.setTitle(getClass().getName());
	}

	private Graph2DView createGraph2DView() {
		Graph2DView view = new Graph2DView();
		view.setAntialiasedPainting(true);
		return view;
	}

	/** Creates a JFrame that will show the demo graph. */
	private JFrame createApplicationFrame(Dimension size, String title,
			JComponent view) {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setPreferredSize(size);
		// Add the given view to the panel.
		panel.add(view, BorderLayout.CENTER);
		JFrame frame = new JFrame(title);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getRootPane().setContentPane(panel);
		frame.pack();
		frame.setLocationRelativeTo(null);
		return frame;
	}

	private Layouter createLayouter() {
		CircularLayouter cl = new CircularLayouter();
		cl.setLayoutStyle(CircularLayouter.BCC_ISOLATED);
		return cl;

	}

	/** Creates a simple graph structure. */
	public void populateGraph(Graph2D graph,
			HashMultimap<Integer, Resource> nodemap) {
		graph.clear();
		//Utils.print("NODEMAP SIZE::" + nodemap.keySet().size());

		for (int parentId : nodemap.keySet()) {

			NodeRealizer parentNodeRealizer = graph.getDefaultNodeRealizer();
			parentNodeRealizer.setWidth(50);
			parentNodeRealizer.setLabelText("" + parentId);
			Node parent = graph.createNode();

			Collection<Resource> collections = nodemap.get(parentId);
			for (Resource r : collections) {
				NodeRealizer childNodeRealizer = graph.getDefaultNodeRealizer();
				if (Utils.expert_post_ids.contains(r.getPostId())) {
					childNodeRealizer.setFillColor(Color.RED);
				} else {
					childNodeRealizer.setFillColor(Color.GREEN);
				}

				childNodeRealizer.setWidth(50);
				childNodeRealizer.setLabelText("" + r.getPostId());
				Node child = graph.createNode();
				graph.createEdge(parent, child);
			}

		}

		new Graph2DLayoutExecutor().doLayout(graph, createLayouter());
		getView().fitContent();
		graph.updateViews();

	}

	public void show() {
		frame.setVisible(true);
	}

	public Graph2DView getView() {
		return view;
	}

	public Graph2D getGraph() {
		return graph;
	}

}
