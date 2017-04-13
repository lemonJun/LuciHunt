package index;

import java.io.IOException;

import store.Directory;
import store.IndexOutput;
import util.StringHelper;

/**
 * 写入词典倒排表
 *
 * @author WangYazhou
 * @date  2017年4月13日 下午1:48:42
 * @see
 */
public class TermInfosWriter {

    private int indexInterval = 128;
    private FieldInfos fieldInfos;
    private IndexOutput tii;
    private IndexOutput tis;
    private boolean isindex;
    private final int FORMAT = -2;
    private int skipInterval = 16;

    TermInfosWriter(Directory directory, String segment, FieldInfos fis, int interval) throws IOException {
        initialize(directory, segment, fis, interval, false);
    }

    private void initialize(Directory directory, String segment, FieldInfos fis, int interval, boolean isi) throws IOException {
        indexInterval = interval;
        fieldInfos = fis;
        isindex = isi;
        tii = directory.createOutput(segment + ".tii");
        tii.writeInt(FORMAT); // 1write format 
        tii.writeLong(0); // 2 leave space for size
        tii.writeInt(indexInterval); //3  write indexInterval
        tii.writeInt(skipInterval); //4  write skipInterval
        tis = directory.createOutput(segment + ".tis");
        tis.writeInt(FORMAT); // 1write format 
        tis.writeLong(0); // 2 leave space for size
        tis.writeInt(indexInterval); //3  write indexInterval
        tis.writeInt(skipInterval); //4  write skipInterval
    }

    private Term lastterm = new Term("", "");
    private TermInfo lastti = new TermInfo();
    private long size;
    private long lastindexpointer = 0L;

    public void add(Term term, TermInfo ti) throws IOException {
        if (term.compareTo(lastterm) <= 0) {
            throw new IOException("term out of order");
        }
        if (ti.freqPointer < lastti.freqPointer) {
            throw new IOException("freq out of order ");
        }
        if (ti.proxPointer < lastti.proxPointer) {
            throw new IOException("prox out of order ");
        }
        if (size % indexInterval == 0) {
            addtii(lastterm, lastti);
        }

        writeTerm(tis, term);
        tis.writeVInt(ti.docFreq);
        tis.writeVLong(ti.freqPointer - lastti.freqPointer);
        tis.writeVLong(ti.proxPointer - lastti.proxPointer);
        if (ti.docFreq >= skipInterval) {
            tis.writeInt(skipInterval);
        }

        lastti.set(ti);
        size++;

    }

    private void writeTerm(IndexOutput output, Term term) throws IOException {
        int start = StringHelper.stringDifference(lastterm.text(), term.text());
        int length = term.text().length() - start;
        output.writeInt(start);//写入开始偏移量
        output.writeInt(length);//写入偏移后的长度
        output.writeChars(term.text(), start, length);
        output.writeInt(fieldInfos.fieldNumber(term.field()));//写入域在元数据中的次序
    }

    public void addtii(Term term, TermInfo ti) throws IOException {
        if (!isindex && term.compareTo(lastterm) <= 0) {
            throw new IOException("term out of order");
        }
        if (ti.freqPointer < lastti.freqPointer) {
            throw new IOException("freq out of order ");
        }
        if (ti.proxPointer < lastti.proxPointer) {
            throw new IOException("prox out of order ");
        }

        writeTerm(tii, term);
        tii.writeVInt(ti.docFreq);
        tii.writeVLong(ti.freqPointer - lastti.freqPointer);
        tii.writeVLong(ti.proxPointer - lastti.proxPointer);
        if (ti.docFreq >= skipInterval) {
            tii.writeInt(skipInterval);
        }

        long tislong = tis.getFilePointer() - lastindexpointer;
        tii.writeLong(tislong);
        lastindexpointer = tis.getFilePointer();
    }

    public void close() throws IOException {
        tii.seek(4);
        tii.writeLong(size);
        tii.close();
        tis.seek(4);
        tis.writeLong(size);
        tis.close();
    }

}
