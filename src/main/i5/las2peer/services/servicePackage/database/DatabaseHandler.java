/**
 * 
 */
package i5.las2peer.services.servicePackage.database;

import i5.las2peer.services.servicePackage.entities.DataEntity;
import i5.las2peer.services.servicePackage.entities.EvaluationMetricsEntity;
import i5.las2peer.services.servicePackage.entities.ExpertEntity;
import i5.las2peer.services.servicePackage.entities.GraphEntity;
import i5.las2peer.services.servicePackage.entities.SemanticTagEntity;
import i5.las2peer.services.servicePackage.entities.UserEntity;
import i5.las2peer.services.servicePackage.parsers.IPost;
import i5.las2peer.services.servicePackage.parsers.IUser;
import i5.las2peer.services.servicePackage.semanticTagger.SemanticTagger;
import i5.las2peer.services.servicePackage.statistics.Stats;
import i5.las2peer.services.servicePackage.textProcessor.StopWordRemover;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gson.Gson;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.support.ConnectionSource;

/**
 * A Database handler to do CRUD operations on the Database.
 * Database is created if not present when fetching connection source.
 * 
 * @author sathvik
 *
 */
public class DatabaseHandler extends MySqlOpenHelper {

    /**
     * @param dbName
     *            A database name to be used during the current session.
     * @param username
     *            A username for the database. (This is static during
     *            development)
     * @param password
     *            A password for the database. (This is static during
     *            development)
     */
    public DatabaseHandler(String dbName, String username, String password) {
	super(dbName, username, password);
    }

    /**
     * Method to add the post containing user activity details into database
     * table.
     * This method is called after all the posts have been collected into a list
     * by a parser such as XML or CSV parser.
     * 
     * @param posts
     *            A list of posts and the details of the posts to be inserted.
     * @throws SQLException
     *             If database is not open to insert posts or does not comply to
     *             table constraints.
     * 
     * */
    public void addPosts(List<? extends IPost> posts) throws SQLException {

	ConnectionSource source = super.getConnectionSource();
	Dao<DataEntity, Long> DataDao = DaoManager.createDao(source, DataEntity.class);

	DataEntity data = null;
	StopWordRemover remover = null;

	for (IPost res : posts) {

	    data = new DataEntity();
	    // boolean idExists = false;

	    // System.out.println(res.getPostId());
	    if (res.getPostId() != null) {
		Long postid = Long.parseLong(res.getPostId());
		data.setPostId(postid);
		// idExists = DataDao.idExists(postid);
		// System.out.println(postid + " " + idExists);
	    }

	    if (res.getAccAnsId() != null) {
		data.setAcceptAnsId(Long.parseLong(res.getAccAnsId()));
	    }

	    if (res.getAnswerCount() != null) {
		data.setAnswerCount(Long.parseLong(res.getAnswerCount()));
	    }

	    if (res.getCommentCount() != null) {
		data.setCommentCount(Integer.parseInt(res.getCommentCount()));
	    }

	    if (res.getCreationDate() != null) {
		data.setCreationDate(res.getCreationDate());
	    }

	    if (res.getLastEditorUserId() != null) {
		data.setLastEditorUserId(Long.parseLong(res.getLastEditorUserId()));
	    }

	    if (res.getOwnerUserId() != null) {
		data.setOwnerUserId(Long.parseLong(res.getOwnerUserId()));
	    }

	    if (res.getParentId() != null) {
		data.setParentId(Long.parseLong(res.getParentId()));
	    }

	    if (res.getPostId() != null) {
		data.setPostId(Long.parseLong(res.getPostId()));
	    }

	    if (res.getPostTypeId() != null) {
		data.setPostTypeId(Integer.parseInt(res.getPostTypeId()));
	    }

	    if (res.getScore() != null) {
		data.setScore(Long.parseLong(res.getScore()));
	    }

	    if (res.getTags() != null) {
		data.setTags(res.getTags());
	    }

	    if (res.getTitle() != null) {
		data.setTitle(res.getTitle());
	    }

	    if (res.getViewCount() != null) {
		data.setViewCount(Long.parseLong(res.getViewCount()));
	    }

	    if (res.getBody() != null) {
		remover = new StopWordRemover(res.getBody());

		data.setBody(res.getBody());
		data.setCleanText(remover.getPlainText());
	    }
	    DataDao.createIfNotExists(data);

	}
    }

