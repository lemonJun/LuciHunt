package index;

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

/** 
 * 词的相关信息   
 * 在terminfowriter中写入 
 * 
 * A TermInfo is the record of information stored for a term
 */
final class TermInfo {
    /** The number of documents which contain the term. */
    int docFreq = 0;//1  文档频率

    long freqPointer = 0;//2  频率倒排表的偏移 
    long proxPointer = 0;//3  位置倒排表的偏移
    int skipOffset;//4  跳过多少偏移量

    TermInfo() {
    }

    TermInfo(int df, long fp, long pp) {
        docFreq = df;
        freqPointer = fp;
        proxPointer = pp;
    }

    TermInfo(TermInfo ti) {
        docFreq = ti.docFreq;
        freqPointer = ti.freqPointer;
        proxPointer = ti.proxPointer;
        skipOffset = ti.skipOffset;
    }

    final void set(int docFreq, long freqPointer, long proxPointer, int skipOffset) {
        this.docFreq = docFreq;
        this.freqPointer = freqPointer;
        this.proxPointer = proxPointer;
        this.skipOffset = skipOffset;
    }

    final void set(TermInfo ti) {
        docFreq = ti.docFreq;
        freqPointer = ti.freqPointer;
        proxPointer = ti.proxPointer;
        skipOffset = ti.skipOffset;
    }
}
