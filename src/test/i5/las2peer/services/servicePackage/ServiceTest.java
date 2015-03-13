package i5.las2peer.services.servicePackage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import i5.las2peer.p2p.LocalNode;
import i5.las2peer.security.ServiceAgent;
import i5.las2peer.security.UserAgent;
import i5.las2peer.services.servicePackage.evaluation.EvaluationMeasure;
import i5.las2peer.services.servicePackage.evaluation.NormalizedDiscountedCumulativeGain;
import i5.las2peer.services.servicePackage.parsers.CSVParser;
import i5.las2peer.services.servicePackage.utils.Global;
import i5.las2peer.testing.MockAgentFactory;
import i5.las2peer.webConnector.WebConnector;
import i5.las2peer.webConnector.client.ClientResponse;
import i5.las2peer.webConnector.client.MiniClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Random;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 * Example Test Class demonstrating a basic JUnit test structure.
 * 
 * 
 *
 */
public class ServiceTest {
	
	private static final String HTTP_ADDRESS = "http://127.0.0.1";
	private static final int HTTP_PORT = WebConnector.DEFAULT_HTTP_PORT;
	
	private static LocalNode node;
	private static WebConnector connector;
	private static ByteArrayOutputStream logStream;
	
	private static UserAgent testAgent;
	private static final String testPass = "adamspass";
	
	private static final String testServiceClass = "i5.las2peer.services.servicePackage.ExpertRecommenderService";
	
	private static final String mainPath = "recommender_framework/";
	
	
	/**
	 * Called before the tests start.
	 * 
	 * Sets up the node and initializes connector and users that can be used throughout the tests.
	 * 
	 * @throws Exception
	 */
	@BeforeClass
	public static void startServer() throws Exception {
		
		//start node
		node = LocalNode.newNode();
		node.storeAgent(MockAgentFactory.getAdam());
		node.launch();
		
		ServiceAgent testService = ServiceAgent.generateNewAgent(testServiceClass, "a pass");
		testService.unlockPrivateKey("a pass");
		
		node.registerReceiver(testService);
		
		//start connector
		logStream = new ByteArrayOutputStream ();
		
		connector = new WebConnector(true,HTTP_PORT,false,1000);
		connector.setSocketTimeout(10000);
		connector.setLogStream(new PrintStream (logStream));
		connector.start ( node );
        Thread.sleep(1000); //wait a second for the connector to become ready
		testAgent = MockAgentFactory.getAdam();
		
        connector.updateServiceList();
        //avoid timing errors: wait for the repository manager to get all services before continuing
        try
        {
            System.out.println("waiting..");
            Thread.sleep(10000);
        }
        catch(InterruptedException e)
        {
            e.printStackTrace();
        }
		
	}
	
	
	/**
	 * Called after the tests have finished.
	 * Shuts down the server and prints out the connector log file for reference.
	 * 
	 * @throws Exception
	 */
	@AfterClass
	public static void shutDownServer () throws Exception {
		
		connector.stop();
		node.shutDown();
		
        connector = null;
        node = null;
        
        LocalNode.reset();
		
		System.out.println("Connector-Log:");
		System.out.println("--------------");
		
		System.out.println(logStream.toString());
		
    }
	
	
	
	
	
	/**
	 * 
	 * Test the example method that consumes one path parameter
	 * which we give the value "testInput" in this test.
	 * 
	 */
	@Test
	public void testExampleMethod()
	{
		MiniClient c = new MiniClient();
		c.setAddressPort(HTTP_ADDRESS, HTTP_PORT);
		
		try
		{
			c.setLogin(Long.toString(testAgent.getId()), testPass);
           // ClientResponse result=c.sendRequest("POST", mainPath +"myResourcePath/testInput", ""); //testInput is the pathParam
			// ClientResponse result=c.sendRequest("POST", mainPath
			// +"recommender", "Who is the expert in compiler?"); //testInput is
			// the pathParam
			// System.out.println("Result of 'testExampleMethod': " +
			// result.getResponse().trim());
            
			// assertEquals(200, result.getHttpCode());
            //assertTrue(result.getResponse().trim().contains("testInput")); //"testInput" name is part of response
		}
		catch(Exception e)
		{
			e.printStackTrace();
			fail ( "Exception: " + e );
		}
		
    }
	
	@Test
	public void testStemmerMethod()
	{
		MiniClient c = new MiniClient();
		c.setAddressPort(HTTP_ADDRESS, HTTP_PORT);
		
		try
		{
			c.setLogin(Long.toString(testAgent.getId()), testPass);
            ClientResponse result=c.sendRequest("POST", mainPath +"stemmer", "Who is the expert in compiler catty?"); //testInput is the pathParam
            System.out.println("Result of 'testExampleMethod': " + result.getResponse().trim());
            
            assertEquals(200, result.getHttpCode());
		}
		catch(Exception e)
		{
			e.printStackTrace();
			fail ( "Exception: " + e );
		}
		
    }
	
	@Test
	public void testNDCG() {
		int[] relevance_mock_val = { 2, 1, 2, 0 };
		NormalizedDiscountedCumulativeGain ndcg = new NormalizedDiscountedCumulativeGain(
				relevance_mock_val);
		float dcg = ndcg.getDCG();
		assertEquals(4.26, Global.round(dcg, 2), 1e-15);

		float idealNdcgVal = ndcg.getIdealDCG();
		assertEquals(4.63, Global.round(idealNdcgVal, 2), 1e-15);

		assertEquals(0.92, Global.round(ndcg.getValue(), 2), 1e-15);

	}

	@Test
	public void testEvaluationMetrics() throws IOException {
		LinkedHashMap<String, Double> userId2Score = new LinkedHashMap<String, Double>();
		double start = 0;
		double end = 40000;
		Random random = new Random();
		for (int i = 0; i < 50; i++) {
			double randomWeight = random.nextDouble();
			double randomValue = start + (randomWeight * (end - start));

			userId2Score.put("123" + i, randomValue);
		}

		// TODO:Precision and recall values have to be computed before doing
		// this.
		EvaluationMeasure measure = new EvaluationMeasure(userId2Score);
		measure.computeAll();
		measure.save("123");

	}

	@Test
	public void testCSVReader() throws IOException, SQLException {
		CSVParser converter = new CSVParser("ehitusfoorumi.csv");
		converter.parse();
		converter.saveRecordsToDb();
	}

	/**
	 * Test the ServiceClass for valid rest mapping.
	 * Important for development.
	 */
	@Test
	public void testDebugMapping()
	{
		ExpertRecommenderService cl = new ExpertRecommenderService();
		assertTrue(cl.debugMapping());
	}
}
