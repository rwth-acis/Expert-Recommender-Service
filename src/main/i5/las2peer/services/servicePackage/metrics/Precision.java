/**
 * 
 */
package i5.las2peer.services.servicePackage.metrics;

import i5.las2peer.services.servicePackage.entities.UserEntity;
import i5.las2peer.services.servicePackage.utils.Application;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;

/**
 * @author sathvik
 *
 */
public class Precision {
    private LinkedHashMap<String, Double> userId2score;
    private int count; // To calculate P@count
    private double precisions[];
    private double avgPrecision = 0;

    private Map<Long, UserEntity> userId2userObj;

    public Precision(LinkedHashMap<String, Double> userId2score, Map<Long, UserEntity> userId2userObj, int count) {
	if (count == -1) {
	    this.count = Integer.MAX_VALUE;
	} else {
	    this.count = count;
	}
	this.userId2score = userId2score;
	this.userId2userObj = userId2userObj;
    }

    public void compute() {
	precisions = new double[this.userId2score.size()];
	double cumulativePrecision = 0;

	Iterator<String> iterator = this.userId2score.keySet().iterator();
	int noRelevantExperts = 0;
	int i = 0;

	while (iterator.hasNext() && i < this.count) {
	    String userId = iterator.next();
	    UserEntity userEntity = userId2userObj.get(Long.valueOf(userId));

	    // Calculate precision value only at relevant experts.
	    if (userEntity.isProbableExpert()) {
		noRelevantExperts++;
		precisions[i] = (double) noRelevantExperts / (i + 1);
	    } else {
		precisions[i] = 0;
	    }
	    i++;
	}

	for (int j = 0; j < this.count; j++) {
	    cumulativePrecision += precisions[j];
	}

	avgPrecision = (double) cumulativePrecision / noRelevantExperts;

    }

    // This is the average precision for the set of result.
    public double getValue() {
	return avgPrecision * 100;
    }

    public double[] getRoundedPrecisionValues() {
	ArrayList<Double> roundedPrecisionValues = new ArrayList<Double>();

	for (int i = 0; i < precisions.length && i < count; i++) {
	    roundedPrecisionValues.add(Application.round(precisions[i], 2));
	}

	Double[] values = roundedPrecisionValues.toArray(new Double[roundedPrecisionValues.size()]);
	return ArrayUtils.toPrimitive(values);
    }

    // Precision@count.
    public int getCount() {
	return this.count;
    }
}
