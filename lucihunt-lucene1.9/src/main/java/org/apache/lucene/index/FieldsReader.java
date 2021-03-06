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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IndexInput;

/**
 * 根据原数据信息   获取域的信息
 * .fdt: 域数据文件
 * .fdx: 域索引文件
 * Class responsible for access to stored document fields.
 *
 * It uses &lt;segment&gt;.fdt and &lt;segment&gt;.fdx; files.
 *
 * @version $Id: FieldsReader.java 329524 2005-10-30 05:38:46Z yonik $
 */
final class FieldsReader {
    private FieldInfos fieldInfos;
    private IndexInput fieldsStream;
    private IndexInput indexStream;
    private int size;

    FieldsReader(Directory d, String segment, FieldInfos fn) throws IOException {
        fieldInfos = fn;

        fieldsStream = d.openInput(segment + ".fdt");//域数据文件
        indexStream = d.openInput(segment + ".fdx");

        size = (int) (indexStream.length() / 8);
    }

    final void close() throws IOException {
        fieldsStream.close();
        indexStream.close();
    }

    final int size() {
        return size;
    }

    /**
     * 读取域数据信息
     * 通过位置  查找一个文档的完整的域的信息
     * @param n
     * @return
     * @throws IOException
     */
    final Document doc(int n) throws IOException {
        indexStream.seek(n * 8L);//从索引中找到数据的起始公交车  问题是怎么把位置与文档关联起来的
        long position = indexStream.readLong();//读取偏移量的值
        fieldsStream.seek(position);///数据文件seek到此位置

        Document doc = new Document();//还原一个文档的内容
        int numFields = fieldsStream.readVInt();//多少个字段
        for (int i = 0; i < numFields; i++) {
            int fieldNumber = fieldsStream.readVInt();
            FieldInfo fi = fieldInfos.fieldInfo(fieldNumber);

            byte bits = fieldsStream.readByte();//状态

            boolean compressed = (bits & FieldsWriter.FIELD_IS_COMPRESSED) != 0;
            boolean tokenize = (bits & FieldsWriter.FIELD_IS_TOKENIZED) != 0;

            if ((bits & FieldsWriter.FIELD_IS_BINARY) != 0) {
                final byte[] b = new byte[fieldsStream.readVInt()];//域的长度
                fieldsStream.readBytes(b, 0, b.length);
                if (compressed)
                    doc.add(new Field(fi.name, uncompress(b), Field.Store.COMPRESS));
                else
                    doc.add(new Field(fi.name, b, Field.Store.YES));
            } else {
                Field.Index index;
                Field.Store store = Field.Store.YES;

                if (fi.isIndexed && tokenize)
                    index = Field.Index.TOKENIZED;
                else if (fi.isIndexed && !tokenize)
                    index = Field.Index.UN_TOKENIZED;
                else
                    index = Field.Index.NO;

                Field.TermVector termVector = null;
                if (fi.storeTermVector) {
                    if (fi.storeOffsetWithTermVector) {
                        if (fi.storePositionWithTermVector) {
                            termVector = Field.TermVector.WITH_POSITIONS_OFFSETS;
                        } else {
                            termVector = Field.TermVector.WITH_OFFSETS;
                        }
                    } else if (fi.storePositionWithTermVector) {
                        termVector = Field.TermVector.WITH_POSITIONS;
                    } else {
                        termVector = Field.TermVector.YES;
                    }
                } else {
                    termVector = Field.TermVector.NO;
                }

                if (compressed) {
                    store = Field.Store.COMPRESS;
                    final byte[] b = new byte[fieldsStream.readVInt()];
                    fieldsStream.readBytes(b, 0, b.length);
                    Field f = new Field(fi.name, // field name
                                    new String(uncompress(b), "UTF-8"), // uncompress the value and add as string
                                    store, index, termVector);
                    f.setOmitNorms(fi.omitNorms);
                    doc.add(f);
                } else {
                    Field f = new Field(fi.name, // name
                                    fieldsStream.readString(), // read value
                                    store, index, termVector);
                    f.setOmitNorms(fi.omitNorms);
                    doc.add(f);
                }
            }
        }

        return doc;
    }

    private final byte[] uncompress(final byte[] input) throws IOException {

        Inflater decompressor = new Inflater();
        decompressor.setInput(input);

        // Create an expandable byte array to hold the decompressed data
        ByteArrayOutputStream bos = new ByteArrayOutputStream(input.length);

        // Decompress the data
        byte[] buf = new byte[1024];
        while (!decompressor.finished()) {
            try {
                int count = decompressor.inflate(buf);
                bos.write(buf, 0, count);
            } catch (DataFormatException e) {
                // this will happen if the field is not compressed
                throw new IOException("field data are in wrong format: " + e.toString());
            }
        }

        decompressor.end();

        // Get the decompressed data
        return bos.toByteArray();
    }
}
