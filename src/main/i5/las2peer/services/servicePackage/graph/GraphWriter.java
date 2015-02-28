/**
 * 
 */
package i5.las2peer.services.servicePackage.graph;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import edu.uci.ics.jung.io.GraphMLWriter;

/**
 * @author sathvik
 *
 */
public class GraphWriter {
	GraphMLWriter<String, RelationshipEdge> graphWriter;
	JUNGGraphCreator jcreator;

	public GraphWriter(JUNGGraphCreator creator) {
		graphWriter = new GraphMLWriter<String, RelationshipEdge>();
		jcreator = creator;
	}

	public void saveToGraphMl(String filename) {

		// graphWriter.addEdgeData("label", "label for an edge", "0", null);

		// graphWriter.setEdgeData(jcreator.getGraph().getEndpoints(arg0));
		PrintWriter out = null;
		try {
			out = new PrintWriter(new BufferedWriter(new FileWriter(filename)));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			graphWriter.save(jcreator.getGraph(), out);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
