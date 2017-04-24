package org.apache.lucene;

import java.io.IOException;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.junit.Test;

public class QueryTest {
    
    @Test
    public void termquery() {
        try {
            IndexSearcher searcher = new IndexSearcher("D:/luncene1.9");
            TopDocs tops = searcher.search(new TermQuery(new Term("line", "java")), null, 10);
            for (ScoreDoc doc : tops.scoreDocs) {
                System.out.print(doc.doc + " ");
                System.out.print(doc.score + " ");
                System.out.print(doc.toString());
                System.out.println();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
