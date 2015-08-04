/**
 * 
 */
package i5.las2peer.services.servicePackage.lucene.indexer;

import i5.las2peer.services.servicePackage.models.Token;
import i5.las2peer.services.servicePackage.utils.Application;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

/**
 * @author sathvik
 *
 */
public class DbTextIndexer {

    private HashMultiset queryTerms;

    private HashMap<Object, Integer> word2freq;
    private HashMap<Long, Long> postId2userId;
    // private Map<Long, UserEntity> userId2userObj = new HashMap<Long,
    // UserEntity>();

    private Map<Long, Double> postid2tfirf;

    private HashMultimap<Long, Long> parentId2postIds;

    private HashMultimap<Long, Token> postid2Tokens = HashMultimap.create();

    private final int THRESHOLD_WORD_FREQ = 0;
    // private ConnectionSource mConnectionSrc;
    private int noOfDocs;

    private static String dataIndexBasePath = "luceneIndex/%s/data";

    /** Creates a new instance of SearchEngine */
    public DbTextIndexer(int noOfDocs) throws IOException {

	clear();

	word2freq = new HashMap<Object, Integer>();
	postId2userId = new HashMap<Long, Long>();
	postid2tfirf = new HashMap<Long, Double>();

	postid2Tokens = HashMultimap.create();
	parentId2postIds = HashMultimap.create();

	this.noOfDocs = noOfDocs;

    }

    public HashMap<Long, Long> getPostId2UserIdMap() {
	return postId2userId;
    }

    public Map<Long, Double> getTfIrfMap() {
	return postid2tfirf;
    }

    public HashMultimap<Long, Token> getTokenMap() {
	return postid2Tokens;
    }

    public void buildIndex(TopDocs docs, String queryString, Date date, String filepath) throws IOException, ParseException, SQLException {

	queryTerms = HashMultiset.create(Splitter.on(CharMatcher.WHITESPACE).omitEmptyStrings().split(queryString));

	addTokenFreqMap(docs, date, filepath);
	createInverseResFreqMap();
    }

    private void addTokenFreqMap(TopDocs docs, Date date, String filepath) throws IOException {

	IndexSearcher dataSearcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(new File(String.format(dataIndexBasePath,
		filepath)).toPath())));

	for (ScoreDoc scoreDoc : docs.scoreDocs) {
	    Document doc = dataSearcher.doc(scoreDoc.doc);

	    long postId = doc.get("postid") != null ? Long.parseLong(doc.get("postid")) : -1;
	    long userId = doc.get("userid") != null ? Long.parseLong(doc.get("userid")) : -1;

	    String text = doc.get("searchableText");

	    Multiset<String> bagOfWords = HashMultiset.create(Splitter.on(CharMatcher.WHITESPACE).omitEmptyStrings().split(text));
	    int totalcount = 0;

	    Token token;
	    int count = 0;
	    // Iterate the Query terms.
	    // Ignore if freq is too less.
	    for (Object word : queryTerms.elementSet()) {
		System.out.println("WORD:: " + word);
		count = bagOfWords.count(word);

		totalcount = word2freq.get((String) word) != null ? word2freq.get((String) word) + count : 0;
		word2freq.put((String) word, totalcount);
		// System.out.println("THRESHOLD FREQ TEST " + count);
		// if (fmtCreationDate != null && fmtCreationDate.before(date))
		// {
		    token = new Token(postId, text);
		    token.setFrequnecy(count);
		    token.setName((String) word);

		// if (parentId > 0) {
		    System.out.println("POST ID:: " + postId);
			postid2Tokens.put(postId, token);
		// parentId2postIds.put(parentId, postId);
		// }
		// }
		// System.out.println("USERID::" + userId);
		if (userId > 0) {
		    postId2userId.put(postId, userId);
		}
	    }

	}

    }

    // private void addTokenFreqMap(DataEntity entity, Date date) {
    //
    // long postid = entity.getPostId();
    // String text = entity.getCleanText();
    // long parentId = entity.getParentId();
    // long userId = entity.getOwnerUserId();
    //
    // String creationDate = entity.getCreationDate();
    // Date fmtCreationDate = null;
    // DateFormat format = new SimpleDateFormat("yyyy-mm-dd");
    // if (creationDate != null) {
    // try {
    // fmtCreationDate = format.parse(creationDate);
    // } catch (java.text.ParseException e) {
    // e.printStackTrace();
    // }
    // }
    //
    // Multiset<String> bagOfWords =
    // HashMultiset.create(Splitter.on(CharMatcher.WHITESPACE).omitEmptyStrings().split(text));
    // int totalcount = 0;
    //
    // Token token;
    // int count = 0;
    // // Iterate the Query terms.
    // // Ignore if freq is too less.
    // for (Object word : queryTerms.elementSet()) {
    // count = bagOfWords.count(word);
    //
    // totalcount = word2freq.get((String) word) != null ?
    // word2freq.get((String) word) + count : 0;
    // word2freq.put((String) word, totalcount);
    // // System.out.println("THRESHOLD FREQ TEST " + count);
    // if (count > THRESHOLD_WORD_FREQ && fmtCreationDate != null &&
    // fmtCreationDate.before(date)) {
    // token = new Token(postid, text);
    // token.setFrequnecy(count);
    // token.setName((String) word);
    //
    // if (parentId > 0) {
    // postid2Tokens.put(postid, token);
    // parentId2postIds.put(parentId, postid);
    // }
    // }
    // // System.out.println("USERID::" + userId);
    // if (userId > 0) {
    // postId2userId.put(postid, userId);
    // }
    // }
    //
    // }

    // public void updateUserMap(UserEntity entity) throws SQLException {
    // userId2userObj.put(entity.getUserId(), entity);
    // }

    private void createInverseResFreqMap() {
	// System.out.println("Creating Inverse Freq Map...");
	try {
	    for (Map.Entry entry : postid2Tokens.entries()) {
		Long postid = (Long) entry.getKey();
		Set<Token> tokens = postid2Tokens.get(postid);
		double sum_tfirf = 0; // TFIRF of individual token combined for
				      // entire post.
		for (Token token : tokens) {
		    int termFreq = token.getFreq();

		    int tokenCount = word2freq.containsKey(token.getName()) ? word2freq.get(token.getName()) : 0;
		    double irfweight = 1;
		    if (tokenCount > 0) {
			irfweight = Application.round((double) Math.log(noOfDocs / word2freq.get(token.getName())), 2);
		    }
		    sum_tfirf = sum_tfirf + (termFreq * irfweight);

		    token.setTfIrf(termFreq * irfweight);
		}
		System.out.println("Inserting TFIRF...");
		postid2tfirf.put(postid, sum_tfirf);

	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
	// System.out.println("Creating Inverse Freq Map completed...");
    }

    private void clear() {
	if (word2freq != null && word2freq.isEmpty() == false)
	    word2freq.clear();
	if (postId2userId != null && postId2userId.isEmpty() == false)
	    postId2userId.clear();
	if (postid2tfirf != null && postid2tfirf.isEmpty() == false)
	    postid2tfirf.clear();

	if (postid2Tokens != null && postid2Tokens.isEmpty() == false)
	    postid2Tokens.clear();
	if (parentId2postIds != null && parentId2postIds.isEmpty() == false)
	    parentId2postIds.clear();

    }
}
