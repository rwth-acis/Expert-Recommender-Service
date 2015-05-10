/**
 * 
 */
package i5.las2peer.services.servicePackage.ocd;

import i5.las2peer.services.servicePackage.utils.Application;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Class for identifying nodes, covers and membership value.
 * 
 * @author sathvik
 *
 */
public class NodeCoverManager {
    private long nodeId;
    private HashMap<Long, Double> coverId2memVal;
    private LinkedHashMap<Long, Double> sortedCoverId2memVal;
    private long dominantCover;


    /**
     * 
     * @param nodeId
     *            A long value specifying the Id of the current node.
     * @param coverId2memVal
     *            A map of coverId and membership value.
     * 
     */
    public NodeCoverManager(long nodeId, HashMap<Long, Double> coverId2memVal) {
	this.nodeId = nodeId;
	this.coverId2memVal = coverId2memVal;
    }

    private void evaluateCovers() {
	this.sortedCoverId2memVal = Application.sortByValue(coverId2memVal);
    }

    /**
     * 
     * @return A long value indicating the Id of the node.
     */
    public long getNodeId() {
	return this.nodeId;
    }

    /**
     * Gets the dominant cover id whose membership value is higher than other
     * covers.
     * 
     * @return A long value indicating the id of the cover. If covers are not
     *         present -1 is returned.
     */
    public long getDominantCover() {
	evaluateCovers();
	if (this.sortedCoverId2memVal != null && this.sortedCoverId2memVal.size() > 0) {
	    dominantCover = (long) sortedCoverId2memVal.keySet().toArray()[0];
	} else {
	    dominantCover = -1;
	}
	return dominantCover;
    }

    /***
     * 
     * @param coverId
     *            A long value indicating the cover.
     * @return A long value indicating the membership of the node with the
     *         specified cover.
     */
    public double getMemberShipValue(long coverId) {
	if (this.sortedCoverId2memVal != null && this.sortedCoverId2memVal.size() > 0) {
	    return this.sortedCoverId2memVal.get(coverId);
	} else {
	    return -1;
	}
    }

}

