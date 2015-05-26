/**
 * 
 */
package i5.las2peer.services.servicePackage.searcher;

import i5.las2peer.services.servicePackage.models.SemanticToken;
import i5.las2peer.services.servicePackage.models.Token;
import i5.las2peer.services.servicePackage.semanticTagger.SemanticTagger;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

import com.google.common.collect.HashMultimap;

/**
 * This class uses Apache Lucene searcher to search for the documents containing
 * the query terms.
 * Indexes are searched in the folder where it was original created.
 * This is also used to create Question to Answers map and posts to author map.
 * 
 * 
 * @author sathvik
 *
 */
public class LuceneSearcher {
    private IndexSearcher dataSearcher = null;
    private IndexSearcher semanticDataSearcher = null;

    private QueryParser dataParser = null;
    private String queryString = null;

    private HashMultimap<Long, Token> postid2Tokens = HashMultimap.create();
    private HashMultimap<Long, Long> parentId2postIds = HashMultimap.create();

    private HashMap<Long, Long> postId2userId;
    private QueryParser semanticDataParser;

    private int maxNoOfResults = Integer.MAX_VALUE;

    private static String dataIndexBasePath = "luceneIndex/%s/data";

    /**
     * 
     * @param query
     * @param indexFilepath
     * @throws IOException
     */
    public LuceneSearcher(String query, String indexFilepath) throws IOException {
	dataSearcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(new File(String.format(dataIndexBasePath, indexFilepath)).toPath())));
	semanticDataSearcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(new File(String.format(dataIndexBasePath, indexFilepath))
		.toPath())));

	dataParser = new QueryParser("searchableText", new StandardAnalyzer());
	semanticDataParser = new QueryParser("searchableText", new StandardAnalyzer());

	queryString = query;

	postId2userId = new HashMap<Long, Long>();
    }

    /**
     * 
     * @param queryString
     *            A query string.
     * @param n
     *            Integer value for the maximum number of results to be returned
     *            by lucene searcher.
     * @return
     * @throws IOException
     * @throws ParseException
     */
    public TopDocs performSearch(String queryString, int n) throws IOException, ParseException {
	Query query = dataParser.parse(queryString);
	TopDocs dataDocs = dataSearcher.search(query, n);
	System.out.println("No of HITS:: " + dataDocs.totalHits);
	maxNoOfResults = n;

	return dataDocs;
    }

    public int getTotalNumberOfDocs() {
	return dataSearcher.getIndexReader().numDocs();
    }

    /**
     * 
     * @param docs
     * @throws IOException
     */
    public void buildQnAMap(TopDocs docs, Date date) throws IOException {
	Token token = null;
	DateFormat format = new SimpleDateFormat("yyyy-mm-dd");
	Date fmtCreationDate = null;

	for (ScoreDoc scoreDoc : docs.scoreDocs) {
	    Document doc = dataSearcher.doc(scoreDoc.doc);

	    long postId = doc.get("postid") != null ? Long.parseLong(doc.get("postid")) : -1;
	    long parentId = doc.get("parentid") != null ? Long.parseLong(doc.get("parentid")) : -1;
	    long userId = doc.get("userid") != null ? Long.parseLong(doc.get("userid")) : -1;

	    String creationDate = doc.get("creationDate");

	    if (creationDate != null) {
		try {
		    fmtCreationDate = format.parse(creationDate);
		} catch (java.text.ParseException e) {
		    e.printStackTrace();
		}
	    }

	    String text = doc.get("searchableText");
	    // System.out.println(postId + " :: " + parentId);

	    if (postId > 0) {
		token = new Token(postId, text);
		token.setFrequnecy(0);
	    }

	    postid2Tokens.put(postId, token);

	    // If parentId is present and the post was created before the
	    // requested date add the value to the map.
	    if (parentId > 0 && fmtCreationDate != null && fmtCreationDate.before(date)) {
		parentId2postIds.put(parentId, postId);
	    }
	    // System.out.println("USERID::" + userId);
	    if (userId > 0) {
		postId2userId.put(postId, userId);
	    }

	}

	searchSemantics(date);

    }

    private void searchSemantics(Date date) {

	SemanticToken token = null;
	Date fmtCreationDate = null;
	DateFormat format = new SimpleDateFormat("yyyy-mm-dd");

	SemanticTagger tagger = new SemanticTagger(queryString);
	String tags = tagger.getSemanticData().getTags();

	if (tags != null && tags.length() > 0) {
	    Query entityQuery;
	    try {
		entityQuery = semanticDataParser.parse(tags);
		TopDocs semanticDocs = semanticDataSearcher.search(entityQuery, maxNoOfResults);

		for (ScoreDoc scoreDoc : semanticDocs.scoreDocs) {
		    Document doc = dataSearcher.doc(scoreDoc.doc);

		    long postId = doc.get("postid") != null ? Long.parseLong(doc.get("postid")) : -1;
		    long parentId = doc.get("parentid") != null ? Long.parseLong(doc.get("parentid")) : -1;
		    long userId = doc.get("userid") != null ? Long.parseLong(doc.get("userid")) : -1;

		    String creationDate = doc.get("creationDate");

		    if (creationDate != null) {
			try {
			    fmtCreationDate = format.parse(creationDate);
			} catch (java.text.ParseException e) {
			    e.printStackTrace();
			}
		    }

		    String text = doc.get("searchableText");
		    // System.out.println(postId + " :: " + parentId);

		    if (postId > 0) {
			token = new SemanticToken(postId, text);
			token.setFrequnecy(0);
		    }

		    postid2Tokens.put(postId, token);

		    // If parentId is present and the post was created before
		    // the
		    // requested date add the value to the map.
		    if (parentId > 0 && fmtCreationDate != null && fmtCreationDate.before(date)) {
			if (parentId > 0) {
			    parentId2postIds.put(parentId, postId);
			}

			// System.out.println("USERID::" + userId);
			if (userId > 0) {
			    postId2userId.put(postId, userId);
			}
		    }

		}
	    } catch (ParseException e1) {
		e1.printStackTrace();
	    } catch (IOException e1) {
		e1.printStackTrace();
	    }
	}

    }

    public HashMap<Long, Long> getPostId2UserIdMap() {
	return postId2userId;
    }

    public Map<Long, Collection<Long>> getQnAMap() {
	return (Map<Long, Collection<Long>>) parentId2postIds.asMap();
    }

    private ArrayList<String> getQueryTermsAsStrings() throws ParseException {
	Set<Term> terms = new HashSet<Term>();
	if (queryString != null) {
	    Query query = dataParser.parse(queryString);
	    query.extractTerms(terms);
	}

	ArrayList<String> qTerms = new ArrayList<String>();
	if (terms != null && terms.size() > 0) {
	    Iterator<Term> it = terms.iterator();
	    while (it.hasNext()) {
		qTerms.add(it.next().toString());
	    }

	    return qTerms;
	}

	return null;
    }

    public Document getDocument(int docId) throws IOException {
	return dataSearcher.doc(docId);
    }
}
