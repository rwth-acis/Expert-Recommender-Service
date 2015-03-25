package i5.las2peer.services.servicePackage.scoring;

import i5.las2peer.services.servicePackage.datamodel.UserEntity;
import i5.las2peer.services.servicePackage.graph.RelationshipEdge;
import i5.las2peer.services.servicePackage.utils.Application;

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
	private Graph<String, RelationshipEdge> graph;
	private LinkedHashMap<String, Double> expert2score;
	private Map<Long, UserEntity> userId2userObj;

	public PageRankStrategy(Graph<String, RelationshipEdge> graph,
			Map<Long, UserEntity> userId2userObj) {
		this.graph = graph;
		this.userId2userObj = userId2userObj;
	}

	@Override
	public void executeAlgorithm() {
		PageRank ranker = new PageRank(this.graph, this.alpha);
		ranker.setTolerance(this.tolerance);
		ranker.setMaxIterations(this.maxIterations);
		ranker.evaluate();

		for (String v : graph.getVertices()) {
			node2pagescore.put(v, (Double) ranker.getVertexScore(v));
		}

	}

	@Override
	public String getExperts() {
		expert2score = Application.sortByValue(node2pagescore);

		int i = 0;
		JSONArray jsonArray = new JSONArray();

		for (String userid : expert2score.keySet()) {
			i++;
			// Restrict result to 10 items for now.
			if (i < 10) {
				UserEntity user = userId2userObj.get(Long
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

	public LinkedHashMap<String, Double> getExpertMap() {
		return expert2score;
	}

}
