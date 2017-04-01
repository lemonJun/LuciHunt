package org.apache.lucene;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Copyright 2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.lucene.analysis.SimpleAnalyzer;
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
            IndexWriter writer = new IndexWriter(new File("D:/luncene1.9"), new SimpleAnalyzer(), true);

            writer.setMergeFactor(20);
            writer.setUseCompoundFile(false);
            //            indexDocs(writer, new File("D:/logs"));
            //            writer.optimize();
            indexEng(writer);
            //            indexStr(writer);
            //            indexStr2(writer);
            //            indexStr3(writer);
            writer.close();

            Date end = new Date();

            System.out.print(end.getTime() - start.getTime());
            System.out.println(" total milliseconds");
            //
            //            Runtime runtime = Runtime.getRuntime();
            //
            //            System.out.print(runtime.freeMemory());
            //            System.out.println(" free memory before gc");
            //            System.out.print(runtime.totalMemory());
            //            System.out.println(" total memory before gc");
            //            
            //            runtime.gc();
            //
            //            System.out.print(runtime.freeMemory());
            //            System.out.println(" free memory after gc");
            //            System.out.print(runtime.totalMemory());
            //            System.out.println(" total memory after gc");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(" caught a " + e.getClass() + "\n with message: " + e.getMessage());
        }
    }

    public static void indexStr(IndexWriter writer) {
        try {
            String line = "java";
            Document doc = new Document();
            doc.add(new Field("line", line, Field.Store.YES, Field.Index.TOKENIZED, TermVector.WITH_POSITIONS_OFFSETS));
            //            doc.add(new Field("name", line, Field.Store.YES, Field.Index.TOKENIZED, TermVector.WITH_POSITIONS_OFFSETS));
            writer.addDocument(doc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void indexStr2(IndexWriter writer) {
        try {
            String line = "you  ";
            Document doc = new Document();
            doc.add(new Field("line", line, Field.Store.YES, Field.Index.TOKENIZED, TermVector.WITH_POSITIONS_OFFSETS));
            doc.add(new Field("name", line, Field.Store.YES, Field.Index.TOKENIZED, TermVector.WITH_POSITIONS_OFFSETS));
            //            doc.add(new Field("mame", line, Field.Store.YES, Field.Index.TOKENIZED, TermVector.WITH_POSITIONS_OFFSETS));
            writer.addDocument(doc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void indexStr3(IndexWriter writer) {
        try {
            String line = "hello ";
            Document doc = new Document();
            doc.add(new Field("line", line, Field.Store.YES, Field.Index.TOKENIZED, TermVector.WITH_POSITIONS_OFFSETS));
            doc.add(new Field("ha", line, Field.Store.YES, Field.Index.TOKENIZED, TermVector.WITH_POSITIONS_OFFSETS));
            //            doc.add(new Field("mame", line, Field.Store.YES, Field.Index.TOKENIZED, TermVector.WITH_POSITIONS_OFFSETS));
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
