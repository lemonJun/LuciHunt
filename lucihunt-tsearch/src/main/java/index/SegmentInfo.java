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

import store.Directory;

/**
 * segments_x文件中  每一个段的信息 
 * 一个段信息  这是索引的组成单位
 * @author WangYazhou
 * @date  2017年2月27日 下午4:13:38
 * @see
 */
public final class SegmentInfo {
    //1 段名  也即counter值  
    public String name; // unique name in dir
    //2 段中包含的文档数 然而此文档数是包括已经删除，又没有optimize的文档的，因为在optimize之前，
    //Lucene的段中包含了所有被索引过的文档，而被删除的文档是保存在.del文件中的，在
    // 搜索的过程中，是先从段中读到了被删除的文档，然后再用.del中的标志，将这篇文档过滤掉
    public int docCount; // number of docs in seg

    //3 段的位置  这个是低版本  高版本是.del文件的版本号  
    public Directory dir; // where segment resides 

    //4 高版本还有其它信息  如每个段可以单独设置自的域和词向量

    public SegmentInfo(String name, int docCount, Directory dir) {
        this.name = name;
        this.docCount = docCount;
        this.dir = dir;
    }
}
