/**
 * 
 */
package i5.las2peer.services.servicePackage.evaluation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author sathvik
 *
 */
public class PrecisionRecall {
	public static final String precision_filepath = "precision_list.txt";
	public static final String recall_filepath = "recall_list.txt";
	public static final String precision_recall_filepath = "precision_recall_list.txt";
	// HashMap<Double, Double> recallValues2PrecisionValue = new HashMap<Double,
	// Double>();

	LinkedHashMap<Double, Double> recall2precision = new LinkedHashMap<Double, Double>();

	public PrecisionRecall(double[] precisions, double[] recalls) {
		int i = 0;
		System.out.println(precisions.length + " " + recalls.length);
		while (i < precisions.length && i < recalls.length) {
			recall2precision.put(recalls[i], precisions[i]);
			i++;
		}
	}

	public LinkedHashMap<Double, Double> getRecallPrecisionMap() {
		return recall2precision;
	}

	public void savePrecisionRecallCSV() throws IOException {

		try (PrintWriter out = new PrintWriter(new BufferedWriter(
				new FileWriter(precision_recall_filepath, false)))) {
			for (Map.Entry<Double, Double> mapEntry : recall2precision
					.entrySet()) {

				double recall = mapEntry.getKey();
				double precision = mapEntry.getValue();

				String text_to_write = precision + "," + recall;

				out.println(text_to_write);

			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	 public void savePrecisionRecallCSV1() throws IOException {
	 BufferedReader precisionBr = new BufferedReader(new FileReader(
	 precision_filepath));
	 BufferedReader recallBr = new BufferedReader(new FileReader(
	 recall_filepath));
	
	 // Open FileWriter, Read the two files line by line and append values.
	 try (PrintWriter out = new PrintWriter(new BufferedWriter(
	 new FileWriter(precision_recall_filepath, false)))) {
	
	 while (true) {
	 String precision = precisionBr.readLine();
	 String recall = recallBr.readLine();
	
	 if (precision == null || recall == null)
	 break;
	
				// recallValues2PrecisionValue.put(
				// Global.round(Double.parseDouble(recall), 1),
				// Global.round(Double.parseDouble(precision), 1));
	 String text_to_write = precision + "," + recall;
	
	 out.println(text_to_write);
	
	 }

		} catch (IOException e) {
			e.printStackTrace();
		}

		precisionBr.close();
		recallBr.close();
	}

	// public HashMap<Double, Double> getRecall2PrecisionMap() {
	// return recallValues2PrecisionValue;
	// }
}
