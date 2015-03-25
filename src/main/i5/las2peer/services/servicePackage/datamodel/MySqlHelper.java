package i5.las2peer.services.servicePackage.datamodel;

import i5.las2peer.services.servicePackage.statistics.Stats;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.jdbc.JdbcDatabaseConnection;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.support.ConnectionSource;

/**
 * @author sathvik
 */

public class MySqlHelper {
	// TODO://Move these to properties file.
	private static String DB_URL = "jdbc:mysql://127.0.0.1:3306/";

	public static void createDatabase(String database_name) throws SQLException {
		JdbcDatabaseConnection connection = new JdbcDatabaseConnection(
				DriverManager.getConnection(DB_URL, "root", ""));
		connection.executeStatement("CREATE DATABASE IF NOT EXISTS "
				+ database_name, -1);
	}

	public static ConnectionSource createConnectionSource(String database_name)
			throws SQLException {
		ConnectionSource connectionSource = new JdbcConnectionSource(DB_URL
				+ database_name);
		((JdbcConnectionSource) connectionSource).setUsername("root");
		((JdbcConnectionSource) connectionSource).setPassword("");
		return connectionSource;
	}

	/*
	 * public static void createAndInsertUserDAO(
	 * List<i5.las2peer.services.servicePackage.xmlparsers.User> users,
	 * ConnectionSource connectionSrc) throws SQLException { UserEntity entity =
	 * null; Dao<UserEntity, Long> UserDao = DaoManager.createDao(connectionSrc,
	 * UserEntity.class);
	 * 
	 * // Iterate tags and create DAO objects. for (User user : users) { entity
	 * = new UserEntity();
	 * 
	 * if (user.getDownVotes() != null) {
	 * entity.setDownVotes(Long.parseLong(user.getDownVotes())); }
	 * 
	 * if (user.getReputation() != null) {
	 * entity.setReputation(Long.parseLong(user.getReputation())); }
	 * 
	 * if (user.getUpVotes() != null) {
	 * entity.setUpVotes(Long.parseLong(user.getUpVotes())); }
	 * 
	 * if (user.getUserAccId() != null) {
	 * entity.setUserAccId(Long.parseLong(user.getUserAccId())); }
	 * 
	 * if (user.getUserAccId() != null) {
	 * entity.setUserId(Long.parseLong(user.getUserId())); }
	 * 
	 * if (user.getViews() != null) {
	 * entity.setViews(Long.parseLong(user.getViews())); }
	 * 
	 * entity.setCreationDate(user.getCreationDate());
	 * entity.setAbtMe(user.getAbtMe()); entity.setLocation(user.getLocation());
	 * entity.setUserName(user.getUserName());
	 * entity.setWebsiteUrl(user.getWebsiteUrl());
	 * 
	 * UserDao.createIfNotExists(entity); } }
	 * 
	 * public static void createAndInsertResourceDAO( ConnectionSource
	 * connectionSrc, ArrayList<DataEntity> data_list) throws SQLException {
	 * Dao<DataEntity, Long> DataDao = DaoManager.createDao(connectionSrc,
	 * DataEntity.class); StopWordRemover remover = null; for (DataEntity data :
	 * data_list) { if (data.getBody() != null) { remover = new
	 * StopWordRemover(data.getBody());
	 * data.setCleanText(remover.getPlainText()); }
	 * DataDao.createIfNotExists(data); } }
	 * 
	 * public static void createAndInsertResourceDAO(
	 * List<i5.las2peer.services.servicePackage.xmlparsers.Resource> resources,
	 * ConnectionSource connectionSrc) throws SQLException {
	 * 
	 * Dao<DataEntity, Long> DataDao = DaoManager.createDao(connectionSrc,
	 * DataEntity.class); DataEntity data = null; StopWordRemover remover =
	 * null;
	 * 
	 * // Iterate tags and create DAO objects. for (Resource res : resources) {
	 * data = new DataEntity(); boolean idExists = false;
	 * 
	 * if (res.getPostId() != null) { Long postid =
	 * Long.parseLong(res.getPostId()); data.setPostId(postid); idExists =
	 * DataDao.idExists(postid); // System.out.println(postid + " " + idExists);
	 * }
	 * 
	 * if (res.getAccAnsId() != null) {
	 * data.setAcceptAnsId(Long.parseLong(res.getAccAnsId())); }
	 * 
	 * if (res.getAnswerCount() != null) {
	 * data.setAnswerCount(Long.parseLong(res.getAnswerCount())); }
	 * 
	 * if (res.getCommentCount() != null) {
	 * data.setCommentCount(Integer.parseInt(res.getCommentCount())); }
	 * 
	 * if (res.getCreationDate() != null) {
	 * data.setCreationDate(res.getCreationDate()); }
	 * 
	 * if (res.getLastEditorUserId() != null) {
	 * data.setLastEditorUserId(Long.parseLong(res .getLastEditorUserId())); }
	 * 
	 * if (res.getOwnerUserId() != null) {
	 * data.setOwnerUserId(Long.parseLong(res.getOwnerUserId())); }
	 * 
	 * if (res.getParentId() != null) {
	 * data.setParentId(Long.parseLong(res.getParentId())); }
	 * 
	 * if (res.getPostId() != null) {
	 * data.setPostId(Long.parseLong(res.getPostId())); }
	 * 
	 * if (res.getPostTypeId() != null) {
	 * data.setPostTypeId(Integer.parseInt(res.getPostTypeId())); }
	 * 
	 * if (res.getScore() != null) {
	 * data.setScore(Long.parseLong(res.getScore())); }
	 * 
	 * if (res.getTags() != null) { data.setTags(res.getTags()); }
	 * 
	 * if (res.getTitle() != null) { data.setTitle(res.getTitle()); }
	 * 
	 * if (res.getViewCount() != null) {
	 * data.setViewCount(Long.parseLong(res.getViewCount())); }
	 * 
	 * if (res.getBody() != null) { data.setBody(res.getBody());
	 * 
	 * remover = new StopWordRemover(res.getBody());
	 * data.setCleanText(remover.getPlainText()); }
	 * DataDao.createIfNotExists(data); } }
	 */

