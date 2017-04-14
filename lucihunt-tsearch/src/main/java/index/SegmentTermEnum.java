package index;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import store.IndexInput;

/**
 * 基于段读取词的信息
 * 
 * 
 * @author WangYazhou
 * @date  2017年4月14日 上午11:46:10
 * @see
 */
public class SegmentTermEnum extends TermEnum implements Cloneable {

    private static final Logger logger = LoggerFactory.getLogger(SegmentTermEnum.class);

    private IndexInput input;
    FieldInfos fieldInfos;
    long size; //Term的个数
    long position = -1;

    private TermBuffer termBuffer = new TermBuffer();//记录当前读到的词
    private TermBuffer prevBuffer = new TermBuffer();//记录前一个词
    private TermBuffer scratch; // used for scanning

    private TermInfo terminfo = new TermInfo();

    private int format;//1
    private boolean isIndex = false;
    long indexPointer = 0;//这些一定要与写入时相同
    int indexInterval;//3
    int skipInterval;//4
    private int formatM1SkipInterval;//5

    //初始化各种参数配置  这些在tii  tis文件中都可以读到
    SegmentTermEnum(IndexInput i, FieldInfos fis, boolean isi) throws IOException {
        input = i;
        fieldInfos = fis;
        isIndex = isi;
        int firstint = input.readInt();
        if (firstint >= 0) {//
            // original-format file, without explicit format version number
            format = 0;
            size = firstint;

            // back-compatible settings
            indexInterval = 128;//为了加快对词的查找  也应用了类似跳跃表的结构
            skipInterval = Integer.MAX_VALUE; // switch off skipTo optimization

        } else {
            format = firstint;
            if (format < TermInfosWriter.FORMAT)
                throw new IOException("Unknown format version:" + format);

            size = input.readLong();//
            if (format == -1) {
                if (!isIndex) {
                    indexInterval = input.readInt();
                    formatM1SkipInterval = input.readInt();
                }
                // switch off skipTo optimization for file format prior to 1.4rc2 in order to avoid a bug in 
                // skipTo implementation of these versions
                skipInterval = Integer.MAX_VALUE;
            } else {
                indexInterval = input.readInt();
                skipInterval = input.readInt();
            }
        }
    }

    //移动指针到某个位置
    final void seek(long pointer, int p, Term t, TermInfo ti) throws IOException {
        input.seek(pointer);
        position = p;
        termBuffer.set(t);
        prevBuffer.reset();
        terminfo.set(ti);
    }

    @Override
    public boolean next() throws IOException {
        if (position++ >= size - 1) {//没有数据就不读了
            termBuffer.reset();
            return false;
        }

        logger.info("termbuffer:" + termBuffer.toString());
        prevBuffer.set(termBuffer);//
        termBuffer.read(input, fieldInfos);//重新读入 

        terminfo.docFreq = input.readInt();
        terminfo.freqPointer += input.readVLong();
        terminfo.proxPointer += input.readVLong();

        if (format == -1) {
            //  just read skipOffset in order to increment  file pointer; 
            // value is never used since skipTo is switched off
            if (!isIndex) {
                if (terminfo.docFreq > formatM1SkipInterval) {
                    terminfo.skipOffset = input.readVInt();
                }
            }
        } else {
            if (terminfo.docFreq >= skipInterval)
                terminfo.skipOffset = input.readVInt();
        }
        if (isIndex) {
            indexPointer += input.readVLong();
        }
        return true;
    }

    //scan操作 
    public void scanTo(Term term) throws IOException {
        if (scratch == null) {
            scratch = new TermBuffer();
        }
        scratch.set(term);
        while (scratch.compareTo(termBuffer) > 0 && next()) {
        }

    }

    @Override
    public Term term() {
        return termBuffer.toTerm();
    }

    @Override
    public int docFreq() {
        return terminfo.docFreq;
    }

    @Override
    public void close() throws IOException {
        input.clone();
    }

    final TermInfo termInfo() {
        return new TermInfo(terminfo);
    }

    final void termInfo(TermInfo ti) {
        ti.set(terminfo);
    }

    final long freqPointer() {
        return terminfo.freqPointer;
    }

    final long proxPointer() {
        return terminfo.proxPointer;
    }

    final Term prev() {
        return prevBuffer.toTerm();
    }

    protected Object clone() {
        SegmentTermEnum clone = null;
        try {
            clone = (SegmentTermEnum) super.clone();
        } catch (CloneNotSupportedException e) {
        }

        clone.input = (IndexInput) input.clone();
        clone.terminfo = new TermInfo(terminfo);

        clone.termBuffer = (TermBuffer) termBuffer.clone();
        clone.prevBuffer = (TermBuffer) prevBuffer.clone();
        clone.scratch = null;

        return clone;
    }

}
