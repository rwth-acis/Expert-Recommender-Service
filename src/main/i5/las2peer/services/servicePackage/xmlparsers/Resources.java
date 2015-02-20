package i5.las2peer.services.servicePackage.xmlparsers;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author sathvik
 */

@XmlRootElement(name = "posts")
public class Resources {
	@XmlElement(name = "row")
	private List<Resource> resources;

	@XmlElement(name = "row")
	public void setResources(ArrayList<Resource> resources) {
		this.resources = resources;
	}

	public List<Resource> getResources() {
		return resources;
	}
}
