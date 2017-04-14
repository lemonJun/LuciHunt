package index;

import java.io.IOException;

import store.Directory;

public class SegmentReader extends IndexReader {

    private String segment;

    private FieldInfos fieldInfos;

    private FieldsReader fieldsreader;

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
        fieldInfos = new FieldInfos(directory(), segment);
        fieldsreader = new FieldsReader(directory(), segment, fieldInfos);
        
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
}