	/*
	 * public static void createAndInsertSemanticTags( ConnectionSource
	 * connectionSrc) throws SQLException { Dao<SemanticTagEntity, Long>
	 * SemanticDao = DaoManager.createDao( connectionSrc,
	 * SemanticTagEntity.class);
	 * 
	 * // Iterate all the posts and extract tags from them. Dao<DataEntity,
	 * Long> postsDao = DaoManager.createDao(connectionSrc, DataEntity.class);
	 * List<DataEntity> data_entites = postsDao.queryForAll();
	 * 
	 * SemanticTagEntity tagEntity = null; SemanticTagger tagger = null; for
	 * (DataEntity entity : data_entites) { tagger = new
	 * SemanticTagger(entity.getBody()); if (tagger != null && tagger.getTags()
	 * != null) { String tags = tagger.getTags().getTags(); String annotations =
	 * tagger.getTags().getAnnotation();
	 * 
	 * tagEntity = new SemanticTagEntity();
	 * tagEntity.setPostId(entity.getPostId());
	 * tagEntity.setAnnotations(annotations); tagEntity.setTags(tags);
	 * 
	 * SemanticDao.createIfNotExists(tagEntity); }
	 * 
	 * }
	 * 
	 * }
	 */

	/*
	 * public static List<UserEntity> getUserDAOs(ConnectionSource
	 * connectionSrc) throws SQLException { Dao<UserEntity, Long> userDao =
	 * DaoManager.createDao(connectionSrc, UserEntity.class); return
	 * userDao.queryForAll(); }
	 */

	/*
	 * public static void createUserMap(ConnectionSource connectionSrc) throws
	 * SQLException { Dao<UserEntity, Long> userDao =
	 * DaoManager.createDao(connectionSrc, UserEntity.class); List<UserEntity>
	 * user_entites = userDao.queryForAll(); for (UserEntity entity :
	 * user_entites) { Global.userId2userObj.put(entity.getUserId(), entity); }
	 * }
	 */

	public static void markExpertsForEvaluation(ConnectionSource connectionSrc)
			throws SQLException {
		List<Long> reputations = new ArrayList<Long>();

		Dao<UserEntity, Long> userDao = DaoManager.createDao(connectionSrc,
				UserEntity.class);
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

	// public static void createTermFreqMap(ConnectionSource connectionSrc)
	// throws SQLException {
	// Dao<DataEntity, Long> postsDao = DaoManager.createDao(connectionSrc,
	// DataEntity.class);
	// List<DataEntity> data_entites = postsDao.queryForAll();
	// Global.totalNoOfResources = data_entites.size();
	// for (DataEntity entity : data_entites) {
	// Global.createTermFreqMap(entity.getPostId(), entity.getBody(),
	// entity.getParentId(), entity.getOwnerUserId());
	// }
	//
	// }

	// public static void createSemanticTagFreqMap(ConnectionSource
	// connectionSrc)
	// throws SQLException {
	//
	// try {
	// Dao<SemanticTagEntity, Long> tagDao = DaoManager.createDao(
	// connectionSrc, SemanticTagEntity.class);
	// List<SemanticTagEntity> tag_entites = tagDao.queryForAll();
	// for (SemanticTagEntity tag_entity : tag_entites) {
	// Global.createTagEntityFreqMap(tag_entity.getPostId(),
	// tag_entity.getAnnotations());
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }

	/*
	 * public static void insertSemanticTags(ConnectionSource connectionSrc)
	 * throws SQLException {
	 * 
	 * try { Dao<SemanticTagEntity, Long> tagDao = DaoManager.createDao(
	 * connectionSrc, SemanticTagEntity.class); List<SemanticTagEntity>
	 * tag_entites = tagDao.queryForAll(); for (SemanticTagEntity tag_entity :
	 * tag_entites) { Global.createTagEntityFreqMap(tag_entity.getPostId(),
	 * tag_entity.getAnnotations()); } } catch (Exception e) {
	 * e.printStackTrace(); } }
	 */
	
	// TODO:Remove this, This is better if it is done in the background.
	// public void calculateResProps() {
	// String textbody = null;
	// String postid = null;
	// String userid = null;
	// String last_edit_date = null; // TODO: Use it to complete Algo.
	// String vote_count = null;
	// String comment_count = null;
	//
	// try {
	// System.out.println("Calculating Resource properties...");
	// preparedStatement = connect.prepareStatement(GET_RESOURCE);
	// ResultSet rs = preparedStatement.executeQuery();
	// Stopwatch timer = Stopwatch.createStarted();
	//
	// while (rs.next()) {
	// textbody = rs.getString("clean_text");
	// postid = rs.getString("post_id");
	// userid = rs.getString("owner_user_id");
	// vote_count = rs.getString("score");
	// comment_count = rs.getString("comment_count");
	// last_edit_date = rs.getString("last_edit_date");
	//
	// utils.calPopAndCosSimilarity(postid, textbody, userid,
	// vote_count, comment_count, last_edit_date);
	//
	// Global.totalNoOfResources++;
	// }
	// System.out.println("Time to calculate Res peoperties...: "
	// + timer.stop());
	//
	// // utils.printMultiMap(utils.TERM_FREQ_MAP, "");
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	//
	// }

}
