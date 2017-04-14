package index;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.locks.Lock;

import store.Directory;
import store.FSDirectory;

/**
 * 读索引 
 *
 * @author WangYazhou
 * @date  2017年4月14日 上午11:05:00
 * @see
 */
public abstract class IndexReader {

    public static final class FieldOption {
        private String option;

        private FieldOption() {
        }

        private FieldOption(String option) {
            this.option = option;
        }

        public String toString() {
            return this.option;
        }

        // all fields
        public static final FieldOption ALL = new FieldOption("ALL");
        // all indexed fields
        public static final FieldOption INDEXED = new FieldOption("INDEXED");
        // all fields which are not indexed
        public static final FieldOption UNINDEXED = new FieldOption("UNINDEXED");
        // all fields which are indexed with termvectors enables
        public static final FieldOption INDEXED_WITH_TERMVECTOR = new FieldOption("INDEXED_WITH_TERMVECTOR");
        // all fields which are indexed but don't have termvectors enabled
        public static final FieldOption INDEXED_NO_TERMVECTOR = new FieldOption("INDEXED_NO_TERMVECTOR");
        // all fields where termvectors are enabled. Please note that only standard termvector fields are returned
        public static final FieldOption TERMVECTOR = new FieldOption("TERMVECTOR");
        // all field with termvectors wiht positions enabled
        public static final FieldOption TERMVECTOR_WITH_POSITION = new FieldOption("TERMVECTOR_WITH_POSITION");
        // all fields where termvectors with offset position are set
        public static final FieldOption TERMVECTOR_WITH_OFFSET = new FieldOption("TERMVECTOR_WITH_OFFSET");
        // all fields where termvectors with offset and position values set
        public static final FieldOption TERMVECTOR_WITH_POSITION_OFFSET = new FieldOption("TERMVECTOR_WITH_POSITION_OFFSET");
    }

    /**
     * Constructor used if IndexReader is not owner of its directory. 
     * This is used for IndexReaders that are used within other IndexReaders that take care or locking directories.
     * 
     * @param directory Directory where IndexReader files reside.
     */
    protected IndexReader(Directory directory) {
        this.directory = directory;
    }

    /**
     * Constructor used if IndexReader is owner of its directory.
     * If IndexReader is owner of its directory, it locks its directory in case of write operations.
     * 
     * @param directory Directory where IndexReader files reside.
     * @param segmentInfos Used for write-l
     * @param closeDirectory
     */
    IndexReader(Directory directory, SegmentInfos segmentInfos, boolean closeDirectory) {
        init(directory, segmentInfos, closeDirectory, true);
    }

    void init(Directory directory, SegmentInfos segmentInfos, boolean closeDirectory, boolean directoryOwner) {
        this.directory = directory;
        this.segmentInfos = segmentInfos;
        this.directoryOwner = directoryOwner;
        this.closeDirectory = closeDirectory;
    }

    private Directory directory;
    private boolean directoryOwner;
    private boolean closeDirectory;

    private SegmentInfos segmentInfos;
    private Lock writeLock;
    private boolean stale;
    private boolean hasChanges;

    /** Returns an IndexReader reading the index in an FSDirectory in the named
     path. */
    public static IndexReader open(String path) throws IOException {
        return open(FSDirectory.getDirectory(path, false), true);
    }

    /** Returns an IndexReader reading the index in an FSDirectory in the named
     path. */
    public static IndexReader open(File path) throws IOException {
        return open(FSDirectory.getDirectory(path, false), true);
    }

    /** Returns an IndexReader reading the index in the given Directory. */
    public static IndexReader open(final Directory directory) throws IOException {
        return open(directory, false);
    }

    private static IndexReader open(final Directory directory, final boolean closeDirectory) throws IOException {
        synchronized (directory) { // in- & inter-process sync
            SegmentInfos infos = new SegmentInfos();
            infos.read(directory);
            if (infos.size() == 1) { // index is optimized
                return SegmentReader.get(infos, infos.info(0), closeDirectory);
            }
            //            IndexReader[] readers = new IndexReader[infos.size()];
            //            for (int i = 0; i < infos.size(); i++)
            //                readers[i] = SegmentReader.get(infos.info(i));
            //            return new MultiReader(directory, infos, closeDirectory, readers);
        }
        return null;
    }

    public Directory directory() {
        return directory;
    }

}
