package i5.las2peer.services.servicePackage.database;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * @author sathvik
 */

@DatabaseTable(tableName = "data")
public class Data {

	@DatabaseField(columnName = "post_id", unique = true, dataType = DataType.LONG, throwIfNull = true, id = true)
	long postId;

	@DatabaseField(columnName = "post_type_id", dataType = DataType.INTEGER)
	int postTypeId;

	@DatabaseField(columnName = "accept_ans_id", dataType = DataType.LONG)
	long acceptAnsId;

	@DatabaseField(columnName = "creation_date", dataType = DataType.LONG_STRING)
	String creationDate;

	@DatabaseField(columnName = "score", dataType = DataType.LONG)
	long score;

	@DatabaseField(columnName = "view_count", dataType = DataType.LONG)
	long viewCount;

	@DatabaseField(columnName = "body", dataType = DataType.STRING_BYTES, format = "ISO-8859-13")
	String body;

	@DatabaseField(columnName = "owner_user_id", dataType = DataType.LONG)
	long ownerUserId;

	@DatabaseField(columnName = "last_editor_user_id", dataType = DataType.LONG)
	long lastEditorUserId;

	@DatabaseField(columnName = "last_edit_date", dataType = DataType.LONG_STRING)
	String lastEditDate;

	@DatabaseField(columnName = "last_activity_date", dataType = DataType.LONG_STRING)
	String lastActivityDate;

	@DatabaseField(columnName = "title", dataType = DataType.STRING_BYTES, format = "ISO-8859-13")
	String title;

	@DatabaseField(columnName = "tags", dataType = DataType.LONG_STRING)
	String tags;

	@DatabaseField(columnName = "answer_count", dataType = DataType.LONG)
	long answerCount;

	@DatabaseField(columnName = "comment_count", dataType = DataType.INTEGER)
	int commentCount;

	@DatabaseField(columnName = "favorite_count", dataType = DataType.LONG)
	long favoriteCount;

	@DatabaseField(columnName = "parent_id", dataType = DataType.LONG)
	long parentId;
	
	@DatabaseField(columnName = "clean_text", dataType = DataType.STRING_BYTES, format = "ISO-8859-13")
	String cleanText;

	// @DatabaseField(generatedId = true)
	// long id;

	public Data() {
		
	}
	
	public void setPostId(long id) {
		this.postId = id;
	}

	public void setPostTypeId(int post_type) {
		this.postTypeId = post_type;
	}

	public void setAcceptAnsId(long id) {
		this.acceptAnsId = id;
	}

	public void setCreationDate(String date) {
		this.creationDate = date;
	}

	public void setScore(long score) {
		this.score = score;
	}

	public void setViewCount(long count) {
		this.viewCount = count;
	}

	public void setOwnerUserId(long id) {
		this.ownerUserId = id;
	}

	public void setLastEditorUserId(long editor_id) {
		this.lastEditorUserId = editor_id;
	}

	public void setLastActivityDate(String date) {
		this.lastActivityDate = date;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public void setAnswerCount(long count) {
		this.answerCount = count;
	}

	public void setCommentCount(int count) {
		this.commentCount = count;
	}

	public void setFavoriteCount(long count) {
		this.favoriteCount = count;
	}

	public void setParentId(long id) {
		this.parentId = id;
	}

	public void setCleanText(String text) {
		this.cleanText = text;
	}

	public void setBody(String text) {
		this.body = text;
	}

	public long getPostId() {
		return this.postId;
	}
	
	public long getAccAnsId() {
		return this.acceptAnsId;
	}
	
	public String getCreationDate() {
		return this.creationDate;
	}
	
	public long getScore() {
		return this.score;
	}
	
	public long getViewCount() {
		return this.viewCount;
	}
	
	public String getBody() {
		return this.body;
	}
	
	public long getOwnerUserId() {
		return this.ownerUserId;
	}
	
	public long getLastEditorUserId() {
		return this.lastEditorUserId;
	}
	
	public String getLastActivityDate() {
		return this.lastActivityDate;
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public String getTags() {
		return this.tags;
	}
	
	public long getAnswerCount() {
		return this.answerCount;
	}
	
	public long getCommentCount() {
		return this.commentCount;
	}
	
	public long getFavoriteCount() {
		return this.favoriteCount;
	}
	
	public String getLastEditDate() {
		return this.lastEditDate;
	}
	
	public int getPostTypeId() {
		return this.postTypeId;
	}
	
	public long getParentId() {
		return this.parentId;
	}
	
	public String getCleanText() {
		return this.cleanText;
	}

}
