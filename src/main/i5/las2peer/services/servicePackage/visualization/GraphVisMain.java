/**
 * 
 */
package i5.las2peer.services.servicePackage.visualization;

import java.io.IOException;

/**
 * @author sathvik
 *
 */
public class GraphVisMain {
	public static void main(String args[]) {
		GraphMl2GEXFConverter converter = new GraphMl2GEXFConverter();
		try {
			converter.convert("fitness_graph_jung.graphml");
			converter.applyLayout();
			converter.export("fitness_graph_jung");

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
