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
 * 一个文件的输入流  支持随机访问  主要用于各种读取操作
 * Abstract base class for input from a file in a {@link Directory}.  A
 * random-access input stream.  Used for all Lucene index input operations.
 * @see Directory
 */
public abstract class IndexInput implements Cloneable {
    private char[] chars; // used by readString()

    /** Reads and returns a single byte.
     * @see IndexOutput#writeByte(byte)
     */
    public abstract byte readByte() throws IOException;

    /** 
     * Reads a specified number of bytes into an array at the specified offset.
     * @param b the array to read bytes into
     * @param offset the offset in the array to start storing bytes
     * @param len the number of bytes to read
     * @see IndexOutput#writeBytes(byte[],int)
     */
    public abstract void readBytes(byte[] b, int offset, int len) throws IOException;

    /** 读取四个字节  返回一个整数
     * @see IndexOutput#writeInt(int)
     */
    public int readInt() throws IOException {
        return ((readByte() & 0xFF) << 24) | ((readByte() & 0xFF) << 16) | ((readByte() & 0xFF) << 8) | (readByte() & 0xFF);
    }

    /** 读取一个变长的整数.
     *  读取1到5个字节   越小的数  占用的字节数越小   不支持负数
     * @see IndexOutput#writeVInt(int)
     */
    public int readVInt() throws IOException {
        byte b = readByte();
        int i = b & 0x7F;
        for (int shift = 7; (b & 0x80) != 0; shift += 7) {
            b = readByte();
            i |= (b & 0x7F) << shift;
        }
        return i;
    }

    /**
     *  读取8个字节 并返回一个LONG
     * @see IndexOutput#writeLong(long)
     */
    public long readLong() throws IOException {
        return (((long) readInt()) << 32) | (readInt() & 0xFFFFFFFFL);
    }

    /**
     * 读取一个变长的long.  读取1到9个字节  越小的数占用的字节越少 . 不支持负数
     * supported. */
    public long readVLong() throws IOException {
        byte b = readByte();
        long i = b & 0x7F;
        for (int shift = 7; (b & 0x80) != 0; shift += 7) {
            b = readByte();
            i |= (b & 0x7FL) << shift;
        }
        return i;
    }

    /** 
     * 根据长度读取一个字符串
     * @see IndexOutput#writeString(String)
     */
    public String readString() throws IOException {
        int length = readVInt();
        if (chars == null || length > chars.length)
            chars = new char[length];
        readChars(chars, 0, length);
        return new String(chars, 0, length);
    }

    /** 
     * 把UTF-8编码的字符读进一个数组
     * @param buffer the array to read characters into
     * @param start the offset in the array to start storing characters
     * @param length the number of characters to read
     * @see IndexOutput#writeChars(String,int,int)
     */
    public void readChars(char[] buffer, int start, int length) throws IOException {
        final int end = start + length;
        for (int i = start; i < end; i++) {
            byte b = readByte();
            if ((b & 0x80) == 0)
                buffer[i] = (char) (b & 0x7F);
            else if ((b & 0xE0) != 0xE0) {
                buffer[i] = (char) (((b & 0x1F) << 6) | (readByte() & 0x3F));
            } else
                buffer[i] = (char) (((b & 0x0F) << 12) | ((readByte() & 0x3F) << 6) | (readByte() & 0x3F));
        }
    }

    /** Closes the stream to futher operations. */
    public abstract void close() throws IOException;

    /**
     * 返回文件当前读到的位置 
     * Returns the current position in this file, where the next read will
     * occur.
     * @see #seek(long)
     */
    public abstract long getFilePointer();

    /** 
     * 寻址到当前正在读的文件的位置
     * Sets current position in this file, where the next read will occur.
     * @see #getFilePointer()
     */
    public abstract void seek(long pos) throws IOException;

    /**  
     * 返回文件的字节数 
     */
    public abstract long length();

    /** 
     * 复制一个流
     *
     * <p>Clones of a stream access the same data, and are positioned at the same
     * point as the stream they were cloned from.
     *
     * <p>Expert: Subclasses must ensure that clones may be positioned at
     * different points in the input from each other and from the stream they
     * were cloned from.
     */
    public Object clone() {
        IndexInput clone = null;
        try {
            clone = (IndexInput) super.clone();
        } catch (CloneNotSupportedException e) {
        }

        clone.chars = null;

        return clone;
    }

}
