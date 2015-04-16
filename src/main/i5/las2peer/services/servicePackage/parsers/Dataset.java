package i5.las2peer.services.servicePackage.parsers;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This contains all the users available in the users.xml
 * which is a list of all the user.
 * 
 * @see User
 * @author sathvik
 */

@XmlRootElement(name = "datasets")
public class Dataset {

    @XmlElement(name = "dataset")
    private ArrayList<DatasetInfo> datasetInfos;

    @XmlElement(name = "dataset")
    public void setDatasetInfoList(ArrayList<DatasetInfo> infos) {
	this.datasetInfos = infos;
    }

    public ArrayList<DatasetInfo> getDatsetInfoList() {
	return datasetInfos;
    }
}
