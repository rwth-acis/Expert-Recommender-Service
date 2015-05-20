package i5.las2peer.services.servicePackage.parsers.csvparser;

import i5.las2peer.services.servicePackage.parsers.IUser;

import com.googlecode.jcsv.annotations.MapToColumn;

/**
 * 
 * XML binding to the users.xml.
 * 
 * This contains the properties of a user who made the post.
 * jCSV is used to parse the tags of CSV file.
 * 
 * @author sathvik
 */

public class UserCSV implements IUser {

    private String accountId;

    @MapToColumn(column = 3)
    private String userId;

    @MapToColumn(column = 5)
    private String userName;

    private String reputation;

    private String creationDate;

    private String location;

    private String aboutme;

    private String views;

    private String upvotes;

    private String downvotes;

    private String websiteUrl;

    public String getUserAccId() {
	return accountId;
    }

    public String getUserName() {
	return userName;
    }

    public String getReputation() {
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

    public String getViews() {
	return views;
    }

    public String getUpVotes() {
	return upvotes;
    }

    public String getDownVotes() {
	return downvotes;
    }

    public String getUserId() {
	return userId;
    }

    public String getWebsiteUrl() {
	return websiteUrl;
    }

}
