/**
 * 
 */
package i5.las2peer.services.servicePackage.visualization;

import i5.las2peer.services.servicePackage.datamodel.VisualizationEntity;
import i5.las2peer.services.servicePackage.graph.GraphWriter;
import i5.las2peer.services.servicePackage.graph.JUNGGraphCreator;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;


/**
 * @author sathvik
 *
 */

// TODO: Check factory pattern for this.
public class Visualizer {
    VisualizationEntity visEntity;

    public Visualizer() {

    }

    public void save(long queryId, ConnectionSource connSrc, JUNGGraphCreator jcreator) {
	GraphWriter writer = new GraphWriter(jcreator);
	try {
	    writer.saveToGraphMl("graph_jung.graphml");
	    String graph = writer.getGraphAsString("graph_jung.graphml");

	    GraphMl2GEXFConverter converter = new GraphMl2GEXFConverter();
	    converter.convert("graph_jung.graphml");
	    converter.applyLayout();
	    converter.export("visgraph");

	    try {
		Dao<VisualizationEntity, Long> visulaizationDao = DaoManager.createDao(connSrc, VisualizationEntity.class);

		visEntity = new VisualizationEntity();
		visEntity.setQueryId(queryId);
		visEntity.setCreateDate(new Date());
		visEntity.setGraph(graph);

		visulaizationDao.createIfNotExists(visEntity);

	    } catch (SQLException e) {
		e.printStackTrace();
	    }

	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    public String getGraph() {
	return visEntity.getGraph();
    }

    public long getId() {
	if (visEntity != null) {
	    return visEntity.getId();
	}
	return -1;
    }

}