    /**
     * Method to add user details into "user" table in the database.
     * This method is called after collecting user information is collected into
     * as list by a parser such as XML or CSV parser.
     * 
     * @param users
     *            A list of users and their information to be inserted.
     * @throws SQLException
     *             If database is not open to insert users or does not comply to
     *             table constraints.
     * */

    public void addUsers(List<? extends IUser> users) throws SQLException {
	ConnectionSource source = super.getConnectionSource();
	UserEntity entity = null;
	Dao<UserEntity, Long> UserDao = DaoManager.createDao(source, UserEntity.class);

	// Iterate tags and create DAO objects.
	for (IUser user : users) {
	    entity = new UserEntity();

	    if (user.getDownVotes() != null) {
		entity.setDownVotes(Long.parseLong(user.getDownVotes()));
	    }

	    if (user.getReputation() != null) {
		entity.setReputation(Long.parseLong(user.getReputation()));
	    }

	    if (user.getUpVotes() != null) {
		entity.setUpVotes(Long.parseLong(user.getUpVotes()));
	    }

	    if (user.getUserAccId() != null) {
		entity.setUserAccId(Long.parseLong(user.getUserAccId()));
	    }

	    if (user.getUserAccId() != null) {
		entity.setUserId(Long.parseLong(user.getUserId()));
	    }

	    if (user.getViews() != null) {
		entity.setViews(Long.parseLong(user.getViews()));
	    }

	    entity.setCreationDate(user.getCreationDate());
	    entity.setAbtMe(user.getAbtMe());
	    entity.setLocation(user.getLocation());
	    entity.setUserName(user.getUserName());
	    entity.setWebsiteUrl(user.getWebsiteUrl());

	    UserDao.createIfNotExists(entity);
	}
    }

    /**
     * Method to add semantic information about the post into "semantics" table
     * in the database.
     * This method is called to add semantic information for each post in the
     * database.
     * All the posts are iterated and a web service is used to retrieve semantic
     * data.
     * 
     * @see SemanticTagger#getSemanticData()
     * 
     * @throws SQLException
     *             If database is not open to insert semantic tags or does not
     *             comply to table constraints.
     * 
     * */

    public void addSemanticTags() throws SQLException {
	ConnectionSource source = super.getConnectionSource();
	Dao<SemanticTagEntity, Long> SemanticDao = DaoManager.createDao(source, SemanticTagEntity.class);

	// Iterate all the posts and extract tags from them.
	Dao<DataEntity, Long> postsDao = DaoManager.createDao(source, DataEntity.class);
	List<DataEntity> data_entites = postsDao.queryForAll();

	SemanticTagEntity tagEntity = null;
	SemanticTagger tagger = null;
	for (DataEntity entity : data_entites) {
	    SemanticTagEntity tEntity = SemanticDao.queryForId(entity.getPostId());
	    // If particular Id is not present in the semantic table then
	    // proceed with extraction of tag
	    if (tEntity == null) {

		tagger = new SemanticTagger(entity.getBody());
		if (tagger != null && tagger.getSemanticData() != null) {
		    String tags = tagger.getSemanticData().getTags();
		    String annotations = tagger.getSemanticData().getAnnotation();

		    tagEntity = new SemanticTagEntity();
		    tagEntity.setPostId(entity.getPostId());
		    tagEntity.setAnnotations(annotations);
		    tagEntity.setTags(tags);

		    SemanticDao.createIfNotExists(tagEntity);
		}
	    }
	}
    }

    /**
     * Queries for all the user rows and creates a DAO object by ORMLite
     * The DAO object is used across the application.
     * 
     * @return List of UserEntity Objects containing user properties.
     * @see UserEntity
     * 
     * */

    public List<UserEntity> getUserDAOs() throws SQLException {
	Dao<UserEntity, Long> userDao = DaoManager.createDao(super.getConnectionSource(), UserEntity.class);
	return userDao.queryForAll();
    }

    /**
     * A helper method used to mark the probable expert in the database. It will
     * be used during evaluation and this marks the ground truth value.
     * 
     * This method iterates all the users and retrieves reputation value for the
     * user.
     * If corresponding reputation is above 98 percentile, it is marked as
     * probable expert.
     * 
     * */

