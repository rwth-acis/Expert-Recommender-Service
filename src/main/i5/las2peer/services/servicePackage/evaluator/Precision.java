/**
 * 
 */
package i5.las2peer.services.servicePackage.evaluator;

import i5.las2peer.services.servicePackage.datamodel.UserEntity;
import i5.las2peer.services.servicePackage.utils.Application;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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
	double precision_scores[];
	double averagePrecision = 0;
	private Map<Long, UserEntity> userId2userObj;

	public Precision(LinkedHashMap<String, Double> userId2score,
			Map<Long, UserEntity> userId2userObj, int count) {
		if (count == -1) {
			this.count = Integer.MAX_VALUE;
		} else {
			this.count = count;
		}
		this.userId2score = userId2score;
		this.userId2userObj = userId2userObj;
	}

	public double getValue() {
		double precision_score = 0;
		Iterator<String> iterator = this.userId2score.keySet().iterator();
		int no_of_relevant_experts = 0;
		int i = 0;

		while (iterator.hasNext() && i < this.count) {
			String setElement = iterator.next();
			UserEntity user_entity = userId2userObj.get(Long
					.valueOf(setElement));
			System.out.println("Is relevant expert:: "
					+ user_entity.isProbableExpert());
			if (user_entity.isProbableExpert()) {
				no_of_relevant_experts++;
			}
			i++;
		}
		System.out.println("No of relevant expert:: " + no_of_relevant_experts);
		precision_score = (double) no_of_relevant_experts / this.count;

		return precision_score * 100;
	}

	public double getAveragePrecision() {
		precision_scores = new double[this.userId2score.size()];
		precision_scores = getAllPrecisionValues();
		for (int j = 0; j < precision_scores.length; j++) {
			averagePrecision = averagePrecision + precision_scores[j];
		}

		return averagePrecision;
	}

	public double[] getRoundedValues() {
		ArrayList<Double> rounded_precision_values = new ArrayList<Double>();

		for (int i = 0; i < precision_scores.length && i < count; i++) {
			rounded_precision_values.add(Application.round(precision_scores[i],
					2));
		}

		Double[] values = rounded_precision_values
				.toArray(new Double[rounded_precision_values.size()]);
		return ArrayUtils.toPrimitive(values);
	}

	public double[] getAllPrecisionValues() {
		Iterator<String> iterator = this.userId2score.keySet().iterator();
		int no_of_relevant_experts = 0;
		int i = 0;

		while (iterator.hasNext() && i < this.count) {
			String setElement = iterator.next();
			UserEntity user_entity = userId2userObj.get(Long
					.valueOf(setElement));
			if (user_entity != null && user_entity.isProbableExpert()) {
				no_of_relevant_experts++;
			}

			precision_scores[i] = (double) no_of_relevant_experts / (i + 1);
			i++;
		}
		return precision_scores;
	}

	public void saveAvgPrecisionToFile() {
		try (PrintWriter out = new PrintWriter(new BufferedWriter(
				new FileWriter("average_precision_list.txt", true)))) {
			out.println(averagePrecision);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void savePrecisionValuesToFile() {
		try (PrintWriter out = new PrintWriter(new BufferedWriter(
				new FileWriter("precision_list.txt", false)))) {
			for (int i = 0; i < precision_scores.length; i++) {
				// System.out.println("ROUNDED:: "
				// + Application.round(precision_scores[i], 1));
				out.println(Application.round(precision_scores[i], 2));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// Precision@count.
	public int getCount() {
		return this.count;
	}
}
