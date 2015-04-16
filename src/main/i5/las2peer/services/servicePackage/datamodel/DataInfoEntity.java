package i5.las2peer.services.servicePackage.datamodel;

import java.util.Date;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * An ORMLite persistence object mapped to "data_info" table.
 * This holds the detail about the relative filepath of the original files and
 * indexed directories.
 * 
 * @author sathvik
 */

@DatabaseTable(tableName = "data_info")
public class DataInfoEntity {
    // (id, upload_filepath, lucene_index_filepath, timestamp)

    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField(columnName = "original_filepath", dataType = DataType.LONG_STRING)
    private String filepath;

    @DatabaseField(columnName = "lucene_index_filepath", dataType = DataType.LONG_STRING)
    private String lucenIndexFilepath;

    @DatabaseField(columnName = "date_created", dataType = DataType.DATE)
    private Date date;

    DataInfoEntity() {

    }

    public long getId() {
	return id;
    }

    public String getIndexFilepath() {
	return lucenIndexFilepath;
    }

    public Date getDate() {
	return date;
    }
}
