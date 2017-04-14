package index;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import document.Document;
import store.Directory;

/**
 * 底层的实现其实还是 segmentreader  
 * 只不是此处处理的是有多个段的情况   
 *
 * @author WangYazhou
 * @date  2017年4月14日 下午5:01:50
 * @see
 */
public class MultiReader extends IndexReader {

    private IndexReader[] subReaders;//保存所有的段
    private int[] starts; //保存每一个段开始的doc的序号
    private Hashtable normsCache = new Hashtable();
    private int maxDoc = 0;
    private int numDocs = -1;
    private boolean hasDeletions = false;

    public MultiReader(IndexReader[] subReaders) throws IOException {
        super(subReaders.length == 0 ? null : subReaders[0].directory());
        initialize(subReaders);
    }

    /** Construct reading the named set of readers. */
    MultiReader(Directory directory, SegmentInfos sis, boolean closeDirectory, IndexReader[] subReaders) {
        super(directory, sis, closeDirectory);
        initialize(subReaders);
    }

    private void initialize(IndexReader[] subReaders) {
        this.subReaders = subReaders;
        starts = new int[subReaders.length + 1]; // build starts array
        for (int i = 0; i < subReaders.length; i++) {
            starts[i] = maxDoc;
            maxDoc += subReaders[i].maxDoc(); // compute maxDocs

            if (subReaders[i].hasDeletions())
                hasDeletions = true;
        }
        starts[subReaders.length] = maxDoc;
    }

    @Override
    public int numDocs() {
        int num = 0;
        for (int i = 0; i < subReaders.length; i++) {
            num += subReaders[i].numDocs();
        }
        return num;
    }

    @Override
    public int maxDoc() {
        return maxDoc;
    }

    @Override
    public Document document(int n) throws IOException {
        int i = readerIndex(n);
        return subReaders[i].document(n - starts[i]);
    }

    @Override
    public boolean isDeleted(int n) {
        int i = readerIndex(n);
        return subReaders[i].isDeleted(n - starts[i]);
    }

    @Override
    public boolean hasDeletions() {
        return hasDeletions;
    }

    //判断一个文档在哪个段中   因为文档是按段排序的 
    private int readerIndex(int n) { // find reader for doc n:
        int lo = 0; // search starts array
        int hi = subReaders.length - 1; // for first element less

        while (hi >= lo) {
            int mid = (lo + hi) >> 1;
            int midValue = starts[mid];
            if (n < midValue)
                hi = mid - 1;
            else if (n > midValue)
                lo = mid + 1;
            else { // found a match
                while (mid + 1 < subReaders.length && starts[mid + 1] == midValue) {
                    mid++; // scan to last match
                }
                return mid;
            }
        }
        return hi;
    }

    @Override
    public Collection getFieldNames(FieldOption fieldNames) {
        Set fieldSet = new HashSet();
        for (int i = 0; i < subReaders.length; i++) {
            IndexReader reader = subReaders[i];
            Collection names = reader.getFieldNames(fieldNames);
            fieldSet.addAll(names);
        }
        return fieldSet;
    }

    @Override
    public byte[] norms(String field) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

}
