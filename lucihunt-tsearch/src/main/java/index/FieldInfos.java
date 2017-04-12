
package index;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import document.Document;
import document.Field;
import store.Directory;
import store.IndexInput;
import store.IndexOutput;

/**
 * s
 * 域的元数据信息
 *  
 * Access to the Field Info file that describes document fields and whether or
 *  not they are indexed. Each segment has a separate Field Info file. Objects
 *  of this class are thread-safe for multiple readers, but only one thread can
 *  be adding documents at a time, with no other reader or writer threads
 *  accessing this object.
 */
final class FieldInfos {

    static final byte IS_INDEXED = 0x1;
    static final byte STORE_TERMVECTOR = 0x2;
    static final byte STORE_POSITIONS_WITH_TERMVECTOR = 0x4;
    static final byte STORE_OFFSET_WITH_TERMVECTOR = 0x8;
    static final byte OMIT_NORMS = 0x10;

    //按字段顺序存储字段
    private List<FieldInfo> byNumber = new ArrayList<FieldInfo>();

    private HashMap<String, FieldInfo> byName = new HashMap<String, FieldInfo>();

    FieldInfos() {
    }

    //读取一个现有的域元数据信息
    FieldInfos(Directory d, String name) throws IOException {
        IndexInput input = d.openInput(name);
        try {
            read(input);
        } finally {
            input.close();
        }
    }

    public void add(Document doc) {
        Enumeration<Field> fields = doc.fields();
        while (fields.hasMoreElements()) {
            Field field = (Field) fields.nextElement();
            add(field);
        }
    }

    public void add(Field field) {
        FieldInfo fi = byName.get(field.name());
        if (fi == null) {
            fi = new FieldInfo(field.name(), field.isIndexed(), byNumber.size(), field.isTermVectorStored(), field.isStorePositionWithTermVector(), field.isStoreOffsetWithTermVector(), field.getOmitNorms());
            byNumber.add(fi);
            byName.put(field.name(), fi);
        } else {
            if (fi.isIndexed != field.isIndexed()) {
                fi.isIndexed = true;
            }
            if (fi.storeTermVector != field.isTermVectorStored()) {
                fi.storeTermVector = true;
            }
        }
    }

    public int fieldNumber(String fieldName) {
        try {
            FieldInfo fi = fieldInfoByName(fieldName);
            if (fi != null)
                return fi.number;
        } catch (IndexOutOfBoundsException ioobe) {
            return -1;
        }
        return -1;
    }

    public FieldInfo fieldInfoByName(String fieldName) {
        return (FieldInfo) byName.get(fieldName);
    }

    public FieldInfo fieldInfo(int fieldNumber) {
        try {
            return (FieldInfo) byNumber.get(fieldNumber);
        } catch (IndexOutOfBoundsException ioobe) {
            return null;
        }
    }

    public String fieldName(int fieldNumber) {
        try {
            return fieldInfo(fieldNumber).name;
        } catch (NullPointerException npe) {
            return "";
        }
    }

    public int size() {
        return byNumber.size();
    }

    //
    public void write(Directory d, String name) throws IOException {
        IndexOutput output = d.createOutput(name);
        try {
            write(output);
        } finally {
            output.close();
        }
    }

    public void write(IndexOutput output) throws IOException {
        output.writeVInt(size());
        for (int i = 0; i < size(); i++) {
            FieldInfo fi = fieldInfo(i);
            byte bits = 0x0;
            if (fi.isIndexed)
                bits |= IS_INDEXED;
            if (fi.storeTermVector)
                bits |= STORE_TERMVECTOR;
            if (fi.storePositionWithTermVector)
                bits |= STORE_POSITIONS_WITH_TERMVECTOR;
            if (fi.storeOffsetWithTermVector)
                bits |= STORE_OFFSET_WITH_TERMVECTOR;
            if (fi.omitNorms)
                bits |= OMIT_NORMS;
            output.writeString(fi.name);
            output.writeByte(bits);
        }
    }

    private void read(IndexInput input) throws IOException {
        int size = input.readVInt();//read in the size   老版本上来就是域的个数   新版本还有些版本信息
        for (int i = 0; i < size; i++) {
            String name = input.readString().intern();//名称
            //读取一个字节
            byte bits = input.readByte();
            //低1位  是否索引
            boolean isIndexed = (bits & IS_INDEXED) != 0;
            //低2位  保存词向量信息
            boolean storeTermVector = (bits & STORE_TERMVECTOR) != 0;
            //低3位 词向量中保存位置信息
            boolean storePositionsWithTermVector = (bits & STORE_POSITIONS_WITH_TERMVECTOR) != 0;
            //低4位  词微量中保存偏移量
            boolean storeOffsetWithTermVector = (bits & STORE_OFFSET_WITH_TERMVECTOR) != 0;
            //低5位  保存标准化因子 
            boolean omitNorms = (bits & OMIT_NORMS) != 0;

            //低6位  新版本中 有payload信息

            //构造一个对象出来  
            FieldInfo fi = new FieldInfo(name, isIndexed, byNumber.size(), storeTermVector, storePositionsWithTermVector, storeOffsetWithTermVector, omitNorms);
            byNumber.add(fi);
            byName.put(name, fi);
        }
    }

}
