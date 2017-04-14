package index;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import store.Directory;

/**
 * 进行段合并
 *
 * @author WangYazhou
 * @date  2017年4月14日 上午11:02:16
 * @see
 */
public class SegmentMerger {

    private static final Logger logger = LoggerFactory.getLogger(SegmentMerger.class);

    private Directory directory;
    private String segment;//新段的名称
    private int termIndexInterval = IndexWriter.DEFAULT_TERM_INDEX_INTERVAL;
    private Vector<IndexReader> readers = new Vector<IndexReader>();
    private FieldInfos fieldInfos;

    SegmentMerger(Directory dir, String name) {
        directory = dir;
        segment = name;
    }

    SegmentMerger(IndexWriter writer, String name) {
        directory = writer.getDirectory();
        segment = name;
    }

    //增加一个需要合并的段  
    public void add(IndexReader reader) {
        readers.addElement(reader);
    }

    //
    final IndexReader segmentReader(int i) {
        return (IndexReader) readers.elementAt(i);
    }

    final int merger() throws IOException {
        int doccount = mergeFields();//域合并完了
        mergeterms();
        mergenorms();
        
        if (fieldInfos.hasVectors()) {
            mergeVectors();
        }
        return doccount;
    }

    //合并词向量 
    private final void mergeVectors() throws IOException {

    }

    //合并词 
    public final void mergeterms() throws IOException {

    }

    //合并标准化因子
    public final void mergenorms() throws IOException {

    }

    //合并元数据信息  合并段的内容  返回文档的个数
    public final int mergeFields() throws IOException {
        fieldInfos = new FieldInfos();
        int docCount = 0;
        for (int i = 0; i < readers.size(); i++) {
            IndexReader reader = (IndexReader) readers.elementAt(i);
            addIndexed(reader, fieldInfos, reader.getFieldNames(IndexReader.FieldOption.TERMVECTOR_WITH_POSITION_OFFSET), true, true, true);
            addIndexed(reader, fieldInfos, reader.getFieldNames(IndexReader.FieldOption.TERMVECTOR_WITH_POSITION), true, true, false);
            addIndexed(reader, fieldInfos, reader.getFieldNames(IndexReader.FieldOption.TERMVECTOR_WITH_OFFSET), true, false, true);
            addIndexed(reader, fieldInfos, reader.getFieldNames(IndexReader.FieldOption.TERMVECTOR), true, false, false);
            addIndexed(reader, fieldInfos, reader.getFieldNames(IndexReader.FieldOption.INDEXED), false, false, false);
            fieldInfos.add(reader.getFieldNames(IndexReader.FieldOption.UNINDEXED), false);
        }

        FieldsWriter newfieldwriter = new FieldsWriter(directory, segment, fieldInfos);
        try {
            for (int i = 0; i < readers.size(); i++) {
                IndexReader reader = readers.elementAt(i);
                for (int j = 0; j < reader.maxDoc(); j++) {
                    if (!reader.isDeleted(j)) {
                        newfieldwriter.addDocument(reader.document(j));
                        docCount++;
                    }
                }
            }
        } catch (Exception e) {
        } finally {
            newfieldwriter.close();
        }
        return docCount;
    }

    //合并其中一项 
    private void addIndexed(IndexReader reader, FieldInfos fieldInfos, Collection names, boolean storeTermVectors, boolean storePositionWithTermVector, boolean storeOffsetWithTermVector) throws IOException {
        Iterator i = names.iterator();
        while (i.hasNext()) {
            String field = (String) i.next();
            fieldInfos.add(field, true, storeTermVectors, storePositionWithTermVector, storeOffsetWithTermVector, !reader.hasNorms(field));
        }
    }

}
