package i5.las2peer.services.servicePackage.scoring;

import i5.las2peer.services.servicePackage.database.UserEntity;
import i5.las2peer.services.servicePackage.graph.RelationshipEdge;
import i5.las2peer.services.servicePackage.utils.Global;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import net.minidev.json.JSONArray;
import edu.uci.ics.jung.algorithms.scoring.PageRank;
import edu.uci.ics.jung.graph.Graph;

/**
 * @author sathvik
 */

public class PageRankStrategy implements ScoreStrategy {

	private Map<String, Double> node2pagescore = new HashMap<String, Double>();
	private int maxIterations = 30;
	private double tolerance = 0.0000001d;
	private double alpha = 0.15d;
	Graph<String, RelationshipEdge> graph;

	public PageRankStrategy(Graph<String, RelationshipEdge> graph) {
		this.graph = graph;
	}

	@Override
	public String executeAlgorithm() {
		PageRank ranker = new PageRank(this.graph, this.alpha);
		ranker.setTolerance(this.tolerance);
		ranker.setMaxIterations(this.maxIterations);
		ranker.evaluate();


		for (String v : graph.getVertices()) {
			node2pagescore.put(v, (Double) ranker.getVertexScore(v));
		}

		return getExpertsList();
	}

	private String getExpertsList() {
		LinkedHashMap<String, Double> experts = Global
				.sortByValue(node2pagescore);

		int i = 0;
		JSONArray jsonArray = new JSONArray();

		for (String userid : experts.keySet()) {
			i++;
			// Restrict result to 10 items for now.
			if (i < 10) {
				UserEntity user = Global.userId2userObj1.get(Long
						.parseLong(userid));
				user.setScore(node2pagescore.get(userid));
				if (user != null) {
					jsonArray.add(user);
				}
			} else {
				break;
			}
		}

		// MeanReciprocalRank mrr = new MeanReciprocalRank();
		// System.out.println("RANK FOR THE QUERY..."
		// + mrr.getReciprocalRank(experts));

		return jsonArray.toJSONString();
	}

}
