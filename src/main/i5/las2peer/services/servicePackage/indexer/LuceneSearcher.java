/**
 * 
 */
package i5.las2peer.services.servicePackage.indexer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

/**
 * @author sathvik
 *
 */
public class LuceneSearcher {
	private IndexSearcher searcher = null;
	private QueryParser parser = null;
	private String queryString = null;

	/** Creates a new instance of SearchEngine */
	public LuceneSearcher(String query) throws IOException {
		searcher = new IndexSearcher(DirectoryReader.open(FSDirectory
				.open(new File("indexDirectory").toPath())));
		parser = new QueryParser("searchableText", new StandardAnalyzer());
		queryString = query;
	}

	public TopDocs performSearch(String queryString, int n) throws IOException,
			ParseException {
		Query query = parser.parse(queryString);
		return searcher.search(query, n);
	}

	public ArrayList<String> getQueryTermsAsStrings() throws ParseException {
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

