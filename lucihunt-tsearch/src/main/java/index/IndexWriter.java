package index;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.takin.emmet.file.FileLock;

import analysis.Analyzer;
import document.Document;
import search.Similarity;
import store.Directory;
import store.FSDirectory;

public class IndexWriter {

    private static final Logger logger = LoggerFactory.getLogger(IndexWriter.class);

    public static final String WRITE_LOCK_NAME = "write.lock";
    public static final String COMMIT_LOCK_NAME = "commit.lock";

    private Directory directory; // where this index resides
    private Analyzer analyzer; // how to analyze text

    private Similarity similarity = Similarity.getDefault(); // how to normalize

    private SegmentInfos segmentInfos = new SegmentInfos(); // the segments

    public final static int DEFAULT_MAX_BUFFERED_DOCS = 10;

    public final static int DEFAULT_MAX_MERGE_DOCS = Integer.MAX_VALUE;

    public final static int DEFAULT_MERGE_FACTOR = 10;

    private FileLock writeLock;

    public final static int DEFAULT_MAX_FIELD_LENGTH = 10000;
    public final static int DEFAULT_TERM_INDEX_INTERVAL = 128;

    public IndexWriter(String path, Analyzer a, boolean create) throws IOException {
        this(FSDirectory.getDirectory(path, create), a, create, true);
    }

    public IndexWriter(File path, Analyzer a, boolean create) throws IOException {
        this(FSDirectory.getDirectory(path, create), a, create, true);
    }

    public IndexWriter(Directory d, Analyzer a, boolean create) throws IOException {
        this(d, a, create, false);
    }

    private IndexWriter(Directory d, Analyzer a, final boolean create, boolean closeDir) throws IOException {
        directory = d;
        analyzer = a;

        FileLock writeLock = directory.makeLock(IndexWriter.WRITE_LOCK_NAME);
        this.writeLock = writeLock; // save it
        FileLock commitLock = directory.makeLock(IndexWriter.COMMIT_LOCK_NAME);
        commitLock.tryLock();
        if (create) {
            segmentInfos.write(directory);
        } else {
            segmentInfos.read(directory);
        }
    }

    public synchronized int docCount() {
        int count = 0;
        for (int i = 0; i < segmentInfos.size(); i++) {
            SegmentInfo si = segmentInfos.info(i);
            count += si.docCount;
        }
        return count;
    }

    public void addDocument(Document doc) throws IOException {
        addDocument(doc, analyzer);
    }

    public void addDocument(Document doc, Analyzer analyzer) throws IOException {
        DocumentWriter dw = new DocumentWriter(directory, analyzer, this);
        String segmentName = newSegmentName();
        dw.addDocument(segmentName, doc);
        //        synchronized (this) {
        segmentInfos.addElement(new SegmentInfo(segmentName, 1, directory));
        maybeMergeSegments();
        //        }
    }

    private final void maybeMergeSegments() throws IOException {
        long targetMergeDocs = DEFAULT_MAX_BUFFERED_DOCS;
        while (targetMergeDocs <= DEFAULT_MAX_MERGE_DOCS) {
            // find segments smaller than current target size
            int minSegment = segmentInfos.size();
            int mergeDocs = 0;
            while (--minSegment >= 0) {
                SegmentInfo si = segmentInfos.info(minSegment);
                if (si.docCount >= targetMergeDocs)
                    break;
                mergeDocs += si.docCount;
            }

            if (mergeDocs >= targetMergeDocs) // found a merge to do
                mergeSegments(minSegment + 1);
            else
                break;

            targetMergeDocs *= DEFAULT_MERGE_FACTOR; // increase target size
        }
    }

    //段名
    private final void mergeSegments(int minSegment) throws IOException {
        mergeSegments(minSegment, segmentInfos.size());
    }

    private final void mergeSegments(int minSegment, int end) throws IOException {
        String newsegname = newSegmentName();
        logger.info("segment merger ...");
    }

    private final synchronized String newSegmentName() {
        return "_" + Integer.toString(segmentInfos.counter++, Character.MAX_RADIX);
    }

    public synchronized void close() throws IOException {
        //        flushRamSegments();
        //        if (writeLock != null) {
        //            writeLock.release(); // release write lock
        //            writeLock = null;
        //        }
        directory.close();
        //        segmentInfos.clone();
    }

    public Directory getDirectory() {
        return directory;
    }

    public void setDirectory(Directory directory) {
        this.directory = directory;
    }

    public Analyzer getAnalyzer() {
        return analyzer;
    }

    public void setAnalyzer(Analyzer analyzer) {
        this.analyzer = analyzer;
    }

    public Similarity getSimilarity() {
        return similarity;
    }

    public void setSimilarity(Similarity similarity) {
        this.similarity = similarity;
    }

    public SegmentInfos getSegmentInfos() {
        return segmentInfos;
    }

    public void setSegmentInfos(SegmentInfos segmentInfos) {
        this.segmentInfos = segmentInfos;
    }

}
