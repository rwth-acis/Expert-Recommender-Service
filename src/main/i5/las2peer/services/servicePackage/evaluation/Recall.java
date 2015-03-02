/**
 * 
 */
package i5.las2peer.services.servicePackage.evaluation;

import i5.las2peer.services.servicePackage.database.UserEntity;
import i5.las2peer.services.servicePackage.utils.Global;

import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * @author sathvik
 *
 */
public class Recall {
	private LinkedHashMap<String, Double> userId2score;
	private int count; // To calculate R@count
	private static final int collectionSize = 50;

	public Recall(LinkedHashMap<String, Double> userId2score, int count) {
		if (count == -1) {
			this.count = Integer.MAX_VALUE;
		} else {
			this.count = count;
		}

		this.userId2score = userId2score;
	}

	private int getTotalRelevantExpertInCollection() {
		int total_relevant_experts_in_collection = 0;
		int i = 0;

		Iterator<String> iterator = this.userId2score.keySet().iterator();
		while (iterator.hasNext() && i < collectionSize) {
			String setElement = iterator.next();
			UserEntity user_entity = Global.userId2userObj1.get(Long
					.valueOf(setElement));
			if (user_entity.isProbableExpert()) {
				total_relevant_experts_in_collection++;
			}
			i++;
		}

		return total_relevant_experts_in_collection;
	}

	public double getValue() {
		double recall_score = 0;
		Iterator<String> iterator = this.userId2score.keySet().iterator();
		int no_of_relevant_experts = 0;
		int i = 0;

		while (iterator.hasNext() && i < this.count) {
			String setElement = iterator.next();
			UserEntity user_entity = Global.userId2userObj1.get(Long
					.valueOf(setElement));
			// System.out.println("Is relevant expert:: "
			// + user_entity.isProbableExpert());
			if (user_entity.isProbableExpert()) {
				no_of_relevant_experts++;
			}
			i++;
		}
		System.out.println("No of relevant expert:: " + no_of_relevant_experts);

		// TODO: Check this again.
		int total_relevant_expert = getTotalRelevantExpertInCollection();
		total_relevant_expert = total_relevant_expert == 0 ? 1
				: total_relevant_expert;

		recall_score = (double) no_of_relevant_experts / total_relevant_expert;

		return recall_score * 100;
	}

	public int getCount() {
		return this.count;
	}
}
