/**
 * 
 */
package i5.las2peer.services.servicePackage.parsers;

import i5.las2peer.services.servicePackage.entities.DataEntity;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

/**
 * A CSV parser when the data is available in the comma separated value.
 * 
 * @author sathvik
 *
 */
public class CSVParser {
    private String filepath;
    private ArrayList<DataEntity> dataList;

    public CSVParser(String path) {
	// post_id;topic_id;forum_id;poster_id;post_time;post_username;post_subject;post_text
	filepath = path;
    }

    /**
     * Parses each record in the CSV file.
     * 
     * @throws IOException
     * @throws SQLException
     */
    public void parse() throws IOException, SQLException {
	Reader in = new FileReader(filepath);
	Iterable<CSVRecord> records = CSVFormat.EXCEL.withDelimiter(';').withIgnoreEmptyLines().withRecordSeparator('\n').withHeader().parse(in);

	DataEntity data = null;
	dataList = new ArrayList<DataEntity>();

	for (CSVRecord record : records) {

	    String postId = record.get("post_id");
	    String parentId = record.get("topic_id");
	    String userId = record.get("poster_id");
	    String creationDate = record.get("post_time");
	    String userName = record.get("post_username");
	    String title = record.get("post_subject");
	    String body = record.get("post_text");
	    String owner_user_id = record.get("poster_id");

	    data = new DataEntity();
	    data.setPostId(Long.parseLong(postId));
	    data.setParentId(Long.parseLong(parentId));
	    data.setTitle(title);
	    data.setBody(body);
	    data.setCreationDate(creationDate);
	    data.setOwnerUserId(Long.parseLong(owner_user_id));
	    dataList.add(data);

	    // System.out.println(postId + " " + parentId + " " + userId + " "
	    // + creationDate + " " + userName + " " + title + " " + body);

	}

    }

    /**
     * This method adds the parsed records to the database.
     * 
     * @throws SQLException
     */
    public void saveRecordsToDb() throws SQLException {
	// MySqlHelper.createDatabase("consruction");
	// ConnectionSource connectionSrc = MySqlHelper
	// .createConnectionSource("consruction");

	// Create Data table.
	// TableUtils.createTableIfNotExists(connectionSrc, DataEntity.class);
	// TODO: Apply refactored class MySqlOpenHelper.
	// if (dataList != null && dataList.size() > 0)
	// MySqlHelper.createAndInsertResourceDAO(connectionSrc, dataList);

    }
}
