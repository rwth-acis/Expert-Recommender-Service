/**
 * 
 */
package i5.las2peer.services.servicePackage.indexer;

import i5.las2peer.services.servicePackage.datamodel.DataEntity;
import i5.las2peer.services.servicePackage.datamodel.UserEntity;
import i5.las2peer.services.servicePackage.models.Token;
import i5.las2peer.services.servicePackage.utils.Application;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.queryparser.classic.ParseException;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;

/**
 * @author sathvik
 *
 */
public class DbIndexer {

	private String mQueryString = null;
	private HashMultiset queryTerms;

	private HashMap<Object, Integer> word2freq;
	private HashMap<Long, Long> postId2userId;
	private Map<Long, UserEntity> userId2userObj = new HashMap<Long, UserEntity>();

	private Map<Long, Double> postid2tfirf;

	private HashMultimap<Long, Long> parentId2postIds;

	private HashMultimap<Long, Token> postid2Tokens = HashMultimap.create();

	public final int THRESHOLD_WORD_FREQ = 0;
	public ConnectionSource mConnectionSrc;

	/** Creates a new instance of SearchEngine */
	public DbIndexer(ConnectionSource connectionSrc) throws IOException {

		mConnectionSrc = connectionSrc;

		word2freq = new HashMap<Object, Integer>();
		postId2userId = new HashMap<Long, Long>();
		postid2tfirf = new HashMap<Long, Double>();

		userId2userObj = new HashMap<Long, UserEntity>();

		postid2Tokens = HashMultimap.create();
		parentId2postIds = HashMultimap.create();

	}

	public HashMap<Long, Long> getPostId2UserIdMap() {
		return postId2userId;
	}

	public Map<Long, Collection<Long>> getQnAMap() {
		return (Map<Long, Collection<Long>>) parentId2postIds.asMap();
	}

	public Map<Long, UserEntity> getUserMap() {
		return userId2userObj;
	}

	public void buildIndex(String queryString) throws IOException,
			ParseException, SQLException {
		try {
			mQueryString = queryString;
			queryTerms = HashMultiset.create(Splitter
					.on(CharMatcher.WHITESPACE).omitEmptyStrings()
					.split(queryString));

			Dao<DataEntity, Long> postsDao = DaoManager.createDao(
					mConnectionSrc, DataEntity.class);
			List<DataEntity> data_entites = postsDao.queryForAll();
			Application.totalNoOfResources = data_entites.size();

			for (DataEntity entity : data_entites) {
				createTokenFreqMap(entity.getPostId(), entity.getBody(),
						entity.getParentId(), entity.getOwnerUserId());
			}

			Dao<UserEntity, Long> userDao = DaoManager.createDao(
					mConnectionSrc, UserEntity.class);
			List<UserEntity> user_entites = userDao.queryForAll();
			for (UserEntity entity : user_entites) {
				// Application.userId2userObj.put(entity.getUserId(), entity);
				updateUserMap(entity);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("" + e);
		}
	}

	private void createTokenFreqMap(long postid, String text, long parentId,
			long userId) {
		Multiset<String> bagOfWords = HashMultiset.create(Splitter
				.on(CharMatcher.WHITESPACE).omitEmptyStrings().split(text));

		int totalcount = 0;

		Token token;
		int count = 0;
		// Iterate the Query terms.
		// Ignore if freq is too less.
		for (Object word : queryTerms.elementSet()) {
			count = bagOfWords.count(word);

			totalcount = word2freq.get((String) word) != null ? word2freq
					.get((String) word) + count : 0;
			word2freq.put((String) word, totalcount);

			if (count > THRESHOLD_WORD_FREQ) {
				token = new Token(postid, text);
				token.setFrequnecy(count);
				token.setText((String) word);

				postid2Tokens.put(postid, token);
				if (parentId > 0) {
					parentId2postIds.put(parentId, postid);
				}

				// System.out.println("USERID::" + userId);
				if (userId > 0) {
					postId2userId.put(postid, userId);
				}
			}
		}

	}

	public void updateUserMap(UserEntity entity) throws SQLException {
		userId2userObj.put(entity.getUserId(), entity);
	}

	private void createInverseResFreqMap() {
		System.out.println("Creating Inverse Freq Map...");
		try {
			for (Map.Entry entry : postid2Tokens.entries()) {
				Long postid = (Long) entry.getKey();
				Set<Token> tokens = postid2Tokens.get(postid);
				double sum_tfirf = 0; // TFIRF of individual token combined for
										// entire post.
				for (Token token : tokens) {
					int termFreq = token.getTermFreq();

					double irfweight = Application.round(
							(double) Math.log(Application.totalNoOfResources
									/ word2freq.get(token.getText())), 2);
					sum_tfirf = sum_tfirf + (termFreq * irfweight);

					token.setTfIrf(termFreq * irfweight);
				}
				postid2tfirf.put(postid, sum_tfirf);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Creating Inverse Freq Map completed...");
	}

}
