//package test;
//
//import java.io.IOException;
//
//import org.junit.Test;
//
//import analysis.WhitespaceAnalyzer;
//import document.Document;
//import document.Field;
//import index.DocumentWriter;
//import index.FieldInfos;
//import index.TermInfosWriter;
//import search.Similarity;
//import store.Directory;
//import store.FSDirectory;
//
//public class TermTermInfos {
//
//    @Test
//    public void terterminfo() {
//        try {
//            Directory dir = FSDirectory.getDirectory("D:/testdir", false);
//
//            DocumentWriter dw = new DocumentWriter(dir, new WhitespaceAnalyzer(), Similarity.getDefault(), 1024);
//            Document doc = new Document();
//            doc.add(new Field("name", "java is good ,php is bad", Field.Store.YES, Field.Index.TOKENIZED));
//            FieldInfos fieldInfos = new FieldInfos();
//            fieldInfos.add(doc);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//}
