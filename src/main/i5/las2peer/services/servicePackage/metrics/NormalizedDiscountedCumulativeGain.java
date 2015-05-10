/**
 * 
 */
package i5.las2peer.services.servicePackage.metrics;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;

/**
 * @author sathvik
 *
 */
public class NormalizedDiscountedCumulativeGain {
	public static final String normDisCumGainFilepath = "ndcg.txt";
	int[] relevanceScores;
	int[] idealRelevanceScores;
	float ndcg;

	public NormalizedDiscountedCumulativeGain(int[] relevance_scores) {
		relevanceScores = relevance_scores;
		idealRelevanceScores = relevanceScores.clone();

		// Sort in descending order.
		Arrays.sort(idealRelevanceScores);
		ArrayUtils.reverse(idealRelevanceScores);

	}

	public float getIdealDCG() {
		DiscountedCumilativeGain idealDisCumGain = new DiscountedCumilativeGain(
				idealRelevanceScores);
		return idealDisCumGain.getDiscountedCumilativeGain();
	}

	public float[] getDCGValues() {
		DiscountedCumilativeGain dcg = new DiscountedCumilativeGain(
				relevanceScores);
		dcg.getDiscountedCumilativeGain();
		return dcg.getDCGValues();
	}

	public float getDCG() {
		DiscountedCumilativeGain dcg = new DiscountedCumilativeGain(
				relevanceScores);
		float dcgVal = dcg.getDiscountedCumilativeGain();
		dcg.save(); // Optional.
		return dcgVal;
	}

	public float getValue() {
		ndcg = getDCG() / getIdealDCG();
		return ndcg;
	}

	public void save() {
		try (PrintWriter out = new PrintWriter(new BufferedWriter(
				new FileWriter(normDisCumGainFilepath, false)))) {
			out.println("NDCG::" + ndcg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

