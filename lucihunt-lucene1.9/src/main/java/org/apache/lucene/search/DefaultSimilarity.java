package org.apache.lucene.search;

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
 * 对similarity的默认实现  
 * 
 * Expert: Default scoring implementation. 
 */
@SuppressWarnings("serial")
public class DefaultSimilarity extends Similarity {
    /**
     * 
     *  3.0版本以后叫 fieldNorm 
     *  Implemented as <code>1/sqrt(numTerms)</code>.
     *  
     */
    public float lengthNorm(String fieldName, int numTerms) {
        return (float) (1.0 / Math.sqrt(numTerms));
    }

    /** 
     * 这个对应的是query  而在一次查询中query是固定的
     * 所以这对排序没什么影响
     * Implemented as <code>1/sqrt(sumOfSquaredWeights)</code>. 
     */
    public float queryNorm(float sumOfSquaredWeights) {
        return (float) (1.0 / Math.sqrt(sumOfSquaredWeights));
    }

    /**
     *  词频  平方根
     *  Implemented as <code>sqrt(freq)</code>. */
    public float tf(float freq) {
        return (float) Math.sqrt(freq);
    }

    /**
     * 
     *  Implemented as <code>1 / (distance + 1)</code>. 
     */
    public float sloppyFreq(int distance) {
        return 1.0f / (distance + 1);
    }

    /**
     * 逆文档频率
     *  Implemented as <code>log(numDocs/(docFreq+1)) + 1</code>. 
     */
    public float idf(int docFreq, int numDocs) {
        return (float) (Math.log(numDocs / (double) (docFreq + 1)) + 1.0);
    }

    /**
     * 符合查询term / 总的查询term
     *  
     *  Implemented as <code>overlap / maxOverlap</code>. 
     *  
     */
    public float coord(int overlap, int maxOverlap) {
        return overlap / (float) maxOverlap;
    }
}
