/**
 * 
 */
package i5.las2peer.services.servicePackage.evaluator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author sathvik
 *
 */
public class DiscountedCumilativeGain {
	public static final String disCumGainFilepath = "dcg.txt";
	int[] relevanceScores;
	float[] discounted_gain_list;
	float[] dcgains;

	public DiscountedCumilativeGain(int[] relevance_scores) {
		relevanceScores = relevance_scores;
	}

	public float getDiscountedCumilativeGain() {
		discounted_gain_list = new float[relevanceScores.length];
		dcgains = new float[relevanceScores.length];
		if (relevanceScores.length > 1) {

			// These two can be merged, but for the clarity of testing, keeping
			// it separate.
			discounted_gain_list[0] = relevanceScores[0];
			dcgains[0] = discounted_gain_list[0];

			for (int i = 1; i < relevanceScores.length; i++) {
				discounted_gain_list[i] = (float) (((float) relevanceScores[i] / Math
						.log(i + 1)) * Math.log(2));
				dcgains[i] = dcgains[i - 1] + discounted_gain_list[i];
			}
		}

		return dcgains[dcgains.length - 1];
	}

	public float[] getDCGValues() {
		return dcgains;
	}

	public void save() {
		try (PrintWriter out = new PrintWriter(new BufferedWriter(
				new FileWriter(disCumGainFilepath, false)))) {

			for (int i = 0; i < discounted_gain_list.length; i++) {
				out.println(discounted_gain_list[i]);
			}
			if (dcgains.length > 0) {
				out.println("DCG::" + dcgains[dcgains.length - 1]);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
