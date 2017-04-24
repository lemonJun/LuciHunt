package org.apache.lucene;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.index.TermPositions;
import org.apache.lucene.search.IndexSearcher;

public class ShowTermS {
    public static void main(String[] args) {
        try {
            IndexSearcher searcher = new IndexSearcher("D:/luncene1.9");
            IndexReader reader = searcher.getIndexReader();
            TermEnum enumeration = reader.terms();
            int ii = 0;
            while (enumeration.next() && ii < 20)//遍历索引此表
            {
                StringBuffer sb = new StringBuffer(1000);
                ii++;
                Term term = enumeration.term();
                //out是一个输出流，它输出到一个文本，这里没有给出out的定义，读者可以自己定义它
                sb.append(term.text());
                sb.append("\n     域:").append(term.field());
                sb.append(" DF:").append(enumeration.docFreq());
                TermPositions posEnum = reader.termPositions(term);
                while (posEnum.next()) {
                    sb.append("\n     TF:");
                    sb.append(posEnum.freq());
                    sb.append(" Doc:").append(posEnum.doc());
                    sb.append(" Position:");
                    for (int i = 0; i < posEnum.freq(); i++)
                        sb.append("[" + posEnum.nextPosition() + "]");
                    sb.append("  raw:");
                    Document d = reader.document(posEnum.doc());
                    sb.append(d.get(term.field()).toString());//DOCNO 是笔者所使用语料的文档的标号，对应一般使用者的"filename"域
                }
                System.out.println(sb.toString());
            }
            searcher.close();
        } catch (

        IOException e) {
            e.printStackTrace();
        }
    }

}
