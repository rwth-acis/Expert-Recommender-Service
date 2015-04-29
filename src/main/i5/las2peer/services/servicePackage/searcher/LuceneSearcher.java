/**
 * 
 */
package i5.las2peer.services.servicePackage.searcher;

import i5.las2peer.services.servicePackage.models.Token;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
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
    private IndexSearcher searcher = null;
    private QueryParser parser = null;
    private String queryString = null;

    private HashMultimap<Long, Token> postid2Tokens = HashMultimap.create();
    private HashMultimap<Long, Long> parentId2postIds = HashMultimap.create();

    private HashMap<Long, Long> postId2userId;

    private static String indexBasePath = "luceneIndex/";

    /**
     * 
     * @param query
     * @param indexFilepath
     * @throws IOException
     */
    public LuceneSearcher(String query, String indexFilepath) throws IOException {
	searcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(new File(indexBasePath + indexFilepath).toPath())));
	parser = new QueryParser("searchableText", new StandardAnalyzer());
	queryString = query;

	postId2userId = new HashMap<Long, Long>();
    }

    /**
     * 
     * @param queryString
     * @param n
     * @return
     * @throws IOException
     * @throws ParseException
     */
    public TopDocs performSearch(String queryString, int n) throws IOException, ParseException {
	Query query = parser.parse(queryString);
	TopDocs docs = searcher.search(query, n);
	System.out.println("No of HITS:: " + docs.totalHits);

	return docs;
    }

    /**
     * 
     * @param docs
     * @throws IOException
     */
    public void buildQnAMap(TopDocs docs) throws IOException {
	Token token = null;
	for (ScoreDoc scoreDoc : docs.scoreDocs) {
	    Document doc = searcher.doc(scoreDoc.doc);

	    long postId = doc.get("postid") != null ? Long.parseLong(doc.get("postid")) : -1;
	    long parentId = doc.get("parentid") != null ? Long.parseLong(doc.get("parentid")) : -1;
	    long userId = doc.get("userid") != null ? Long.parseLong(doc.get("userid")) : -1;

	    String text = doc.get("searchableText");
	    // System.out.println(postId + " :: " + parentId);

	    if (postId > 0) {
		token = new Token(postId, text);
		token.setFrequnecy(0);
	    }

	    postid2Tokens.put(postId, token);
	    if (parentId > 0) {
		parentId2postIds.put(parentId, postId);
	    }

	    // System.out.println("USERID::" + userId);
	    if (userId > 0) {
		postId2userId.put(postId, userId);
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
	    Query query = parser.parse(queryString);
	    query.extractTerms(terms);
	}

	ArrayList<String> qTerms = new ArrayList<String>();
	if (terms != null && terms.size() > 0) {
	    java.util.Iterator<Term> it = terms.iterator();
	    while (it.hasNext()) {
		qTerms.add(it.next().toString());
	    }

	    return qTerms;
	}

	return null;
    }

    public Document getDocument(int docId) throws IOException {
	return searcher.doc(docId);
    }
}
