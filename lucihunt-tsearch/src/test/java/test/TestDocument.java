package test;

import org.junit.Test;

import document.Document;
import document.Field;

public class TestDocument {

    @Test
    public void add() {
        Document doc = new Document();
        doc.add(new Field("name", "java is good ,php is bad", Field.Store.YES, Field.Index.TOKENIZED));
        System.out.println(doc.toString());
    }

}
