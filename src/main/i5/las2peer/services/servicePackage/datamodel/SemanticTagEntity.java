package i5.las2peer.services.servicePackage.datamodel;


import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * @author sathvik
 */

@DatabaseTable(tableName = "semantics1")
public class SemanticTagEntity {
	@DatabaseField(columnName = "post_id", dataType = DataType.LONG, id = true, unique = true)
	private long postId;
	
	@DatabaseField(columnName = "annotations", dataType = DataType.LONG_STRING)
	private String annotations;

	@DatabaseField(columnName = "tags", dataType = DataType.LONG_STRING)
	private String tags;
	
	public long getPostId() {
		return postId;
	}

	public String getTags() {
		return tags;
	}

	public String getAnnotations() {
		return annotations;
	}

	public void setPostId(long id) {
		this.postId = id;
	}

	public void setAnnotations(String annotations) {
		this.annotations = annotations;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

}
