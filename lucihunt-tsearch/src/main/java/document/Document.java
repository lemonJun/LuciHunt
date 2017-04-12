package document;

import java.util.ArrayList;

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

import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/** Documents are the unit of indexing and search.
 *
 * A Document is a set of fields.  Each field has a name and a textual value.
 * A field may be {@link Field#isStored() stored} with the document, in which
 * case it is returned with search hits on the document.  Thus each document
 * should typically contain one or more stored fields which uniquely identify
 * it.
 *
 * <p>Note that fields which are <i>not</i> {@link Field#isStored() stored} are
 * <i>not</i> available in documents retrieved from the index, e.g. with {@link
 * Hits#doc(int)}, {@link Searcher#doc(int)} or {@link
 * IndexReader#document(int)}.
 */

public final class Document implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    List<Field> fields = new Vector<Field>();
    private float boost = 1.0f;

    /** Constructs a new document with no fields. */
    public Document() {
    }

    public void setBoost(float boost) {
        this.boost = boost;
    }

    public float getBoost() {
        return boost;
    }

    public final void add(Field field) {
        fields.add(field);
    }

    public final void removeField(String name) {
        Iterator it = fields.iterator();
        while (it.hasNext()) {
            Field field = (Field) it.next();
            if (field.name().equals(name)) {
                it.remove();
                return;
            }
        }
    }

    public final void removeFields(String name) {
        Iterator it = fields.iterator();
        while (it.hasNext()) {
            Field field = (Field) it.next();
            if (field.name().equals(name)) {
                it.remove();
            }
        }
    }

    /** Returns a field with the given name if any exist in this document, or
     * null.  If multiple fields exists with this name, this method returns the
     * first value added.
     */
    public final Field getField(String name) {
        for (int i = 0; i < fields.size(); i++) {
            Field field = (Field) fields.get(i);
            if (field.name().equals(name))
                return field;
        }
        return null;
    }

    public final String get(String name) {
        for (int i = 0; i < fields.size(); i++) {
            Field field = (Field) fields.get(i);
            if (field.name().equals(name) && (!field.isBinary()))
                return field.stringValue();
        }
        return null;
    }

    /** Returns an Enumeration of all the fields in a document. */
    public final Enumeration<Field> fields() {
        return ((Vector<Field>) fields).elements();
    }

    public final int fieldscnt() {
        return fields.size();
    }

    /**
     * Returns an array of {@link Field}s with the given name.
     * This method can return <code>null</code>.
     * 
     * @param name the name of the field
     * @return a <code>Field[]</code> array
     */
    public final Field[] getFields(String name) {
        List result = new ArrayList();
        for (int i = 0; i < fields.size(); i++) {
            Field field = (Field) fields.get(i);
            if (field.name().equals(name)) {
                result.add(field);
            }
        }

        if (result.size() == 0)
            return null;

        return (Field[]) result.toArray(new Field[result.size()]);
    }

    /**
     * Returns an array of values of the field specified as the method parameter.
     * This method can return <code>null</code>.
     *
     * @param name the name of the field
     * @return a <code>String[]</code> of field values
     */
    public final String[] getValues(String name) {
        List result = new ArrayList();
        for (int i = 0; i < fields.size(); i++) {
            Field field = (Field) fields.get(i);
            if (field.name().equals(name) && (!field.isBinary()))
                result.add(field.stringValue());
        }

        if (result.size() == 0)
            return null;

        return (String[]) result.toArray(new String[result.size()]);
    }

    /**
    * Returns an array of byte arrays for of the fields that have the name specified
    * as the method parameter. This method will return <code>null</code> if no
    * binary fields with the specified name are available.
    *
    * @param name the name of the field
    * @return a  <code>byte[][]</code> of binary field values.
    */
    public final byte[][] getBinaryValues(String name) {
        List result = new ArrayList();
        for (int i = 0; i < fields.size(); i++) {
            Field field = (Field) fields.get(i);
            if (field.name().equals(name) && (field.isBinary()))
                result.add(field.binaryValue());
        }

        if (result.size() == 0)
            return null;

        return (byte[][]) result.toArray(new byte[result.size()][]);
    }

    /**
    * Returns an array of bytes for the first (or only) field that has the name
    * specified as the method parameter. This method will return <code>null</code>
    * if no binary fields with the specified name are available.
    * There may be non-binary fields with the same name.
    *
    * @param name the name of the field.
    * @return a <code>byte[]</code> containing the binary field value.
    */
    public final byte[] getBinaryValue(String name) {
        for (int i = 0; i < fields.size(); i++) {
            Field field = (Field) fields.get(i);
            if (field.name().equals(name) && (field.isBinary()))
                return field.binaryValue();
        }
        return null;
    }

    /** Prints the fields of a document for human consumption. */
    public final String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("Document<");
        for (int i = 0; i < fields.size(); i++) {
            Field field = (Field) fields.get(i);
            buffer.append(field.toString());
            if (i != fields.size() - 1)
                buffer.append(" ");
        }
        buffer.append(">");
        return buffer.toString();
    }
}
