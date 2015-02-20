package i5.las2peer.services.servicePackage.graph;

import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;

/**
 * @author sathvik
 */

public class JUNGGraphCreator {
	Graph<String, RelationshipEdge> g;
	public JUNGGraphCreator() {
		 g = new DirectedSparseGraph<String, RelationshipEdge>();
	}
	
	public void createVertex(String value) {
		g.addVertex(value);
	}
	
	public void createEdge(String v1, String v2, String label) {
		g.addEdge(new RelationshipEdge<String>(v1, v2, label),v1, v2,EdgeType.DIRECTED);
		
	}
	
	public Graph<String, RelationshipEdge> getGraph() {
		return g;
	}
}
