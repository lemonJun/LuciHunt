package test;

import java.io.IOException;

import analysis.Analyzer;
import analysis.WhitespaceAnalyzer;
import document.Document;
import document.Field;
import index.IndexWriter;
import store.Directory;
import store.FSDirectory;

public class TestIndexWriter {

    public static void main(String[] args) {
        try {
            Directory dir = FSDirectory.getDirectory("D:/testdir", true);
            Analyzer ana = new WhitespaceAnalyzer();
            IndexWriter iw = new IndexWriter(dir, ana, true);
            Document doc = new Document();
            doc.add(new Field("name", "java is good php is bad", Field.Store.YES, Field.Index.TOKENIZED));
            iw.addDocument(doc);
            iw.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }
    }
}
