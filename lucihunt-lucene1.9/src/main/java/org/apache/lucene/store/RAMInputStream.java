package org.apache.lucene.store;

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
 * A memory-resident {@link IndexInput} implementation.
 *
 * @version $Id: RAMInputStream.java 150537 2004-09-28 20:45:26Z cutting $
 */

class RAMInputStream extends BufferedIndexInput implements Cloneable {
    private RAMFile file;
    private int pointer = 0;
    private long length;

    public RAMInputStream(RAMFile f) {
        file = f;
        length = file.length;
    }

    public void readInternal(byte[] dest, int destOffset, int len) {
        int remainder = len;
        int start = pointer;
        while (remainder != 0) {
            int bufferNumber = start / BUFFER_SIZE;
            int bufferOffset = start % BUFFER_SIZE;
            int bytesInBuffer = BUFFER_SIZE - bufferOffset;
            int bytesToCopy = bytesInBuffer >= remainder ? remainder : bytesInBuffer;
            byte[] buffer = (byte[]) file.buffers.elementAt(bufferNumber);
            System.arraycopy(buffer, bufferOffset, dest, destOffset, bytesToCopy);
            destOffset += bytesToCopy;
            start += bytesToCopy;
            remainder -= bytesToCopy;
        }
        pointer += len;
    }

    public void close() {
    }

    public void seekInternal(long pos) {
        pointer = (int) pos;
    }

    public long length() {
        return length;
    }

}
