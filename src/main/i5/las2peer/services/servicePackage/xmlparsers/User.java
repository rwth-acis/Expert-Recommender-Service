package i5.las2peer.services.servicePackage.xmlparsers;


import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author sathvik
 */

@XmlRootElement(name = "row")
public class User {
	@XmlAttribute(name = "AccountId")
	private String accountId;
	
	@XmlAttribute(name = "Id")
	private String userId;

	@XmlAttribute(name = "DisplayName")
	private String userName;
	
	@XmlAttribute(name = "Reputation")
	private String reputation;
	
	@XmlAttribute(name = "CreationDate")
	private String creationDate;
	
	@XmlAttribute(name = "Location")
	private String location;
	
	@XmlAttribute(name = "AboutMe")
	private String aboutme;
	
	@XmlAttribute(name = "Views")
	private String views;
	
	@XmlAttribute(name = "UpVotes")
	private String upvotes;
	
	@XmlAttribute(name = "DownVotes")
	private String downvotes;
	
	@XmlAttribute(name = "WebsiteUrl")
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
