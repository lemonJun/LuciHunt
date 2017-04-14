package index;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import document.Document;
import store.Directory;
import store.IndexInput;
import util.BitVector;

public class SegmentReader extends IndexReader {

    private String segment;

    private FieldInfos fieldinfos;
    private FieldsReader fieldsreader;

    private TermInfosReader tis;
    private BitVector deledocs;
    private IndexInput freqstream;
    private IndexInput proxstream;
    private TermVectorsReader termvectorreader;

    protected SegmentReader(Directory directory) {
        super(directory);
    }

    public static SegmentReader get(SegmentInfo si) throws IOException {
        return get(si.dir, si, null, false, false);
    }

    public static SegmentReader get(SegmentInfos sis, SegmentInfo si, boolean closeDir) throws IOException {
        return get(si.dir, si, sis, closeDir, true);
    }

    public static SegmentReader get(Directory dir, SegmentInfo si, SegmentInfos sis, boolean closeDir, boolean ownDir) throws IOException {
        SegmentReader instance;
        try {
            instance = (SegmentReader) IMPL.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("cannot load SegmentReader class: " + e);
        }
        instance.init(dir, sis, closeDir, ownDir);
        instance.initialize(si);
        return instance;
    }

    private void initialize(SegmentInfo si) throws IOException {
        segment = si.name;
        fieldinfos = new FieldInfos(directory(), segment);
        fieldsreader = new FieldsReader(directory(), segment, fieldinfos);
        tis = new TermInfosReader(directory(), segment, fieldinfos);
        if (hasDeletions(si)) {
            deledocs = new BitVector(directory(), segment + ".del");
        }

        freqstream = directory().openInput(segment + FreqProxWriter.FREQ_SUFFIX);
        proxstream = directory().openInput(segment + FreqProxWriter.PROX_SUFFIX);
        if (fieldinfos.hasVectors()) {
            termvectorreader = new TermVectorsReader(directory(), segment, fieldinfos);
        }
    }

    //提交  正是因为reader也能删除文件  所以可以提交  此处产生.del文件
    public void doCommit() throws IOException {

    }

    protected void doclose() throws IOException {
        fieldsreader.close();
        tis.close();
        if (freqstream != null) {
            freqstream.close();
        }
        if (proxstream != null) {
            freqstream.close();
        }

        if (termvectorreader != null) {
            termvectorreader.close();
        }

    }

    //下面这个是为了安全起见  
    private static Class IMPL;
    static {
        try {
            String name = System.getProperty("index.SegmentReader.class", SegmentReader.class.getName());
            IMPL = Class.forName(name);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("cannot load SegmentReader class: " + e);
        } catch (SecurityException se) {
            try {
                IMPL = Class.forName(SegmentReader.class.getName());
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("cannot load default SegmentReader class: " + e);
            }
        }
    }

    //是否有删除文件
    static boolean hasDeletions(SegmentInfo si) throws IOException {
        return si.dir.fileExists(si.name + ".del");
    }

    public boolean hasDeletions() {
        return deledocs != null;
    }

    @Override
    public int numDocs() {
        int n = maxDoc();
        if (deledocs != null)
            n -= deledocs.count();
        return n;
    }

    @Override
    public int maxDoc() {
        return fieldsreader.getSize();
    }

    @Override
    public Document document(int n) throws IOException {
        return fieldsreader.doc(n);
    }

    @Override
    public boolean isDeleted(int n) {
        return (deledocs != null && deledocs.get(n));
    }

    @Override
    public Collection<String> getFieldNames(FieldOption fieldOption) {
        Set<String> fieldSet = new HashSet<String>();
        for (int i = 0; i < fieldinfos.size(); i++) {
            FieldInfo fi = fieldinfos.fieldInfo(i);
            if (fieldOption == IndexReader.FieldOption.ALL) {
                fieldSet.add(fi.name);
            } else if (!fi.isIndexed && fieldOption == IndexReader.FieldOption.UNINDEXED) {
                fieldSet.add(fi.name);
            } else if (fi.isIndexed && fieldOption == IndexReader.FieldOption.INDEXED) {
                fieldSet.add(fi.name);
            } else if (fi.isIndexed && fi.storeTermVector == false && fieldOption == IndexReader.FieldOption.INDEXED_NO_TERMVECTOR) {
                fieldSet.add(fi.name);
            } else if (fi.storeTermVector == true && fi.storePositionWithTermVector == false && fi.storeOffsetWithTermVector == false && fieldOption == IndexReader.FieldOption.TERMVECTOR) {
                fieldSet.add(fi.name);
            } else if (fi.isIndexed && fi.storeTermVector && fieldOption == IndexReader.FieldOption.INDEXED_WITH_TERMVECTOR) {
                fieldSet.add(fi.name);
            } else if (fi.storePositionWithTermVector && fi.storeOffsetWithTermVector == false && fieldOption == IndexReader.FieldOption.TERMVECTOR_WITH_POSITION) {
                fieldSet.add(fi.name);
            } else if (fi.storeOffsetWithTermVector && fi.storePositionWithTermVector == false && fieldOption == IndexReader.FieldOption.TERMVECTOR_WITH_OFFSET) {
                fieldSet.add(fi.name);
            } else if ((fi.storeOffsetWithTermVector && fi.storePositionWithTermVector) && fieldOption == IndexReader.FieldOption.TERMVECTOR_WITH_POSITION_OFFSET) {
                fieldSet.add(fi.name);
            }
        }
        return fieldSet;
    }

    @Override
    public byte[] norms(String field) throws IOException {
        return null;
    }

}
