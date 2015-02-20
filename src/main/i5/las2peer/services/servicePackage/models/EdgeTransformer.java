package i5.las2peer.services.servicePackage.models;
import i5.las2peer.services.servicePackage.graph.RelationshipEdge;

import org.apache.commons.collections15.Transformer;

/**
 * @author sathvik
 */

public class EdgeTransformer implements Transformer<RelationshipEdge, String> {

	@Override
	public String transform(RelationshipEdge edge) {
		return edge.toString();
	}

}

