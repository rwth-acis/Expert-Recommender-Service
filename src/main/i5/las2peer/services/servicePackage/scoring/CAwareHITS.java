package i5.las2peer.services.servicePackage.scoring;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.scoring.HITS;
import edu.uci.ics.jung.algorithms.scoring.HITSWithPriors;
import edu.uci.ics.jung.algorithms.scoring.util.ScoringUtils;
import edu.uci.ics.jung.graph.Graph;

/**
 * @author sathvik
 * @param <V>
 */

public class CAwareHITS<V, E> extends HITSWithPriors<V, E> {

	public CAwareHITS(Graph<V, E> g, Transformer<E, Double> edge_weights,
			double alpha) {
		super(g, edge_weights, ScoringUtils.getHITSUniformRootPrior(g
				.getVertices()), alpha);
	}

	public CAwareHITS(Graph<V, E> g, double alpha) {
		super(g, ScoringUtils.getHITSUniformRootPrior(g.getVertices()), alpha);
	}

	/**
	 * Updates the value for this vertex.
	 */
	@Override
	protected double update(V v) {
		collectDisappearingPotential(v);

		// This is to be calculated depending on the Community
		double v_auth = 0;
		for (E e : graph.getInEdges(v)) {
			int incident_count = getAdjustedIncidentCount(e);
			for (V w : graph.getIncidentVertices(e)) {
				if (!w.equals(v) || hyperedges_are_self_loops)
					// Update depending on community.
					v_auth += (getCurrentValue(w).hub
							* getEdgeWeight(w, e).doubleValue() / incident_count);
			}
		}

		double v_hub = 0;
		for (E e : graph.getOutEdges(v)) {
			int incident_count = getAdjustedIncidentCount(e);
			for (V w : graph.getIncidentVertices(e)) {
				if (!w.equals(v) || hyperedges_are_self_loops)
					// Update depending on community.
					v_hub += (getCurrentValue(w).authority
							* getEdgeWeight(w, e).doubleValue() / incident_count);
			}
		}

		// modify total_input according to alpha
		if (alpha > 0) {
			v_auth = v_auth * (1 - alpha) + getVertexPrior(v).authority * alpha;
			v_hub = v_hub * (1 - alpha) + getVertexPrior(v).hub * alpha;
		}
		setOutputValue(v, new HITS.Scores(v_hub, v_auth));

		return Math.max(Math.abs(getCurrentValue(v).hub - v_hub),
				Math.abs(getCurrentValue(v).authority - v_auth));
	}
}
