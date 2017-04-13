package org.apache.lucene;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.demo.FileDocument;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.TermVector;
import org.apache.lucene.index.IndexWriter;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

class IndexTest {

    public static void main(String[] args) {
        index();
    }

    public static void index() {
        try {
            Date start = new Date();
            IndexWriter writer = new IndexWriter(new File("D:/luncene1.9"), new StandardAnalyzer(), true);

            writer.setMergeFactor(20);
            writer.setUseCompoundFile(false);
            //            indexDocs(writer, new File("D:/logs"));
            //            writer.optimize();
            //            indexEng(writer);
            indexStr(writer);
            indexStr2(writer);
            indexStr3(writer);
            writer.close();

            Date end = new Date();

            System.out.print(end.getTime() - start.getTime());
            System.out.println(" total milliseconds");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(" caught a " + e.getClass() + "\n with message: " + e.getMessage());
        }
    }

    public static void indexStr(IndexWriter writer) {
        try {
            Document doc = new Document();
            doc.add(new Field("line", "java is good php is bad so java", Field.Store.YES, Field.Index.TOKENIZED, TermVector.WITH_POSITIONS_OFFSETS));
            doc.add(new Field("name", "hi java ", Field.Store.YES, Field.Index.TOKENIZED, TermVector.WITH_POSITIONS_OFFSETS));
            writer.addDocument(doc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void indexStr2(IndexWriter writer) {
        try {
            Document doc = new Document();
            doc.add(new Field("line", "you love java but not php", Field.Store.YES, Field.Index.TOKENIZED, TermVector.WITH_POSITIONS_OFFSETS));
            doc.add(new Field("name", "see you ", Field.Store.YES, Field.Index.TOKENIZED, TermVector.WITH_POSITIONS_OFFSETS));
            //            doc.add(new Field("mame", line, Field.Store.YES, Field.Index.TOKENIZED, TermVector.WITH_POSITIONS_OFFSETS));
            writer.addDocument(doc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void indexStr3(IndexWriter writer) {
        try {
            Document doc = new Document();
            doc.add(new Field("line", "php say hello world java ", Field.Store.YES, Field.Index.TOKENIZED, TermVector.WITH_POSITIONS_OFFSETS));
            doc.add(new Field("mame", "java is bad ", Field.Store.YES, Field.Index.TOKENIZED, TermVector.WITH_POSITIONS_OFFSETS));
            writer.addDocument(doc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void indexEng(IndexWriter writer) {
        try {
            List<String> lines = Files.readLines(new File("D:/eng.txt"), Charsets.UTF_8);
            for (String line : lines) {
                Document doc = new Document();
                doc.add(new Field("line", line, Field.Store.YES, Field.Index.TOKENIZED));
                writer.addDocument(doc);
                System.out.println(line);
                TimeUnit.SECONDS.sleep(5);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void indexDocs(IndexWriter writer, File file) throws Exception {
        if (file.isDirectory()) {
            String[] files = file.list();
            for (int i = 0; i < files.length; i++)
                indexDocs(writer, new File(file, files[i]));
        } else {
            System.out.println("adding " + file);
            writer.addDocument(FileDocument.Document(file));
        }
    }
}
