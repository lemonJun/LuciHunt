package index;

/**
 * 
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
final class FieldInfo {
    String name;//域名
    boolean isIndexed;//是否索引
    int number;//在域元数据中的位置
    // true if term vector for this field should be stored
    boolean storeTermVector;//保存词微量
    boolean storePositionWithTermVector;//保存位置信息
    boolean storeOffsetWithTermVector;//保存偏移量
    boolean omitNorms; // omit norms associated with indexed fields

    FieldInfo(String na, boolean tk, int nu, boolean storeTermVector, boolean storePositionWithTermVector, boolean storeOffsetWithTermVector, boolean omitNorms) {
        name = na;
        isIndexed = tk;
        number = nu;
        this.storeTermVector = storeTermVector;
        this.storeOffsetWithTermVector = storeOffsetWithTermVector;
        this.storePositionWithTermVector = storePositionWithTermVector;
        this.omitNorms = omitNorms;
    }
}
