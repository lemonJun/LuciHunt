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

import com.takin.emmet.file.FileLock;

/** 
 * A Directory is a flat list of files.  Files may be written once, when they
 * are created.  Once a file is created it may only be opened for read, or
 * deleted.  Random access is permitted both when reading and writing.
 *
 * <p> Java's i/o APIs not used directly, but rather all i/o is
 * through this API.  This permits things such as: <ul>
 * <li> implementation of RAM-based indices;
 * <li> implementation indices stored in a database, via JDBC;
 * <li> implementation of an index as a single file;
 * </ul>
 * 
 * @author Doug Cutting
 */
public abstract class Directory {
    //返回一堆文件列表 
    public abstract String[] list() throws IOException;

    //判断一个文件是否存在
    public abstract boolean fileExists(String name) throws IOException;

    //返回文件的修改时间
    public abstract long fileModified(String name) throws IOException;

    //修改一个文件的时间
    public abstract void touchFile(String name) throws IOException;

    //删除一个文件
    public abstract void deleteFile(String name) throws IOException;

    //重命名  此步应该是原子的
    public abstract void renameFile(String from, String to) throws IOException;

    //返回文件大小
    public abstract long fileLength(String name) throws IOException;

    /** @deprecated use {@link #createOutput(String)} */
    public OutputStream createFile(String name) throws IOException {
        return (OutputStream) createOutput(name);
    }

    //创建一个新的 空的文件
    public IndexOutput createOutput(String name) throws IOException {
        // default implementation for back compatibility
        // this method should be abstract
        return (IndexOutput) createFile(name);
    }

    /** @deprecated use {@link #openInput(String)} */
    public InputStream openFile(String name) throws IOException {
        return (InputStream) openInput(name);
    }

    //返回一个文件的输入流
    public IndexInput openInput(String name) throws IOException {
        // default implementation for back compatibility
        // this method should be abstract
        return (IndexInput) openFile(name);
    }
    
    //文件锁
    public abstract FileLock makeLock(String name);

    //关闭此存储
    public abstract void close() throws IOException;
}
