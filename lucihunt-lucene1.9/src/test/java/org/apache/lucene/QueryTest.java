package org.apache.lucene;

import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
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
                System.out.println();
            }
            searcher.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void booleanmustquery() {
        try {
            IndexSearcher searcher = new IndexSearcher("D:/luncene1.9");
            BooleanQuery query = new BooleanQuery();
            query.add(new TermQuery(new Term("line", "java")), Occur.MUST);
            query.add(new TermQuery(new Term("name", "java")), Occur.MUST);

            TopDocs tops = searcher.search(query, null, 10);
            for (ScoreDoc doc : tops.scoreDocs) {
                System.out.print(doc.doc + ":");
                System.out.print(doc.score + " ");
                System.out.println();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void booleanquery() {
        try {
            IndexSearcher searcher = new IndexSearcher("D:/luncene1.9");
            BooleanQuery query = new BooleanQuery();
            query.add(new TermQuery(new Term("line", "java")), Occur.MUST);
            query.add(new TermQuery(new Term("name", "java")), Occur.SHOULD);

            TopDocs tops = searcher.search(query, null, 10);
            for (ScoreDoc doc : tops.scoreDocs) {
                System.out.print(doc.doc + " ");
                System.out.print(doc.score + " ");
                System.out.println();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void phrasequery() {
        try {
            IndexSearcher searcher = new IndexSearcher("D:/luncene1.9");
            PhraseQuery query = new PhraseQuery();
            query.add(new Term("line", "java"));
            query.add(new Term("line", "you"));

            query.setSlop(3);

            TopDocs tops = searcher.search(query, null, 10);
            for (ScoreDoc doc : tops.scoreDocs) {
                System.out.print(doc.doc + ": ");
                System.out.print(doc.score + " ");
                System.out.println();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void phrasparseequery() {
        try {
            IndexSearcher searcher = new IndexSearcher("D:/luncene1.9");
            QueryParser parser = new QueryParser("line", new StandardAnalyzer());
            Query query = parser.parse("\"you java\" ~0");

            TopDocs tops = searcher.search(query, null, 10);
            for (ScoreDoc doc : tops.scoreDocs) {
                System.out.print(doc.doc + ": ");
                System.out.print(doc.score + " ");
                System.out.println();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
