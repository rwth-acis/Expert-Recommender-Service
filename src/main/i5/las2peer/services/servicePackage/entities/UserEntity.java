package i5.las2peer.services.servicePackage.entities;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * An ORMLite persistence object mapped to "users" table in the
 * corresponding MySQL database.
 * This entity will hold the values from the "data" table columns.
 * Every field of the class specifies the mapping of the table column name,
 * data type and constraints if any.
 * 
 * @author sathvik
 */

@DatabaseTable(tableName = "users")
public class UserEntity {
    @DatabaseField(columnName = "accountId", dataType = DataType.LONG)
    private long accountId;

    @DatabaseField(columnName = "userId", dataType = DataType.LONG, unique = true, id = true)
    private long userId;

    @DatabaseField(columnName = "userName")
    private String userName;

    @DatabaseField(columnName = "reputation", dataType = DataType.LONG)
    private long reputation;

    @DatabaseField(columnName = "creationDate")
    private String creationDate;

    @DatabaseField(columnName = "location")
    private String location;

    @DatabaseField(columnName = "aboutme", dataType = DataType.LONG_STRING)
    private String aboutme;

    @DatabaseField(columnName = "views", dataType = DataType.LONG)
    private long views;

    @DatabaseField(columnName = "upvotes", dataType = DataType.LONG)
    private long upvotes;

    @DatabaseField(columnName = "downvotes", dataType = DataType.LONG)
    private long downvotes;

    @DatabaseField(columnName = "websiteUrl", dataType = DataType.LONG_STRING)
    private String websiteUrl;

    @DatabaseField(columnName = "no_of_posts", dataType = DataType.LONG)
    private long noOfPosts;

    // Inserted after calculation by the evaluation module.
    @DatabaseField(columnName = "probable_expert", dataType = DataType.BOOLEAN)
    private boolean probableExpert = false; // Used for evaluation.

    // This is calculated by algorithm.
    private double score;

    public void setUserAccId(long id) {
	this.accountId = id;
    }

    public void setUserName(String username) {
	this.userName = username;
    }

    public void setReputation(long rep) {
	this.reputation = rep;
    }

    public void setCreationDate(String date) {
	this.creationDate = date;
    }

    public void setLocation(String location) {
	this.location = location;
    }

    public void setAbtMe(String abtme) {
	this.aboutme = abtme;
    }

    public void setViews(long views) {
	this.views = views;
    }

    public void setUpVotes(long votes) {
	this.upvotes = votes;
    }

    public void setDownVotes(long votes) {
	this.downvotes = votes;
    }

    public void setUserId(long id) {
	this.userId = id;
    }

    public void setWebsiteUrl(String url) {
	this.websiteUrl = url;
    }

    public void setScore(double score) {
	this.score = score;
    }

    public void setNoOfPosts(long noOfPosts) {
	this.noOfPosts = noOfPosts;
    }

    public long getUserAccId() {
	return accountId;
    }

    public String getUserName() {
	return userName;
    }

    public long getReputation() {
	return reputation;
    }

    public String getCreationDate() {
	return creationDate;
    }

    public String getLocation() {
	return location;
    }

    public String getAbtMe() {
	return aboutme;
    }

    public long getViews() {
	return views;
    }

    public long getUpVotes() {
	return upvotes;
    }

    public long getDownVotes() {
	return downvotes;
    }

    public long getUserId() {
	return userId;
    }

    public String getWebsiteUrl() {
	return websiteUrl;
    }

    public double getScore() {
	return this.score;
    }

    public long getNoOfPosts() {
	return noOfPosts;
    }

    public boolean isProbableExpert() {
	return probableExpert;
    }

}
