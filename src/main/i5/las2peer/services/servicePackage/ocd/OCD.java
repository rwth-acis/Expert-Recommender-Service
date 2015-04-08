/**
 * 
 */
package i5.las2peer.services.servicePackage.ocd;

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

/**
 * @author sathvik
 *
 */
public class OCD {
    public static final String basepath = "";
    String url = "graphs/name/expertnetwork_graph/REAL_WORLD/inputFormat/GRAPH_ML";

    public OCD() {

    }

    private String getBasicAuthEncodedString() {
	byte[] encoding = Base64.encodeBase64("Sathvik:dzJODeM4l5OmQVh".getBytes());
	return new String(encoding);
    }

    /**
     * 
     * @param graphId
     *            String value which is an id of the graph.
     */
    public void getCovers(String graphId) {
	// TODO Do a request to OCD web service.

	try {

	    HttpGet httpget = new HttpGet("https://api.learning-layers.eu/ocd/graphs");
	    httpget.setHeader("Authorization", "Basic " + getBasicAuthEncodedString());

	    CloseableHttpClient httpclient = HttpClients.createDefault();
	    CloseableHttpResponse response = httpclient.execute(httpget);

	    System.out.println("----------------------------------------");
	    System.out.println(response.getStatusLine());

	    HttpEntity entity = response.getEntity();
	    String responseString = EntityUtils.toString(entity, "UTF-8");
	    System.out.println(responseString);
	    int statusCode = response.getStatusLine().getStatusCode();
	    System.out.println("Status Code::" + statusCode);
	    EntityUtils.consume(entity);

	    System.out.println("----------------------------------------");

	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    /**
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

	    // System.out.println("--------------GRAPH CONTENT-----------------------");
	    // System.out.println(graphContent);

	    ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
	    postParameters.add(new BasicNameValuePair("name", "testing_graph_upload"));
	    httppost.setEntity(new StringEntity(graphContent));

	    CloseableHttpClient httpclient = HttpClients.createDefault();
	    CloseableHttpResponse response = httpclient.execute(httppost);

	    System.out.println("----------------------------------------");
	    System.out.println(response.getStatusLine());

	    HttpEntity entity = response.getEntity();
	    String responseString = EntityUtils.toString(entity, "UTF-8");
	    System.out.println(responseString);
	    int statusCode = response.getStatusLine().getStatusCode();
	    System.out.println("Status Code::" + statusCode);
	    EntityUtils.consume(entity);

	    System.out.println("----------------------------------------");

	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    /**
     * 
     * @param graphId
     *            A unique string value for the graph, generally received from
     *            the server.
     */
    public int identifyCovers(String graphId) {
	int statusCode = -1;

	try {

	    String url = "https://api.learning-layers.eu/ocd/covers/graphs/%s/algorithms?algorithm=SPEAKER_LISTENER_LABEL_PROPAGATION_ALGORITHM";
	    url = String.format(url, graphId);

	    HttpPost httppost = new HttpPost(url);
	    httppost.addHeader("Authorization", "Basic " + getBasicAuthEncodedString());

	    CloseableHttpClient httpclient = HttpClients.createDefault();
	    CloseableHttpResponse response = httpclient.execute(httppost);

	    // System.out.println("------------------IDENTIFYING COVERS----------------------");
	    System.out.println(response.getStatusLine());

	    HttpEntity entity = response.getEntity();
	    String responseString = EntityUtils.toString(entity, "UTF-8");
	    System.out.println(responseString);

	    statusCode = response.getStatusLine().getStatusCode();
	    System.out.println("Status Code::" + statusCode);
	    EntityUtils.consume(entity);

	    // System.out.println("----------------------------------------");

	} catch (Exception e) {
	    e.printStackTrace();
	}
	return statusCode;
    }

}
