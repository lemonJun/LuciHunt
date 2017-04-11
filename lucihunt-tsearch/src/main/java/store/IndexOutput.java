package store;

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

/** 
 * 可随机访问的输出流
 * Abstract base class for output to a file in a Directory.  A random-access
 * output stream.  Used for all Lucene index output operations.
 * @see Directory
 * @see IndexInput
 */
public abstract class IndexOutput {

    /**
     * 写一个字节 
     * Writes a single byte.
     * @see IndexInput#readByte()
     */
    public abstract void writeByte(byte b) throws IOException;

    /** 
     * 写一个字节数组
     * Writes an array of bytes.
     * @param b the bytes to write
     * @param length the number of bytes to write
     * @see IndexInput#readBytes(byte[],int,int)
     */
    public abstract void writeBytes(byte[] b, int length) throws IOException;

    /** 
     * 从高到低写入一个整数   一个整数占四个字节
     * Writes an int as four bytes.
     * @see IndexInput#readInt()
     */
    public void writeInt(int i) throws IOException {
        writeByte((byte) (i >> 24));
        writeByte((byte) (i >> 16));
        writeByte((byte) (i >> 8));
        writeByte((byte) i);
    }

    /** 
     * 写入一个变长的整数，长度在1到5个字节，不支持负数
     * Writes an int in a variable-length format.  Writes between one and
     * five bytes.  Smaller values take fewer bytes.  Negative numbers are not
     * supported.
     * @see IndexInput#readVInt()
     */
    public void writeVInt(int i) throws IOException {
        while ((i & ~0x7F) != 0) {
            writeByte((byte) ((i & 0x7f) | 0x80));
            i >>>= 7;
        }
        writeByte((byte) i);
    }

    /**
     * 写入一个LONG数据  占8个字节
     *  Writes a long as eight bytes.
     * @see IndexInput#readLong()
     */
    public void writeLong(long i) throws IOException {
        writeInt((int) (i >> 32));
        writeInt((int) i);
    }

    /**
     * 变长的LONG
     * Writes an long in a variable-length format.  Writes between one and five
     * bytes.  Smaller values take fewer bytes.  Negative numbers are not
     * supported.
     * @see IndexInput#readVLong()
     */
    public void writeVLong(long i) throws IOException {
        while ((i & ~0x7F) != 0) {
            writeByte((byte) ((i & 0x7f) | 0x80));
            i >>>= 7;
        }
        writeByte((byte) i);
    }

    /** 
     * 写入一个字符串，先以变长的方式写入此字符串的长度，再写入字符
     * 读操作与此相反
     * Writes a string.
     * @see IndexInput#readString()
     */
    public void writeString(String s) throws IOException {
        int length = s.length();
        writeVInt(length);
        writeChars(s, 0, length);
    }

    /** 
     * 写入一个UTF-8编码的字符串
     * Writes a sequence of UTF-8 encoded characters from a string.
     * @param s the source of the characters
     * @param start the first character in the sequence
     * @param length the number of characters in the sequence
     * @see IndexInput#readChars(char[],int,int)
     */
    public void writeChars(String s, int start, int length) throws IOException {
        final int end = start + length;
        for (int i = start; i < end; i++) {
            final int code = (int) s.charAt(i);
            if (code >= 0x01 && code <= 0x7F)
                writeByte((byte) code);
            else if (((code >= 0x80) && (code <= 0x7FF)) || code == 0) {
                writeByte((byte) (0xC0 | (code >> 6)));
                writeByte((byte) (0x80 | (code & 0x3F)));
            } else {
                writeByte((byte) (0xE0 | (code >>> 12)));
                writeByte((byte) (0x80 | ((code >> 6) & 0x3F)));
                writeByte((byte) (0x80 | (code & 0x3F)));
            }
        }
    }

    //强制把缓存的数据写出
    public abstract void flush() throws IOException;

    /** Closes this stream to further operations. */
    public abstract void close() throws IOException;

    /**
     * 返回此文件当前写入的位置，
     * Returns the current position in this file, where the next write will
     * occur.
     * @see #seek(long)
     */
    public abstract long getFilePointer();

    /** 
     * 定位到文件的某个位置
     * Sets current position in this file, where the next write will occur.
     * @see #getFilePointer()
     */
    public abstract void seek(long pos) throws IOException;

    //文件的字节数
    public abstract long length() throws IOException;

}
