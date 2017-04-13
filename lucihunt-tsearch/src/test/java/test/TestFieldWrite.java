package test;

import java.io.IOException;

import org.junit.Test;

import analysis.WhitespaceAnalyzer;
import document.Document;
import document.Field;
import index.DocumentWriter;
import search.Similarity;
import store.Directory;
import store.FSDirectory;

public class TestFieldWrite {

    @Test
    public void write() {
        try {
            Directory dir = FSDirectory.getDirectory("D:/testdir", false);

            DocumentWriter dw = new DocumentWriter(dir, new WhitespaceAnalyzer(), Similarity.getDefault(), 1024);
            Document doc = new Document();
            doc.add(new Field("name", "java is good ,php is bad", Field.Store.YES, Field.Index.TOKENIZED));

            dw.addDocument("1", doc);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void read() {

    }
}
