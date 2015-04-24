/**
 * 
 */
package i5.las2peer.services.servicePackage.evaluator;

import i5.las2peer.services.servicePackage.datamodel.EvaluationMetricsEntity;
import i5.las2peer.services.servicePackage.datamodel.UserEntity;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;

/**
 * @author sathvik
 *
 */

// TODO: Check factory pattern for this.
public class EvaluationMeasure {
    private LinkedHashMap<String, Double> userId2Score = new LinkedHashMap<String, Double>();
    private Precision precision;
    private Recall recall;
    private PrecisionRecall precision_recall;
    private ElevenPointInterpolatedAveragePrecision epap;
    private ReciprocalRank rr;
    private String algoName;
    private Map<Long, UserEntity> userId2userObj;
    private EvaluationMetricsEntity evaluationEntity;

    public EvaluationMeasure(LinkedHashMap<String, Double> id2Score, Map<Long, UserEntity> userId2userObj, String name) {
	userId2Score = id2Score;
	algoName = name;
	this.userId2userObj = userId2userObj;
    }

    public void computeAll() throws IOException {
	computePrecision();
	computeRecall();
	computePrecisionRecall();
	compute11ptIP();
	computeMRR();
    }

    public void computePrecision() {
	precision = new Precision(userId2Score, this.userId2userObj, 40);
	precision.getAveragePrecision();
	precision.saveAvgPrecisionToFile();
	precision.savePrecisionValuesToFile();
    }

    public void computeRecall() {
	recall = new Recall(userId2Score, userId2userObj, 40);
	recall.calculateValuesAtEveryPosition();
	recall.saveRecallValuesToFile();
    }

    public void computePrecisionRecall() throws IOException {
	System.out.println("PRECISION - RECALL ::");
	if (precision == null) {
	    computePrecision();
	}

	if (recall == null) {
	    computeRecall();
	}

	precision_recall = new PrecisionRecall(precision.getRoundedValues(), recall.getRoundedValues());
	precision_recall.savePrecisionRecallCSV1();
	precision_recall.insertStandardRecallPoints();
    }

    public void compute11ptIP() {
	System.out.println("11 pt Interpolated precision ::");
	epap = new ElevenPointInterpolatedAveragePrecision();
	epap.calculateInterPrecisionValues(recall.getRoundedValues(), precision.getRoundedValues());

	epap.calculateInterPrecisionValues(ArrayUtils.toPrimitive(precision_recall.getRecallValues()),
		ArrayUtils.toPrimitive(precision_recall.getPrecisionValues()));

	epap.save();
    }

    public void computeNDCG() {

    }

    public void computeMAP() {

    }

    public void computeMRR() {
	System.out.println("MRR ::");
	rr = new ReciprocalRank(userId2userObj);
	rr.computeReciprocalRank(userId2Score);
	rr.save();
    }

    public void save(long queryId, ConnectionSource connSrc) throws JsonIOException, JsonSyntaxException, IOException {

	// File metricsFile = new File("metrics.json");
	//
	// if (metricsFile.exists() == false) {
	// metricsFile.createNewFile();
	// }
	//
	// String jsonStr = Application.readFile(metricsFile.getPath(),
	// StandardCharsets.UTF_8);
	JsonArray jContainer = new JsonArray();

	// if (jsonStr == null || jsonStr.length() == 0) {
	// jsonStr = "[]";
	// }
	//
	// JsonParser parser = new JsonParser();
	// if (parser.parse(jsonStr).isJsonArray()) {
	// jContainer = parser.parse(jsonStr).getAsJsonArray();
	// }
	JsonArray metricsArray = new JsonArray();

	JsonArray jPrecision = new JsonArray();
	for (double value : precision.getRoundedValues()) {
	    JsonPrimitive element = new JsonPrimitive(value);
	    jPrecision.add(element);
	}
	JsonObject jPrecisionObj = new JsonObject();
	jPrecisionObj.add("precision", jPrecision);

	JsonArray jRecall = new JsonArray();
	for (double value : recall.getRoundedValues()) {
	    JsonPrimitive element = new JsonPrimitive(value);
	    jRecall.add(element);
	}
	JsonObject jRecallObj = new JsonObject();
	jRecallObj.add("recall", jRecall);

	metricsArray.add(jPrecisionObj);
	metricsArray.add(jRecallObj);

	JsonObject jObj = new JsonObject();
	jObj.addProperty("id", queryId);
	jObj.addProperty("algo", algoName);
	jObj.addProperty("timestamp", System.currentTimeMillis());
	jObj.add("metrics", metricsArray);

	jContainer.add(jObj);
	String evaluationMeasures = jContainer.toString();

	try {
	    Dao<EvaluationMetricsEntity, Long> EvaluationDao = DaoManager.createDao(connSrc, EvaluationMetricsEntity.class);

	    evaluationEntity = new EvaluationMetricsEntity();
	    evaluationEntity.setQueryId(queryId);
	    evaluationEntity.setCreateDate(new Date());
	    evaluationEntity.setMetrics(evaluationMeasures);

	    EvaluationDao.createIfNotExists(evaluationEntity);

	} catch (SQLException e) {
	    e.printStackTrace();
	}

	// PrintWriter writer = new PrintWriter(metricsFile.getPath(), "UTF-8");
	// writer.println(evaluationMeasures);
	// writer.close();
    }

    public long getId() {
	if (evaluationEntity != null) {
	    return evaluationEntity.getId();
	}
	return -1;
    }

}