    public void markExpertsForEvaluation(ConnectionSource connectionSrc) throws SQLException {
	List<Long> reputations = new ArrayList<Long>();

	Dao<UserEntity, Long> userDao = DaoManager.createDao(connectionSrc, UserEntity.class);
	List<UserEntity> user_entites = userDao.queryForAll();
	for (UserEntity entity : user_entites) {
	    long reputation = entity.getReputation();
	    reputations.add(reputation);
	}

	Stats stats = new Stats(reputations);

	// System.out.println("MAX::" + stats.getMax());
	// System.out.println("MIN::" + stats.getMin());
	// System.out.println("MEDIAN::" + stats.getMedian());
	// System.out.println("UPPER QUARTILE::" + stats.getUpperQuartile());
	// System.out.println("LOWER QUARTILE::" + stats.getLowerQuartile());
	// System.out.println("GET ABOVE PERCENTILE::"
	// + stats.getPercentileAbove(98));

	UpdateBuilder updateBuilder = userDao.updateBuilder();
	for (UserEntity entity : user_entites) {
	    long reputation = entity.getReputation();
	    if (reputation >= stats.getPercentileAbove(98)) {
		updateBuilder.where().eq("userId", entity.getUserId());
		updateBuilder.updateColumnValue("probable_expert", true);
		updateBuilder.update();
	    }
	}

    }

    /**
     * 
     * @param evaluationId
     * @return
     */
    public String getEvaluationMetrics(long evaluationId) {
	Dao<EvaluationMetricsEntity, Long> evaluationDao = null;
	try {
	    evaluationDao = DaoManager.createDao(super.getConnectionSource(), EvaluationMetricsEntity.class);
	    EvaluationMetricsEntity entity = evaluationDao.queryForId(evaluationId);

	    return entity.getMetrics();
	} catch (SQLException e) {
	    e.printStackTrace();
	}

	return null;
    }

    /**
     * 
     * @param queryId
     * @return
     */
    public String getVisGraph(long queryId) {
	Dao<GraphEntity, Long> visulaizationDao = null;
	try {
	    visulaizationDao = DaoManager.createDao(super.getConnectionSource(), GraphEntity.class);
	    GraphEntity entity = visulaizationDao.queryForId(queryId);

	    return entity.getGraph();
	} catch (SQLException e) {
	    e.printStackTrace();
	}

	return null;
    }

    /**
     * 
     * @param userId
     * @return
     */
    public String getUser(long userId) {
	Dao<UserEntity, Long> userDao = null;
	try {
	    userDao = DaoManager.createDao(super.getConnectionSource(), UserEntity.class);
	    UserEntity entity = userDao.queryForId(userId);

	    Gson gson = new Gson();

	    return gson.toJson(entity);
	} catch (SQLException e) {
	    e.printStackTrace();
	}

	return null;
    }

    /**
     * 
     * @param queryId
     *            Id to identify the expert list.
     * @param experts
     *            A list of experts and their details stored as json string.
     * @return
     */
    public long addExperts(long queryId, String experts) {
	try {
	    Dao<ExpertEntity, Long> ExpertDao = DaoManager.createDao(super.getConnectionSource(), ExpertEntity.class);
	    ExpertEntity entity = new ExpertEntity();
	    entity.setQueryId(queryId);
	    entity.setExperts(experts);
	    entity.setDate(new Date());
	    ExpertDao.createIfNotExists(entity);

	    return entity.getId();
	} catch (SQLException e) {
	    e.printStackTrace();
	}
	return -1;
    }

    /**
     * 
     * @param expertsId
     * @return
     */
    public String getExperts(long expertsId) {
	Dao<ExpertEntity, Long> expertsDao = null;
	try {
	    expertsDao = DaoManager.createDao(super.getConnectionSource(), ExpertEntity.class);
	    ExpertEntity entity = expertsDao.queryForId(expertsId);

	    return entity.getExperts();
	} catch (SQLException e) {
	    e.printStackTrace();
	}

	return null;
    }

    public void close() {
	try {
	    super.close();
	} catch (SQLException e) {
	    e.printStackTrace();
	}
    }

}
