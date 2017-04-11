package document;

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

import java.io.Reader;
import java.io.Serializable;

import util.Parameter;

/**
  A field is a section of a Document.  Each field has two parts, a name and a
  value.  Values may be free text, provided as a String or as a Reader, or they
  may be atomic keywords, which are not further processed.  Such keywords may
  be used to represent dates, urls, etc.  Fields are optionally stored in the
  index, so that they may be returned with hits on the document.
  */

public final class Field implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private String name = "body";

    // the one and only data object for all different kind of field values
    private Object fieldsData = null;

    private boolean storeTermVector = false;
    private boolean storeOffsetWithTermVector = false;
    private boolean storePositionWithTermVector = false;
    private boolean omitNorms = false;
    private boolean isStored = false;
    private boolean isIndexed = true;
    private boolean isTokenized = true;
    private boolean isBinary = false;
    private boolean isCompressed = false;

    private float boost = 1.0f;

    public static final class Store extends Parameter implements Serializable {

        private Store(String name) {
            super(name);
        }

        /** Store the original field value in the index in a compressed form. This is
         * useful for long documents and for binary valued fields.
         */
        public static final Store COMPRESS = new Store("COMPRESS");
        //存储原始值
        public static final Store YES = new Store("YES");

        /** Do not store the field value in the index. */
        public static final Store NO = new Store("NO");
    }

    /** Specifies whether and how a field should be indexed. */
    public static final class Index extends Parameter implements Serializable {

        private Index(String name) {
            super(name);
        }

        //不索引 不能搜索 
        public static final Index NO = new Index("NO");

        //索引   分词
        public static final Index TOKENIZED = new Index("TOKENIZED");

        //索引不分词  比如商品ID
        public static final Index UN_TOKENIZED = new Index("UN_TOKENIZED");

        /**
         * 索引不分词   不记录标准化因子
         * Index the field's value without an Analyzer, and disable
         * the storing of norms.  No norms means that index-time boosting
         * and field length normalization will be disabled.  The benefit is
         * less memory usage as norms take up one byte per indexed field
         * for every document in the index.
         */
        public static final Index NO_NORMS = new Index("NO_NORMS");

    }

    //是否记录到向量信息  如果不记录  则无法在正排表中找到
    /** Specifies whether and how a field should have term vectors. */
    public static final class TermVector extends Parameter implements Serializable {

        private TermVector(String name) {
            super(name);
        }

        /** Do not store term vectors. 
         */
        public static final TermVector NO = new TermVector("NO");

        /** Store the term vectors of each document. A term vector is a list
         * of the document's terms and their number of occurences in that document. */
        public static final TermVector YES = new TermVector("YES");

        /**
         * Store the term vector + token position information
         * 
         * @see #YES
         */
        public static final TermVector WITH_POSITIONS = new TermVector("WITH_POSITIONS");

        /**
         * Store the term vector + Token offset information
         * 
         * @see #YES
         */
        public static final TermVector WITH_OFFSETS = new TermVector("WITH_OFFSETS");

        /**
         * Store the term vector + Token position and offset information
         * 
         * @see #YES
         * @see #WITH_POSITIONS
         * @see #WITH_OFFSETS
         */
        public static final TermVector WITH_POSITIONS_OFFSETS = new TermVector("WITH_POSITIONS_OFFSETS");
    }

    public void setBoost(float boost) {
        this.boost = boost;
    }

    public float getBoost() {
        return boost;
    }

    public String name() {
        return name;
    }

    public String stringValue() {
        return fieldsData instanceof String ? (String) fieldsData : null;
    }

    /** The value of the field as a Reader, or null.  If null, the String value
     * or binary value is  used.  Exactly one of stringValue(), readerValue(),
     * and binaryValue() must be set. */
    public Reader readerValue() {
        return fieldsData instanceof Reader ? (Reader) fieldsData : null;
    }

    /** The value of the field in Binary, or null.  If null, the Reader or
     * String value is used.  Exactly one of stringValue(), readerValue() and
     * binaryValue() must be set. */
    public byte[] binaryValue() {
        return fieldsData instanceof byte[] ? (byte[]) fieldsData : null;
    }

    public Field(String name, String value, Store store, Index index) {
        this(name, value, store, index, TermVector.NO);
    }

    ///
    public Field(String name, String value, Store store, Index index, TermVector termVector) {
        if (name == null)
            throw new NullPointerException("name cannot be null");
        if (value == null)
            throw new NullPointerException("value cannot be null");
        if (index == Index.NO && store == Store.NO)
            throw new IllegalArgumentException("it doesn't make sense to have a field that " + "is neither indexed nor stored");
        if (index == Index.NO && termVector != TermVector.NO)
            throw new IllegalArgumentException("cannot store term vector information " + "for a field that is not indexed");

        this.name = name.intern(); // field names are interned
        this.fieldsData = value;

        if (store == Store.YES) {
            this.isStored = true;
            this.isCompressed = false;
        } else if (store == Store.COMPRESS) {
            this.isStored = true;
            this.isCompressed = true;
        } else if (store == Store.NO) {
            this.isStored = false;
            this.isCompressed = false;
        } else
            throw new IllegalArgumentException("unknown store parameter " + store);

        if (index == Index.NO) {
            this.isIndexed = false;
            this.isTokenized = false;
        } else if (index == Index.TOKENIZED) {
            this.isIndexed = true;
            this.isTokenized = true;
        } else if (index == Index.UN_TOKENIZED) {
            this.isIndexed = true;
            this.isTokenized = false;
        } else if (index == Index.NO_NORMS) {
            this.isIndexed = true;
            this.isTokenized = false;
            this.omitNorms = true;
        } else {
            throw new IllegalArgumentException("unknown index parameter " + index);
        }

        this.isBinary = false;

        setStoreTermVector(termVector);
    }

    public Field(String name, Reader reader) {
        this(name, reader, TermVector.NO);
    }

    public Field(String name, Reader reader, TermVector termVector) {
        if (name == null)
            throw new NullPointerException("name cannot be null");
        if (reader == null)
            throw new NullPointerException("reader cannot be null");

        this.name = name.intern(); // field names are interned
        this.fieldsData = reader;

        this.isStored = false;
        this.isCompressed = false;

        this.isIndexed = true;
        this.isTokenized = true;

        this.isBinary = false;

        setStoreTermVector(termVector);
    }

    public Field(String name, byte[] value, Store store) {
        if (name == null)
            throw new IllegalArgumentException("name cannot be null");
        if (value == null)
            throw new IllegalArgumentException("value cannot be null");

        this.name = name.intern();
        this.fieldsData = value;

        if (store == Store.YES) {
            this.isStored = true;
            this.isCompressed = false;
        } else if (store == Store.COMPRESS) {
            this.isStored = true;
            this.isCompressed = true;
        } else if (store == Store.NO)
            throw new IllegalArgumentException("binary values can't be unstored");
        else
            throw new IllegalArgumentException("unknown store parameter " + store);

        this.isIndexed = false;
        this.isTokenized = false;

        this.isBinary = true;

        setStoreTermVector(TermVector.NO);
    }

    private void setStoreTermVector(TermVector termVector) {
        if (termVector == TermVector.NO) {
            this.storeTermVector = false;
            this.storePositionWithTermVector = false;
            this.storeOffsetWithTermVector = false;
        } else if (termVector == TermVector.YES) {
            this.storeTermVector = true;
            this.storePositionWithTermVector = false;
            this.storeOffsetWithTermVector = false;
        } else if (termVector == TermVector.WITH_POSITIONS) {
            this.storeTermVector = true;
            this.storePositionWithTermVector = true;
            this.storeOffsetWithTermVector = false;
        } else if (termVector == TermVector.WITH_OFFSETS) {
            this.storeTermVector = true;
            this.storePositionWithTermVector = false;
            this.storeOffsetWithTermVector = true;
        } else if (termVector == TermVector.WITH_POSITIONS_OFFSETS) {
            this.storeTermVector = true;
            this.storePositionWithTermVector = true;
            this.storeOffsetWithTermVector = true;
        } else {
            throw new IllegalArgumentException("unknown termVector parameter " + termVector);
        }
    }

    public final boolean isStored() {
        return isStored;
    }

    public final boolean isIndexed() {
        return isIndexed;
    }

    public final boolean isTokenized() {
        return isTokenized;
    }

    public final boolean isCompressed() {
        return isCompressed;
    }

    public final boolean isTermVectorStored() {
        return storeTermVector;
    }

    public boolean isStoreOffsetWithTermVector() {
        return storeOffsetWithTermVector;
    }

    /**
     * True iff terms are stored as term vector together with their token positions.
     */
    public boolean isStorePositionWithTermVector() {
        return storePositionWithTermVector;
    }

    /** True iff the value of the filed is stored as binary */
    public final boolean isBinary() {
        return isBinary;
    }

    /** True if norms are omitted for this indexed field */
    public boolean getOmitNorms() {
        return omitNorms;
    }

    /** Expert:
     *
     * If set, omit normalization factors associated with this indexed field.
     * This effectively disables indexing boosts and length normalization for this field.
     */
    public void setOmitNorms(boolean omitNorms) {
        this.omitNorms = omitNorms;
    }

    /** Prints a Field for human consumption. */
    public final String toString() {
        StringBuffer result = new StringBuffer();
        if (isStored) {
            result.append("stored");
            if (isCompressed)
                result.append("/compressed");
            else
                result.append("/uncompressed");
        }
        if (isIndexed) {
            if (result.length() > 0)
                result.append(",");
            result.append("indexed");
        }
        if (isTokenized) {
            if (result.length() > 0)
                result.append(",");
            result.append("tokenized");
        }
        if (storeTermVector) {
            if (result.length() > 0)
                result.append(",");
            result.append("termVector");
        }
        if (storeOffsetWithTermVector) {
            if (result.length() > 0)
                result.append(",");
            result.append("termVectorOffsets");
        }
        if (storePositionWithTermVector) {
            if (result.length() > 0)
                result.append(",");
            result.append("termVectorPosition");
        }
        if (isBinary) {
            if (result.length() > 0)
                result.append(",");
            result.append("binary");
        }
        if (omitNorms) {
            result.append(",omitNorms");
        }
        result.append('<');
        result.append(name);
        result.append(':');

        if (fieldsData != null) {
            result.append(fieldsData);
        }

        result.append('>');
        return result.toString();
    }

}
