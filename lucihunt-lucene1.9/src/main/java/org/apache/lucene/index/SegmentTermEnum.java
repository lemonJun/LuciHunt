package org.apache.lucene.index;

import java.io.IOException;
import org.apache.lucene.store.IndexInput;

/**
 * 这是一个Term的枚举类，通过这个类可以获取此段下的所有的词
 * 按1->5标记的是读的顺序
 * 词典文件.tis   词典索引文件.tii 共用的是同一个类   
 * 
 * @author WangYazhou
 * @date  2017年2月27日 下午2:58:11
 * @see
 */
final class SegmentTermEnum extends TermEnum implements Cloneable {
    private IndexInput input;
    FieldInfos fieldInfos;
    long size;//2 
    long position = -1;

    private TermBuffer termBuffer = new TermBuffer();
    private TermBuffer prevBuffer = new TermBuffer();
    private TermBuffer scratch; // used for scanning

    private TermInfo termInfo = new TermInfo();

    private int format;//1
    private boolean isIndex = false;
    long indexPointer = 0;
    int indexInterval;//3
    int skipInterval;//4
    private int formatM1SkipInterval;//5

    //
    SegmentTermEnum(IndexInput i, FieldInfos fis, boolean isi) throws IOException {
        input = i;
        fieldInfos = fis;
        isIndex = isi;

        int firstInt = input.readInt();//表示有多少个词
        if (firstInt >= 0) {//
            // original-format file, without explicit format version number
            format = 0;
            size = firstInt;

            // back-compatible settings
            indexInterval = 128;//为了加快对词的查找  也应用了类似跳跃表的结构
            skipInterval = Integer.MAX_VALUE; // switch off skipTo optimization

        } else {
            // we have a format version number
            format = firstInt;

            // check that it is a format we can understand
            if (format < TermInfosWriter.FORMAT)
                throw new IOException("Unknown format version:" + format);

            size = input.readLong(); // read the size

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

    protected Object clone() {
        SegmentTermEnum clone = null;
        try {
            clone = (SegmentTermEnum) super.clone();
        } catch (CloneNotSupportedException e) {
        }

        clone.input = (IndexInput) input.clone();
        clone.termInfo = new TermInfo(termInfo);

        clone.termBuffer = (TermBuffer) termBuffer.clone();
        clone.prevBuffer = (TermBuffer) prevBuffer.clone();
        clone.scratch = null;

        return clone;
    }

    //初始化位置
    final void seek(long pointer, int p, Term t, TermInfo ti) throws IOException {
        input.seek(pointer);
        position = p;
        termBuffer.set(t);
        prevBuffer.reset();
        termInfo.set(ti);
    }

    /**
     * 遍历数据
     * Increments the enumeration to the next element.  True if one exists.
     */
    public final boolean next() throws IOException {
        if (position++ >= size - 1) {//没有数据就不读了
            termBuffer.reset();
            return false;
        }

        prevBuffer.set(termBuffer);
        termBuffer.read(input, fieldInfos);

        termInfo.docFreq = input.readVInt(); // 1 read doc freq
        termInfo.freqPointer += input.readVLong(); // read freq pointer
        termInfo.proxPointer += input.readVLong(); // read prox pointer

        if (format == -1) {
            //  just read skipOffset in order to increment  file pointer; 
            // value is never used since skipTo is switched off
            if (!isIndex) {
                if (termInfo.docFreq > formatM1SkipInterval) {
                    termInfo.skipOffset = input.readVInt();
                }
            }
        } else {
            if (termInfo.docFreq >= skipInterval)
                termInfo.skipOffset = input.readVInt();
        }

        if (isIndex)
            indexPointer += input.readVLong(); // read index pointer

        return true;
    }

    /** Optimized scan, without allocating new terms. */
    final void scanTo(Term term) throws IOException {
        if (scratch == null)
            scratch = new TermBuffer();
        scratch.set(term);
        while (scratch.compareTo(termBuffer) > 0 && next()) {
        }
    }

    /** Returns the current Term in the enumeration.
     Initially invalid, valid after next() called for the first time.*/
    public final Term term() {
        return termBuffer.toTerm();
    }

    /** Returns the previous Term enumerated. Initially null.*/
    final Term prev() {
        return prevBuffer.toTerm();
    }

    /** Returns the current TermInfo in the enumeration.
     Initially invalid, valid after next() called for the first time.*/
    final TermInfo termInfo() {
        return new TermInfo(termInfo);
    }

    /** Sets the argument to the current TermInfo in the enumeration.
     Initially invalid, valid after next() called for the first time.*/
    final void termInfo(TermInfo ti) {
        ti.set(termInfo);
    }

    /** Returns the docFreq from the current TermInfo in the enumeration.
     Initially invalid, valid after next() called for the first time.*/
    public final int docFreq() {
        return termInfo.docFreq;
    }

    /* Returns the freqPointer from the current TermInfo in the enumeration.
    Initially invalid, valid after next() called for the first time.*/
    final long freqPointer() {
        return termInfo.freqPointer;
    }

    /* Returns the proxPointer from the current TermInfo in the enumeration.
    Initially invalid, valid after next() called for the first time.*/
    final long proxPointer() {
        return termInfo.proxPointer;
    }

    /** Closes the enumeration to further activity, freeing resources. */
    public final void close() throws IOException {
        input.close();
    }
}
