package index;

import java.io.IOException;

import store.Directory;

/**
 * 读取词典信息
 * 
 * @author WangYazhou
 * @date  2017年4月14日 上午11:41:05
 * @see
 */
public class TermInfosReader {

    private Directory directory;
    private String segment;
    private FieldInfos fieldInfos;
    private ThreadLocal<SegmentTermEnum> enumerators = new ThreadLocal<SegmentTermEnum>();
    private long size;//term的总个数

    private SegmentTermEnum tisenum;
    private SegmentTermEnum tiienum;

    //缓存索引中的内容
    private Term[] indexTerms = null;//保存所有的词 field:value
    private TermInfo[] indexInfos;//保存所有的词信息 如文档频率  频率倒排表  位置倒排表
    private long[] indexPointers;

    TermInfosReader(Directory dir, String seg, FieldInfos fis) throws IOException {
        this.directory = dir;
        this.segment = seg;
        this.fieldInfos = fis;

        this.tisenum = new SegmentTermEnum(directory.openInput(segment + ".tis"), fieldInfos, false);
        this.tiienum = new SegmentTermEnum(directory.openInput(segment + ".tii"), fieldInfos, true);
        size = this.tisenum.size;
    }

    //没搞懂  此处为啥要注意线程安全呢
    private SegmentTermEnum getEnum() {
        SegmentTermEnum termenum = enumerators.get();
        if (termenum == null) {
            termenum = terms();
            enumerators.set(termenum);
        }
        return termenum;
    }

    public SegmentTermEnum terms() {
        return (SegmentTermEnum) tisenum.clone();
    }

    public SegmentTermEnum terms(Term term) throws IOException {
        get(term);
        return (SegmentTermEnum) getEnum().clone();
    }

    public int getSkipInterval() {
        return tisenum.skipInterval;
    }

    //确保初始化TERM索引文件
    public synchronized void ensureIndexIsRead() throws IOException {
        if (tiienum != null) {
            return;
        }
        try {
            int tiisize = (int) tiienum.size;
            indexTerms = new Term[tiisize];
            indexInfos = new TermInfo[tiisize];
            indexPointers = new long[tiisize];
            for (int i = 0; tiienum.next(); i++) {
                indexTerms[i] = tiienum.term();
                indexInfos[i] = tiienum.termInfo();
                indexPointers[i] = tiienum.indexPointer;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            tiienum.clone();
            tiienum = null;
        }
    }

    private final void seekEnum(int indexOffset) throws IOException {
        getEnum().seek(indexPointers[indexOffset], (indexOffset * getEnum().indexInterval) - 1, indexTerms[indexOffset], indexInfos[indexOffset]);
    }

    TermInfo get(Term term) throws IOException {
        if (size == 0)
            return null;

        ensureIndexIsRead();

        // optimize sequential access: first try scanning cached enum w/o seeking
        SegmentTermEnum enumerator = getEnum();
        if (enumerator.term() != null // term is at or past current
                        && ((enumerator.prev() != null && term.compareTo(enumerator.prev()) > 0) || term.compareTo(enumerator.term()) >= 0)) {
            int enumOffset = (int) (enumerator.position / enumerator.indexInterval) + 1;
            if (indexTerms.length == enumOffset // but before end of block
                            || term.compareTo(indexTerms[enumOffset]) < 0)
                return scanEnum(term); // no need to seek
        }

        // random-access: must seek
        seekEnum(getIndexOffset(term));
        return scanEnum(term);
    }

    final Term get(int position) throws IOException {
        if (size == 0)
            return null;

        SegmentTermEnum enumerator = getEnum();
        if (enumerator != null && enumerator.term() != null && position >= enumerator.position && position < (enumerator.position + enumerator.indexInterval))
            return scanEnum(position); // can avoid seek

        seekEnum(position / enumerator.indexInterval); // must seek
        return scanEnum(position);
    }

    private final Term scanEnum(int position) throws IOException {
        SegmentTermEnum enumerator = getEnum();
        while (enumerator.position < position)
            if (!enumerator.next())
                return null;

        return enumerator.term();
    }

    /** Returns the position of a Term in the set or -1. */
    final long getPosition(Term term) throws IOException {
        if (size == 0)
            return -1;

        ensureIndexIsRead();
        int indexOffset = getIndexOffset(term);
        seekEnum(indexOffset);

        SegmentTermEnum enumerator = getEnum();
        while (term.compareTo(enumerator.term()) > 0 && enumerator.next()) {
        }

        if (term.compareTo(enumerator.term()) == 0)
            return enumerator.position;
        else
            return -1;
    }

    private final TermInfo scanEnum(Term term) throws IOException {
        SegmentTermEnum enumerator = getEnum();
        enumerator.scanTo(term);
        if (enumerator.term() != null && term.compareTo(enumerator.term()) == 0)
            return enumerator.termInfo();
        else
            return null;
    }

    //返回小于等于此term的当近的一个词的偏移量  二分查找法   因为已经排过序了
    private final int getIndexOffset(Term term) {
        int lo = 0; // binary search indexTerms[]
        int hi = indexTerms.length - 1;

        while (hi >= lo) {
            int mid = (lo + hi) >> 1;
            int delta = term.compareTo(indexTerms[mid]);
            if (delta < 0)
                hi = mid - 1;
            else if (delta > 0)
                lo = mid + 1;
            else
                return mid;
        }
        return hi;
    }

    final long size() {
        return size;
    }

    final void close() throws IOException {
        if (tiienum != null)
            tiienum.close();
        if (tisenum != null)
            tisenum.close();
    }

}
