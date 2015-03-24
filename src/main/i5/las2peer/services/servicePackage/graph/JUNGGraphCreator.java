package i5.las2peer.services.servicePackage.graph;

import i5.las2peer.services.servicePackage.datamodel.UserEntity;
import i5.las2peer.services.servicePackage.utils.Global;

import java.util.HashMap;
import java.util.Set;

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
		g.addEdge(new RelationshipEdge(v1, v2, label), v1, v2,
				EdgeType.DIRECTED);
		
	}
	
	public Graph<String, RelationshipEdge> getGraph() {
		return g;
	}

	public void createGraph(HashMap<Long, Set<Long>> qus2ans,
			HashMap<Long, Long> postId2userId) {
		// Logger log = LoggerFactory.getLogger(ExpertUtils.class);
		// log.info("Creating JUNG graph...");

		// Stopwatch timer = Stopwatch.createStarted();
		System.out.println("Creating graph...");

		try {
			for (Long key : qus2ans.keySet()) {

				// System.out.println("Key::" + key);
				if (postId2userId.containsKey(key)) {
					long q_user_id = postId2userId.get(key);
					UserEntity user = Global.userId2userObj1.get(q_user_id);
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

					if (q_user_id > 0) {
						// System.out.println("post " + key + " userid " +
						// q_user_id);
						g.addVertex(String.valueOf(q_user_id));
						Set<Long> values = qus2ans.get(key);

						for (Long value : values) {
							Long a_user_id = postId2userId.get(value);
							user = Global.userId2userObj1.get(a_user_id);
							if (a_user_id != null) {
								// if (Global.userId2userObj1.get(a_user_id) !=
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
								createVertex(String.valueOf(a_user_id));
								createEdge(String.valueOf(q_user_id),
										String.valueOf(a_user_id),
										String.valueOf(value));
							}
						}
					}
				} else {
					System.out.println("Doesnot contain this key" + key);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Graph created...");

		System.out.println("Graph created...");
		// System.out.println("Graph Creation Time... " + timer.stop());

		// save2JungGraphML("fitness_graph_jung.graphml");
	}
}
