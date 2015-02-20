package i5.las2peer.services.servicePackage.xmlparsers;
import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author sathvik
 */

@XmlRootElement(name = "users")
public class Users {

	@XmlElement(name = "row")
	private ArrayList<User> users;

	@XmlElement(name = "row")
	public void setUsersList(ArrayList<User> users) {
		this.users = users;
	}

	public ArrayList<User> getUsersList() {
		return this.users;
	}
}
