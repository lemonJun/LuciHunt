package org.apache.lucene.my;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.FieldType.NumericType;
import org.apache.lucene.index.FieldInfo.DocValuesType;
import org.apache.lucene.index.FieldInfo.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;

public class IndexTest {

    @Test
    public void index() {
        try {
            Analyzer analyzer = new StandardAnalyzer();

            Directory directory = FSDirectory.open(new File("D:/lucene_4_10"));
            IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_10_4, analyzer);
            config.setUseCompoundFile(false);
            FieldType num = new FieldType();
            num.setStored(true);//设置存储  
            num.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);//设置索引类型  
            //            num.setDocValueType(DocValuesType.SORTED);//DocValue类型 O
            // To store an index on disk, use this instead:
            // Directory directory = FSDirectory.open(new File("/tmp/testindex"));
            IndexWriter iwriter = new IndexWriter(directory, config);
            Document doc = new Document();
            String text = "This is the text to be indexed. hello world  ";
            doc.add(new Field("fieldname", text, num));
            iwriter.addDocument(doc);
            iwriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
