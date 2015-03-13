/**
 * 
 */
package i5.las2peer.services.servicePackage.parsers;

import i5.las2peer.services.servicePackage.database.Data;
import i5.las2peer.services.servicePackage.database.MySqlHelper;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

/**
 * @author sathvik
 *
 */
public class CSVParser {
	String filepath;
	ArrayList<Data> dataList;

	public CSVParser(String path) {
		// post_id;topic_id;forum_id;poster_id;post_time;post_username;post_subject;post_text
		filepath = path;
	}

	public void parse() throws IOException, SQLException {
		Reader in = new FileReader(filepath);
		Iterable<CSVRecord> records = CSVFormat.EXCEL.withDelimiter(';')
				.withIgnoreEmptyLines().withRecordSeparator('\n')
				.withHeader().parse(in);

		Data data = null;
		dataList = new ArrayList<Data>();

		for (CSVRecord record : records) {

			String postId = record.get("post_id");
			String parentId = record.get("topic_id");
			String userId = record.get("poster_id");
			String creationDate = record.get("post_time");
			String userName = record.get("post_username");
			String title = record.get("post_subject");
			String body = record.get("post_text");
			String owner_user_id = record.get("poster_id");

			data = new Data();
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

	public void saveRecordsToDb() throws SQLException {
		MySqlHelper.createDatabase("consruction");
		ConnectionSource connectionSrc = MySqlHelper
				.createConnectionSource("consruction");

		// Create Data table.
		TableUtils.createTableIfNotExists(connectionSrc, Data.class);
		if (dataList != null && dataList.size() > 0)
		MySqlHelper.createAndInsertResourceDAO(connectionSrc, dataList);

	}
}

