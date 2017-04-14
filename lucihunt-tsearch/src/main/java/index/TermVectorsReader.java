package index;

import java.io.IOException;

import store.Directory;
import store.IndexInput;

/**
 * 读取词和量信息
 *
 * @author WangYazhou
 * @date  2017年4月14日 下午2:45:37
 * @see
 */
public class TermVectorsReader {

    private IndexInput tvx;
    private IndexInput tvd;
    private IndexInput tvf;

    private int size;//文档个数

    TermVectorsReader(Directory d, String segment, FieldInfos fieldInfos) throws IOException {
        tvx = d.openInput(segment + TermVectorsWriter.TVX_EXTENSION);
        tvd = d.openInput(segment + TermVectorsWriter.TVD_EXTENSION);
        tvf = d.openInput(segment + TermVectorsWriter.TVF_EXTENSION);

        size = (int) (tvx.length() / 8);
    }

    void close() throws IOException {

    }

}
