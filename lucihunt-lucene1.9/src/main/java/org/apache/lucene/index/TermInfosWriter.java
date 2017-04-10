package org.apache.lucene.index;

/**
 * Copyright 2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.IOException;
import org.apache.lucene.store.IndexOutput;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.StringHelper;

/** 
 * 写入词典信息tis  
 * 1 初始化时写入版本  词的总数   内部间隔   跳跃间隔
 * 2 写入每一个Term的基本信息，这又包两部分：
 * 2.1 上下词开始不同的位置   词所占的长度（从差值算起） 词的内容(从差值算起) 词在元数据中的位置
 * 2.2 包含此词的文档频率   在频率表中的偏移量   在位置表中的偏移量   此词的倒排表的跳跃表在frq中的偏移量
 * 同时写顺便写入词典索引文件tii
 * 在indexInterval个间隔上写入索引词
 *  
 * 
 * This stores a monotonically increasing set of <Term, TermInfo> pairs in a
 * Directory.  A TermInfos can be written once, in order.  
 */

final class TermInfosWriter {
    /** The file format version, a negative number. */
    public static final int FORMAT = -2;

    private FieldInfos fieldInfos;
    private IndexOutput output;
    private Term lastTerm = new Term("", "");
    private TermInfo lastTi = new TermInfo();
    private long size = 0;

    // TODO: the default values for these two parameters should be settable from
    // IndexWriter.  However, once that's done, folks will start setting them to
    // ridiculous values and complaining that things don't work well, as with
    // mergeFactor.  So, let's wait until a number of folks find that alternate
    // values work better.  Note that both of these values are stored in the
    // segment, so that it's safe to change these w/o rebuilding all indexes.

    /** Expert: The fraction of terms in the "dictionary" which should be stored
     * in RAM.  Smaller values use more memory, but make searching slightly
     * faster, while larger values use less memory and make searching slightly
     * slower.  Searching is typically not dominated by dictionary lookup, so
     * tweaking this is rarely useful.*/
    int indexInterval = 128;

    /** Expert: The fraction of {@link TermDocs} entries stored in skip tables,
     * used to accellerate {@link TermDocs#skipTo(int)}.  Larger values result in
     * smaller indexes, greater acceleration, but fewer accelerable cases, while
     * smaller values result in bigger indexes, less acceleration and more
     * accelerable cases. More detailed experiments would be useful here. */
    int skipInterval = 16;

    private long lastIndexPointer = 0;
    private boolean isIndex = false;

    private TermInfosWriter other = null;//这个是用来写索引tii索引的

    //牛呗啊   这实时上是在同时写两个文件   
    TermInfosWriter(Directory directory, String segment, FieldInfos fis, int interval) throws IOException {
        initialize(directory, segment, fis, interval, false);
        other = new TermInfosWriter(directory, segment, fis, interval, true);
        other.other = this;
    }

    private TermInfosWriter(Directory directory, String segment, FieldInfos fis, int interval, boolean isIndex) throws IOException {
        initialize(directory, segment, fis, interval, isIndex);
    }

    //首先写入tii文件的是版本  Term总数 间隔
    private void initialize(Directory directory, String segment, FieldInfos fis, int interval, boolean isi) throws IOException {
        indexInterval = interval;
        fieldInfos = fis;
        isIndex = isi;
        output = directory.createOutput(segment + (isIndex ? ".tii" : ".tis"));
        output.writeInt(FORMAT); // 1write format 
        output.writeLong(0); // 2 leave space for size
        output.writeInt(indexInterval); //3  write indexInterval
        output.writeInt(skipInterval); //4  write skipInterval
    }

    /** Adds a new <Term, TermInfo> pair to the set.
    Term must be lexicographically greater than all previous Terms added.
    TermInfo pointers must be positive and greater than all previous.*/
    final void add(Term term, TermInfo ti) throws IOException {
        if (!isIndex && term.compareTo(lastTerm) <= 0)
            throw new IOException("term out of order");
        if (ti.freqPointer < lastTi.freqPointer)
            throw new IOException("freqPointer out of order");
        if (ti.proxPointer < lastTi.proxPointer)
            throw new IOException("proxPointer out of order");

        //isIndex=false是写入tis，只在一定跨度时才保存索引
        if (!isIndex && size % indexInterval == 0)
            other.add(lastTerm, lastTi); // add an index term

        writeTerm(term); // write term
        //写入频率 位置 等信息
        output.writeVInt(ti.docFreq); // write doc freq
        output.writeVLong(ti.freqPointer - lastTi.freqPointer); // write pointers
        output.writeVLong(ti.proxPointer - lastTi.proxPointer);

        if (ti.docFreq >= skipInterval) {
            output.writeVInt(ti.skipOffset);
        }

        //记录在tii中的位置   此时对应的是索引文件
        if (isIndex) {
            //写入在索引中的位置
            output.writeVLong(other.output.getFilePointer() - lastIndexPointer);
            //保存这次位置
            lastIndexPointer = other.output.getFilePointer(); // write pointer
        }

        lastTi.set(ti);
        size++;
    }

    //写入Term信息   按字典序写入
    private final void writeTerm(Term term) throws IOException {
        int start = StringHelper.stringDifference(lastTerm.text, term.text);
        int length = term.text.length() - start;

        output.writeVInt(start); // write shared prefix length
        output.writeVInt(length); // write delta length
        output.writeChars(term.text, start, length); // write delta chars

        //这里写入的是字段在域元数据信息中的位置    这样在查找时就出区分出是哪个字段了
        output.writeVInt(fieldInfos.fieldNumber(term.field)); // write field num

        lastTerm = term;
    }

    /** Called to complete TermInfos creation. */
    final void close() throws IOException {
        output.seek(4); // write size after format
        output.writeLong(size);
        output.close();

        if (!isIndex)
            other.close();
    }

}
