package index;

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

}
