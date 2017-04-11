package index;

import java.io.File;
import java.io.IOException;

import com.takin.emmet.file.FileLock;

import analysis.Analyzer;
import document.Document;
import search.Similarity;
import store.Directory;
import store.FSDirectory;

public class IndexWriter {

    public static final String WRITE_LOCK_NAME = "write.lock";
    public static final String COMMIT_LOCK_NAME = "commit.lock";

    private Directory directory; // where this index resides
    private Analyzer analyzer; // how to analyze text

    private Similarity similarity = Similarity.getDefault(); // how to normalize

    private SegmentInfos segmentInfos = new SegmentInfos(); // the segments

    private FileLock writeLock;

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
        //        DocumentWriter dw = new DocumentWriter(ramDirectory, analyzer, this);
        //        String segmentName = newSegmentName();
        //        dw.addDocument(segmentName, doc);
        //        synchronized (this) {
        //            segmentInfos.addElement(new SegmentInfo(segmentName, 1, ramDirectory));
        //            maybeMergeSegments();
        //        }
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
