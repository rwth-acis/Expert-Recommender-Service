package i5.las2peer.services.servicePackage.datamodel;

import java.util.Date;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * An ORMLite persistence object mapped to "visualization" table in the
 * corresponding MySQL database.
 * This entity will hold the details about the visualization, a graph.
 * 
 * @author sathvik
 */

@DatabaseTable(tableName = "visulaization")
public class VisualizationEntity {

    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField(columnName = "query_id", dataType = DataType.LONG)
    private long queryId;

    @DatabaseField(columnName = "date_created", dataType = DataType.DATE)
    private Date createdAt;

    // A json string consisting of all metric details.
    @DatabaseField(columnName = "vis_data", dataType = DataType.LONG_STRING)
    private String visData;

    public VisualizationEntity() {

    }

    public void setQueryId(long queryId) {
	this.queryId = queryId;
    }

    public void setCreateDate(Date date) {
	this.createdAt = date;
    }

    public void setGraph(String graph) {
	visData = graph;
    }

    public long getId() {
	return id;
    }

    public long getQueryId() {
	return queryId;
    }

    public Date getCreateDate() {
	return createdAt;
    }

    public String getGraph() {
	return visData;
    }
}
