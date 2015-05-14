/**
 * 
 */
package i5.las2peer.services.servicePackage.indexer;

import i5.las2peer.services.servicePackage.entities.DataEntity;
import i5.las2peer.services.servicePackage.utils.Application;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;

/**
 * @author sathvik
 *
 */
public class LuceneMysqlIndexer {
    private ConnectionSource connSrc;
    private IndexWriter indexWriter;

    private static String indexBasePath = "luceneIndex/";

    /**
     * 
     * @param connectionSrc
     * @param indexDirectoryPath
     * @throws IOException
     */
    public LuceneMysqlIndexer(ConnectionSource connectionSrc, String indexDirectoryPath) throws IOException {
	connSrc = connectionSrc;
	Directory indexDir = FSDirectory.open(new File(indexBasePath + indexDirectoryPath).toPath());
	IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
	indexWriter = new IndexWriter(indexDir, config);
    }

    /**
     * 
     * @throws SQLException
     * @throws IOException
     */
    public void buildIndex() throws SQLException, IOException {

	// If index already exists. Delete all.
	if (indexWriter != null) {
	    indexWriter.deleteAll();
	}

	Document doc;

	Dao<DataEntity, Long> postsDao = DaoManager.createDao(connSrc, DataEntity.class);
	List<DataEntity> data_entites = postsDao.queryForAll();
	Application.totalNoOfResources = data_entites.size();
	StringBuffer fullSearchableText;

	for (DataEntity entity : data_entites) {
	    doc = new Document();
	    long postId = entity.getPostId();
	    String title = entity.getTitle();
	    String body = entity.getBody();
	    long parentId = entity.getParentId();
	    long userId = entity.getOwnerUserId();

	    fullSearchableText = new StringBuffer();
	    if (title != null && title.length() > 0) {
		fullSearchableText.append(title + " ");
	    }

	    if (body != null && body.length() > 0) {
		fullSearchableText.append(body);
	    }

	    doc.add(new StringField("postid", String.valueOf(postId), Field.Store.YES));
	    doc.add(new StringField("parentid", String.valueOf(parentId), Field.Store.YES));
	    doc.add(new StringField("userid", String.valueOf(userId), Field.Store.YES));
	    doc.add(new TextField("searchableText", fullSearchableText.toString(), Field.Store.YES));

	    indexWriter.addDocument(doc);
	}

	if (indexWriter != null) {
	    // TODO: Add relative path of the the index directory to the
	    // datainfo table.
	    indexWriter.close();
	}

    }
}
