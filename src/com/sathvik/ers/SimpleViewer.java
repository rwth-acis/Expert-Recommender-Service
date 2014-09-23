package com.sathvik.ers;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import y.base.Node;
import y.layout.Layouter;
import y.layout.circular.CircularLayouter;
import y.view.EdgeRealizer;
import y.view.Graph2D;
import y.view.Graph2DLayoutExecutor;
import y.view.Graph2DView;
import y.view.NodeRealizer;

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
		this(new Dimension(700, 700), "");
		frame.setTitle(getClass().getName());
	}

	private Graph2DView createGraph2DView() {
		Graph2DView view = new Graph2DView();
		view.setAntialiasedPainting(true);
		return view;
	}

	/** Creates a JFrame that will show the graph. */
	private JFrame createApplicationFrame(Dimension size, String title,
			JComponent view) {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setPreferredSize(size);
		// Add the given view to the panel.
		panel.add(view, BorderLayout.CENTER);
	    panel.add(createToolBar(), BorderLayout.NORTH);

	    
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
	public void populateGraph(Graph2D graph, SimpleIndexedMatrix sim) {

		// Utils.print("NODEMAP SIZE::" + nodemap.keySet().size());

		graph.clear();
		double[] users = sim.getIndexArray();
		Utils.println(" ");

		if (Utils.SHOW_USER == Utils.SHOW_ALL) {
			for (int i = 0; i < sim.numRows(); i++) {
				showUser(users[i], sim);
			}
		} else {
			showUser(Utils.SHOW_USER, sim);
		}

		new Graph2DLayoutExecutor().doLayout(graph, createLayouter());
		getView().fitContent();
		graph.updateViews();

	}

	public void showUser(double userId, SimpleIndexedMatrix sim) {
		int index = sim.find(userId);
		double[] users = sim.getIndexArray();

		NodeRealizer nodeRealizer = graph.getDefaultNodeRealizer();
		EdgeRealizer edgeRealizer = graph.getDefaultEdgeRealizer();

		nodeRealizer.setLabelText("" + users[index]);
		nodeRealizer.setHeight(50);
		nodeRealizer.setWidth(70);

		Node parent = graph.createNode();

		for (int i = 0; i < sim.numCols(); i++) {
			if (sim.get(index, i) >= 1) {
				nodeRealizer.setLabelText(users[i] + "");
				nodeRealizer.setHeight(50);
				nodeRealizer.setWidth(70);
				Node child = graph.createNode();

				edgeRealizer.setLabelText(" " + sim.get(index, i));
				graph.createEdge(parent, child);

				Utils.println("User:" + users[i] + " Post:" + sim.get(index, i));
			}
		}

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

	/** Creates a toolbar for this demo. */
	protected JToolBar createToolBar() {
		JToolBar toolbar = new JToolBar();
		toolbar.add(new FitContent(getView()));
		toolbar.add(new Zoom(getView(), 1.25));
		toolbar.add(new Zoom(getView(), 0.8));
		return toolbar;
	}
	
	/** Action that fits the content nicely inside the view. */
	  protected static class FitContent extends AbstractAction {
	    Graph2DView view;
	    
	    public FitContent(Graph2DView view) {
	      super("Fit Content");
	      this.view = view;
	      this.putValue(Action.SHORT_DESCRIPTION, "Fit Content");
	    }
	    
	    public void actionPerformed(ActionEvent e) {
	      view.fitContent();
	      view.updateView();
	    }
	  }


}


