package i5.las2peer.services.servicePackage.graph;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;

/**
 * Creates a directed sparse graph G(V,E) with given vertices and edges.
 * 
 * @see <a href="http://en.wikipedia.org/wiki/Directed_graph">Directed graph
 *      </a>
 * 
 * @author sathvik
 */

public class JUNGGraphCreator {
    private Graph<String, RelationshipEdge> graph;

    public JUNGGraphCreator() {
	graph = new DirectedSparseGraph<String, RelationshipEdge>();
    }

    /**
     * This method adds vertex to the graph instance.
     * 
     * @param value
     *            String identifying the vertex.
     * */
    private void createVertex(String value) {
	graph.addVertex(value);
    }

    /**
     * This method connects two vertices with a directed edge.
     * 
     * @param vertex1
     *            String to identify one of the vertex.
     * @param vertex2
     *            String to identify another vertex.
     * @param label
     *            String that gives a name to an edge connecting two vertex.
     * 
     * */
    private void createEdge(String vertex1, String vertex2, String label) {
	graph.addEdge(new RelationshipEdge(vertex1, vertex2, label), vertex1, vertex2, EdgeType.DIRECTED);

    }

    /**
     * 
     * @return Returns a generated graph with vertices and edges.
     * */

    public Graph<String, RelationshipEdge> getGraph() {
	return graph;
    }

    /**
     * This method creates the actual graph using the collection of nodes.
     * Nodes are generally posts made by the user. These nodes can be of two
     * types namely questions and answers differentiated by a type id @see
     * DataEntity#postTypeId
     * 
     * If nodes share a same parentId @see DataEntity#parentId they are
     * connected by an edge. Edge direction is from question to answers.
     * 
     * Every Node is associated with a user.
     * 
     * 
     * @param qus2ans
     *            A map of id of the post to collection of answers to the
     *            particular post.
     * 
     * @param postId2userId
     *            A map of id of the post to the id of the user.
     * 
     * */
    // TODO: May have to pass entire indexer.
    public void createGraph(Map<Long, Collection<Long>> qus2ans, HashMap<Long, Long> postId2userId) {
	// Stopwatch timer = Stopwatch.createStarted();
	System.out.println("Creating graph...");

	try {
	    for (Long key : qus2ans.keySet()) {

		// System.out.println("Key::" + key);
		if (postId2userId.containsKey(key)) {
		    long qUserId = postId2userId.get(key);
		    // UserEntity user =
		    // Application.userId2userObj.get(q_user_id);
		    // if (q_user_id > 0 && user != null) {
		    // // user.setRelatedPost(key);
		    // Set termObjs = postid2Resource1.get(key);
		    // if (termObjs.size() > 0) {
		    // Resource res = (Resource) termObjs.iterator().next();
		    // user.setTitle(res.getText());
		    // } else {
		    // user.setTitle("Title is empty");
		    // }
		    // }

		    if (qUserId > 0) {
			// System.out.println("post " + key + " userid " +
			// q_user_id);
			graph.addVertex(String.valueOf(qUserId));
			Set<Long> values = (Set<Long>) qus2ans.get(key);

			for (Long value : values) {
			    Long aUserId = postId2userId.get(value);
			    // user = Application.userId2userObj.get(a_user_id);
			    if (aUserId != null) {
				// if
				// (Application.userId2userObj1.get(a_user_id)
				// !=
				// null) {
				// user.setRelatedPost(value);
				//
				// Set termObjs = TERM_FREQ_MAP.get(value);
				// if (termObjs.size() > 0) {
				// Resource res = (Resource) termObjs.iterator()
				// .next();
				// user.setTitle(res.getText());
				//
				// } else {
				// user.setTitle("Title is empty");
				// }
				// }
				createVertex(String.valueOf(aUserId));
				createEdge(String.valueOf(qUserId), String.valueOf(aUserId), String.valueOf(value));
			    }
			}
		    }
		} else {
		    // System.out.println("Doesnot contain this key" + key);
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
	System.out.println("Graph created...");
	// System.out.println("Graph Creation Time... " + timer.stop());

	// save2JungGraphML("fitness_graph_jung.graphml");
    }
}
