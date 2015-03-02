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
	private int count; // To calculate P@count

	public Recall(LinkedHashMap<String, Double> userId2score, int count) {
		if (count == -1) {
			this.count = Integer.MAX_VALUE;
		} else {
			this.count = count;
		}

		this.userId2score = userId2score;
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

		// We are returning all the possible results for the query, hence recall
		// is always 100%.
		// TODO: Check if this can be modified to be more accurate.
		// Possible missing experts are deliberately removed, for eg. anonymous
		// users may be relevant but ignored for Expert recommenders.
		recall_score = (double) no_of_relevant_experts / no_of_relevant_experts;

		return recall_score * 100;
	}

	// Precision@count.
	public int getCount() {
		return this.count;
	}
}
