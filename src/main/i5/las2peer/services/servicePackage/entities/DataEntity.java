package i5.las2peer.services.servicePackage.entities;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * An ORMLite persistence object mapped to "data" table in the corresponding
 * MySQL database.
 * This entity will hold the values from the "data" table columns.
 * Every field of the class specifies the mapping of the table column name,
 * data type and constraints if any.
 * 
 * @author sathvik
 * 
 */

@DatabaseTable(tableName = "data")
public class DataEntity {

    @DatabaseField(columnName = "post_id", unique = true, dataType = DataType.LONG, throwIfNull = true, id = true)
    private long postId;

    @DatabaseField(columnName = "post_type_id", dataType = DataType.INTEGER)
    private int postTypeId;

    @DatabaseField(columnName = "accept_ans_id", dataType = DataType.LONG)
    private long acceptAnsId;

    @DatabaseField(columnName = "creation_date", dataType = DataType.LONG_STRING)
    private String creationDate;

    @DatabaseField(columnName = "score", dataType = DataType.LONG)
    private long score;

    @DatabaseField(columnName = "view_count", dataType = DataType.LONG)
    private long viewCount;

    @DatabaseField(columnName = "body", dataType = DataType.STRING_BYTES)
    private String body;

    @DatabaseField(columnName = "owner_user_id", dataType = DataType.LONG)
    private long ownerUserId;

    @DatabaseField(columnName = "last_editor_user_id", dataType = DataType.LONG)
    private long lastEditorUserId;

    @DatabaseField(columnName = "last_edit_date", dataType = DataType.LONG_STRING)
    private String lastEditDate;

    @DatabaseField(columnName = "last_activity_date", dataType = DataType.LONG_STRING)
    private String lastActivityDate;

    @DatabaseField(columnName = "title", dataType = DataType.STRING_BYTES)
    private String title;

    @DatabaseField(columnName = "tags", dataType = DataType.LONG_STRING)
    String tags;

    @DatabaseField(columnName = "answer_count", dataType = DataType.LONG)
    private long answerCount;

    @DatabaseField(columnName = "comment_count", dataType = DataType.INTEGER)
    private int commentCount;

    @DatabaseField(columnName = "favorite_count", dataType = DataType.LONG)
    private long favoriteCount;

    @DatabaseField(columnName = "parent_id", dataType = DataType.LONG)
    private long parentId;

    @DatabaseField(columnName = "clean_text", dataType = DataType.STRING_BYTES)
    private String cleanText;

    // @DatabaseField(generatedId = true)
    // long id;

    public DataEntity() {

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
	return acceptAnsId;
    }

    public String getCreationDate() {
	return creationDate;
    }

    public long getScore() {
	return score;
    }

    public long getViewCount() {
	return viewCount;
    }

    public String getBody() {
	return body;
    }

    public long getOwnerUserId() {
	return ownerUserId;
    }

    public long getLastEditorUserId() {
	return lastEditorUserId;
    }

    public String getLastActivityDate() {
	return lastActivityDate;
    }

    public String getTitle() {
	return title;
    }

    public String getTags() {
	return this.tags;
    }

    public long getAnswerCount() {
	return answerCount;
    }

    public long getCommentCount() {
	return commentCount;
    }

    public long getFavoriteCount() {
	return favoriteCount;
    }

    public String getLastEditDate() {
	return lastEditDate;
    }

    public int getPostTypeId() {
	return postTypeId;
    }

    public long getParentId() {
	return parentId;
    }

    public String getCleanText() {
	return cleanText;
    }

}
