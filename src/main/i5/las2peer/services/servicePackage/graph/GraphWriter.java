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
 * A graph writer class to save the generated graph to a file.
 * 
 * @author sathvik
 *
 */
public class GraphWriter {
    private GraphMLWriter<String, RelationshipEdge> graphWriter;
    private JUNGGraphCreator creator;

    /**
     * @param creator
     *            An object that holds graph created by node and edge map.
     * @see JUNGGraphCreator#createGraph(java.util.Map, java.util.HashMap)
     * 
     */

    public GraphWriter(JUNGGraphCreator creator) {
	this.graphWriter = new GraphMLWriter<String, RelationshipEdge>();
	this.creator = creator;
    }

    /**
     * A method that saves the content of creator to the file in GraphML format.
     * 
     * @see <a href="http://en.wikipedia.org/wiki/GraphML">GraphML</a>
     * @see GraphMLWriter
     * 
     * @param filename
     *            Name of the file where graph will be saved.
     * @throws IOException
     */

    public void saveToGraphMl(String filename) throws IOException {

	// graphWriter.addEdgeData("label", "label for an edge", "0", null);
	// graphWriter.setEdgeData(jcreator.getGraph().getEndpoints(arg0));
	PrintWriter out = null;
	out = new PrintWriter(new BufferedWriter(new FileWriter(filename)));

	graphWriter.save(creator.getGraph(), out);

    }
}
