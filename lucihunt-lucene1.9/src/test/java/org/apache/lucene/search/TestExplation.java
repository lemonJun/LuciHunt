package org.apache.lucene.search;

import java.io.File;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.store.FSDirectory;

/**
 * 
 * 
 * 0.19936351 = sum of:
  0.067106694 = weight(line:the in 2), product of:
    0.5801764 = queryWeight(line:the), product of:
      0.71231794 = idf(docFreq=3)
      0.8144908 = queryNorm
    0.11566602 = fieldWeight(line:the in 2), product of:
      1.7320508 = tf(termFreq(line:the)=3)
      0.71231794 = idf(docFreq=3)
      0.09375 = fieldNorm(field=line, doc=2)
  0.13225682 = weight(line:with in 2), product of:
    0.8144908 = queryWeight(line:with), product of:
      1.0 = idf(docFreq=2)
      0.8144908 = queryNorm
    0.16237976 = fieldWeight(line:with in 2), product of:
      1.7320508 = tf(termFreq(line:with)=3)
      1.0 = idf(docFreq=2)
      0.09375 = fieldNorm(field=line, doc=2)
 *
 * @author WangYazhou
 * @date  2017年3月1日 下午3:20:05
 * @see
 */
public class TestExplation {

    public static void main(String[] args) {
        exp();
    }

    public static void exp() {
        try {
            BooleanQuery bq = new BooleanQuery();
            TermQuery q = new TermQuery(new Term("line", "java"));
            bq.add(q, Occur.MUST);
            TermQuery q2 = new TermQuery(new Term("name", "php"));
            q2.setBoost(2.0f);
            bq.add(q2, Occur.SHOULD);
            System.out.println(bq.toString());
            IndexSearcher search = new IndexSearcher(FSDirectory.getDirectory(new File("D:/luncene1.9"), false));
            TopDocs tdocs = search.search(bq, null, 5);
            System.out.println(tdocs.totalHits);
            for (int i = 0; i < tdocs.scoreDocs.length; i++) {
                Explanation exp = search.explain(bq, i);
                System.out.println(exp.toString());
                System.out.println("--------------------------------");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
