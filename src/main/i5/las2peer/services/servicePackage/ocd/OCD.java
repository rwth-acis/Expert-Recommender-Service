/**
 * 
 */
package i5.las2peer.services.servicePackage.ocd;

import i5.las2peer.services.servicePackage.parsers.SimpleDOMParser;

import java.util.ArrayList;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.NodeList;

/**
 * @author sathvik
 *
 */
public class OCD {
    private String graphId;
    private String coverId;

    public static final String basepath = "";

    public OCD() {

    }

    private String getBasicAuthEncodedString() {
	byte[] encoding = Base64.encodeBase64("Sathvik:dzJODeM4l5OmQVh".getBytes());
	return new String(encoding);
    }

    /**
     * 
     * Response from the service is of the format.
     *
     * @param filename
     *            String value that will be used to save the graph on the
     *            server.
     * @param graphContent
     *            String containing details about the graph, generally an xml
     *            string.
     */
    public void uploadGraph(String filename, String graphContent) {
	try {

	    HttpPost httppost = new HttpPost("https://api.learning-layers.eu/ocd/graphs?name=" + filename);
	    httppost.addHeader("Authorization", "Basic " + getBasicAuthEncodedString());

	    System.out.println("--------------GRAPH CONTENT-----------------------");
	    System.out.println(new String(graphContent));

	    ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
	    postParameters.add(new BasicNameValuePair("name", "testing_graph_upload"));
	    httppost.setEntity(new StringEntity(graphContent));

	    CloseableHttpClient httpclient = HttpClients.createDefault();
	    CloseableHttpResponse response = httpclient.execute(httppost);
	    System.out.println("-----------UPLOAD GRAPH-----------------------------");

	    HttpEntity entity = response.getEntity();
	    String responseString = EntityUtils.toString(entity, "UTF-8");

	    System.out.println(responseString);

	    int statusCode = response.getStatusLine().getStatusCode();
	    EntityUtils.consume(entity);

	    // Parse the XML response to get the id of the graph.
	    if (statusCode == 200 && responseString != null && responseString.length() > 0) {
		SimpleDOMParser parser = new SimpleDOMParser(responseString);
		NodeList list = parser.getNodes("Id");
		if (list != null) {
		    for (int i = 0; i < list.getLength(); i++) {
			graphId = list.item(i).getTextContent();
		    }
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
	System.out.println("GraphId = " + graphId);

    }

    /**
     * This method executes a OCD service to identify covers in the graph.
     * 
     * Response string of the format.
     * 
     */
    public int identifyCovers() {
	int statusCode = -1;

	try {

	    String url = "https://api.learning-layers.eu/ocd/covers/graphs/%s/algorithms";
	    url = String.format(url, graphId);

	    StringEntity strentity = new StringEntity("<?xml version=\"1.0\" encoding=\"UTF-16\"?><Parameters></Parameters>");

	    HttpPost httppost = new HttpPost(url);
	    httppost.addHeader("Authorization", "Basic " + getBasicAuthEncodedString());
	    httppost.setEntity(strentity);

	    CloseableHttpClient httpclient = HttpClients.createDefault();
	    CloseableHttpResponse response = httpclient.execute(httppost);

	    // System.out.println("------------------IDENTIFYING COVERS----------------------");
	    HttpEntity entity = response.getEntity();
	    String responseString = EntityUtils.toString(entity, "UTF-8");

	    statusCode = response.getStatusLine().getStatusCode();
	    System.out.println("Status Code::" + statusCode);
	    EntityUtils.consume(entity);
	    // <?xml version="1.0" encoding="UTF-16"?>
	    // <Cover><Id><CoverId>434</CoverId><GraphId>226</GraphId></Id></Cover>

	    // Parse the XML response to get the id of the graph.
	    if (statusCode == 200 && responseString != null && responseString.length() > 0) {
		SimpleDOMParser parser = new SimpleDOMParser(responseString);
		NodeList list = parser.getNodes("CoverId");
		if (list != null) {
		    for (int i = 0; i < list.getLength(); i++) {
			coverId = list.item(i).getTextContent();
		    }
		}
	    }

	    System.out.println("COVER ID:: " + coverId);

	} catch (Exception e) {
	    e.printStackTrace();
	}
	return statusCode;
    }

    /**
     */
    public String getCovers() {
	String url = "https://api.learning-layers.eu/ocd/covers/%s/graphs/%s";
	url = String.format(url, coverId, graphId);
	String responseString = null;
	try {

	    HttpGet httpget = new HttpGet(url);
	    httpget.setHeader("Authorization", "Basic " + getBasicAuthEncodedString());

	    CloseableHttpClient httpclient = HttpClients.createDefault();
	    CloseableHttpResponse response = httpclient.execute(httpget);

	    System.out.println("-----------------COVERS--------------------");

	    HttpEntity entity = response.getEntity();
	    responseString = EntityUtils.toString(entity, "UTF-8");
	    System.out.println(responseString);
	    int statusCode = response.getStatusLine().getStatusCode();
	    EntityUtils.consume(entity);

	} catch (Exception e) {
	    e.printStackTrace();
	}

	return responseString;
    }

}
