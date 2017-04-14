package index;

import java.io.IOException;

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
    
    

}
