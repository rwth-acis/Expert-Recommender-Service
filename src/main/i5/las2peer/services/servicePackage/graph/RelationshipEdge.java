package i5.las2peer.services.servicePackage.graph;


/**
 * @author sathvik
 */

public class RelationshipEdge {
	private String v1;
	private String v2;
    private String label;

	public RelationshipEdge(String v1, String v2, String label) {
        this.v1 = v1;
        this.v2 = v2;
        this.label = label;
    }

	public String getV1() {
        return v1;
    }

	public String getV2() {
        return v2;
    }

    public String toString() {
        return label;
    }
}