package i5.las2peer.services.servicePackage.scorer;

import i5.las2peer.services.servicePackage.entities.UserEntity;
import i5.las2peer.services.servicePackage.graph.RelationshipEdge;
import i5.las2peer.services.servicePackage.ocd.NodeCoverManager;
import i5.las2peer.services.servicePackage.utils.Application;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import net.minidev.json.JSONArray;
import edu.uci.ics.jung.algorithms.scoring.HITS;
import edu.uci.ics.jung.graph.Graph;

/**
 * @author sathvik
 */

public class CommunityAwareHITSStrategy implements ScoreStrategy {
    public Map<String, Double> node2hitsscore = new HashMap<String, Double>();

    private int maxIterations = 30;
    private double tolerance = 0.0000001d;
    private double alpha = 0d;
    private Graph<String, RelationshipEdge> graph;
    private LinkedHashMap<String, Double> expert2score;
    private Map<Long, UserEntity> userId2userObj;
    private HashMap<Long, NodeCoverManager> nodeId2Covers;

    /**
     * 
     * @param graph
     * @param userId2userObj
     * @param nodeId2Covers
     */
    public CommunityAwareHITSStrategy(Graph<String, RelationshipEdge> graph, Map<Long, UserEntity> userId2userObj,
	    HashMap<Long, NodeCoverManager> nodeId2Covers) {
	this.graph = graph;
	this.userId2userObj = userId2userObj;
	this.nodeId2Covers = nodeId2Covers;
    }

    /**
     * 
     */
    @Override
    public void executeAlgorithm() {

	CAwareHITS<String, RelationshipEdge> ranker = new CAwareHITS<String, RelationshipEdge>(graph, alpha, nodeId2Covers);

	ranker.setTolerance(tolerance);
	ranker.setMaxIterations(maxIterations);
	ranker.evaluate();

	for (String v : graph.getVertices()) {
	    HITS.Scores scores = (HITS.Scores) ranker.getVertexScore(v);
	    node2hitsscore.put(v, scores.authority);
	}

    }

    /**
     * 
     */
    @Override
    public String getExperts() {
	expert2score = Application.sortByValue(node2hitsscore);

	int i = 0;
	JSONArray jsonArray = new JSONArray();

	for (String userid : expert2score.keySet()) {
	    i++;
	    // Restrict result to 10 items for now.
	    if (i < 10) {
		UserEntity user = userId2userObj.get(Long.parseLong(userid));
		user.setScore(node2hitsscore.get(userid));
		if (user != null) {
		    jsonArray.add(user);
		}
	    } else {
		break;
	    }
	}

	return jsonArray.toJSONString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * i5.las2peer.services.servicePackage.scoring.ScoreStrategy#getExpertMap()
     */
    @Override
    public LinkedHashMap<String, Double> getExpertMap() {
	return expert2score;
    }

}
