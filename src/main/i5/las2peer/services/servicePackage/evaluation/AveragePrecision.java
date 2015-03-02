/**
 * 
 */
package i5.las2peer.services.servicePackage.evaluation;

import i5.las2peer.services.servicePackage.database.UserEntity;
import i5.las2peer.services.servicePackage.utils.Global;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * @author sathvik
 *
 */
public class AveragePrecision {
	private LinkedHashMap<String, Double> userId2score;
	double precision_scores[];
	double averagePrecision = 0;
	int count = 0;

	public AveragePrecision(LinkedHashMap<String, Double> userId2score,
			int count) {
		if (count == -1) {
			this.count = Integer.MAX_VALUE;
		} else {
			this.count = count;
		}

		this.userId2score = userId2score;
	}

	public double getValue() {
		precision_scores = new double[this.userId2score.size()];

		Iterator<String> iterator = this.userId2score.keySet().iterator();
		int no_of_relevant_experts = 0;
		int i = 0;

		while (iterator.hasNext() && i < this.count) {
			String setElement = iterator.next();
			UserEntity user_entity = Global.userId2userObj1.get(Long
					.valueOf(setElement));
			if (user_entity.isProbableExpert()) {
				no_of_relevant_experts++;
				precision_scores[i] = (double) no_of_relevant_experts / (i + 1);
			}
			i++;
		}

		for (int j = 0; j < precision_scores.length; j++) {
			averagePrecision = averagePrecision + precision_scores[j];
		}

		return averagePrecision;

	}

	public void saveAvgPrecision() {
		try (PrintWriter out = new PrintWriter(new BufferedWriter(
				new FileWriter("average_precision_list.txt", true)))) {
			out.println(averagePrecision);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
