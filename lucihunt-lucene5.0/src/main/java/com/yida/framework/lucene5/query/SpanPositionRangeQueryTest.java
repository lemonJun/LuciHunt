package com.yida.framework.lucene5.query;

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.spans.SpanMultiTermQueryWrapper;
import org.apache.lucene.search.spans.SpanNearQuery;
import org.apache.lucene.search.spans.SpanNotQuery;
import org.apache.lucene.search.spans.SpanPositionRangeQuery;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.spans.SpanTermQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

/**
 * SpanPositionRangeQuery测试
 * @author Lanxiaowei
 *
 */
public class SpanPositionRangeQueryTest {
    public static void main(String[] args) throws IOException {
        Directory dir = new RAMDirectory();
        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        iwc.setOpenMode(OpenMode.CREATE);
        IndexWriter writer = new IndexWriter(dir, iwc);

        Document doc = new Document();
        doc.add(new TextField("text", "quick brown fox", Field.Store.YES));
        writer.addDocument(doc);

        doc = new Document();
        doc.add(new TextField("text", "jumps over lazy broun dog", Field.Store.YES));
        writer.addDocument(doc);

        doc = new Document();
        doc.add(new TextField("text", "jumps over extremely very lazy broxn dog", Field.Store.YES));
        writer.addDocument(doc);

        writer.close();

        IndexReader reader = DirectoryReader.open(dir);
        IndexSearcher searcher = new IndexSearcher(reader);

        FuzzyQuery fq = new FuzzyQuery(new Term("text", "broan"));
        SpanQuery sfq = new SpanMultiTermQueryWrapper<FuzzyQuery>(fq);
        // will not match quick brown fox
        SpanPositionRangeQuery spanPositionRangeQuery = new SpanPositionRangeQuery(sfq, 5, 7);

        TopDocs results = searcher.search(spanPositionRangeQuery, null, 100);
        ScoreDoc[] scoreDocs = results.scoreDocs;

        for (int i = 0; i < scoreDocs.length; ++i) {
            //System.out.println(searcher.explain(query, scoreDocs[i].doc));
            int docID = scoreDocs[i].doc;
            Document document = searcher.doc(docID);
            String path = document.get("text");
            System.out.println("text:" + path);
        }
    }
}
