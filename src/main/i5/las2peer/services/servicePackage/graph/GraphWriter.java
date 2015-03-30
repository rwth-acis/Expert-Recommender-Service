/**
 * 
 */
package i5.las2peer.services.servicePackage.graph;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.io.GraphMLWriter;

/**
 * A graph writer class to save the generated graph to a file.
 * 
 * @author sathvik
 *
 */
public class GraphWriter {
    private GraphMLWriterWithAttrNameAndType<String, RelationshipEdge> graphWriter;
    private JUNGGraphCreator creator;

    /**
     * @param creator
     *            An object that holds graph created by node and edge map.
     * @see JUNGGraphCreator#createGraph(java.util.Map, java.util.HashMap)
     * 
     */

    public GraphWriter(JUNGGraphCreator creator) {
	this.graphWriter = new GraphMLWriterWithAttrNameAndType<String, RelationshipEdge>();
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

	PrintWriter out = null;
	out = new PrintWriter(new BufferedWriter(new FileWriter(filename)));

	graphWriter.addVertexData("d3", "name", null, new Transformer<String, String>() {

	    @Override
	    public String transform(String nodeid) {
		return nodeid;
	    }
	});

	HashMap<String, String> key2val = new HashMap<String, String>();
	key2val.put("attr.name", "name");
	key2val.put("attr.type", "string");
	graphWriter.setKeyAttributes(key2val);

	graphWriter.save(creator.getGraph(), out);

    }
}
