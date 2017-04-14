package index;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import document.Document;
import document.Field;
import store.Directory;
import store.IndexInput;

public class FieldsReader {

    private FieldInfos fieldinfos;

    private IndexInput datastream;

    private IndexInput indexstream;

    private int size;//文档的个数

    FieldsReader(Directory d, String segment, FieldInfos fn) throws IOException {
        fieldinfos = fn;

        datastream = d.openInput(segment + ".fdt");//域数据文件
        indexstream = d.openInput(segment + ".fdx");

        size = (int) (indexstream.length() / 8);
    }

    //从正排表时 恢复一个文档的完整信息
    final Document doc(int n) throws IOException {
        Document doc = new Document();
        indexstream.seek(n * 8L);//一个文档所在的位置是以LONG保存的 
        long datapointer = indexstream.readLong();
        datastream.seek(datapointer);
        
        int fieldsize = datastream.readInt();//字段个数
        for (int i = 0; i < fieldsize; i++) {
            int fieldnum = datastream.readInt();//字段的序号
            FieldInfo fieldinfo = fieldinfos.fieldInfo(fieldnum);
            byte bits = datastream.readByte();//状态位
            boolean compressed = (bits & FieldsWriter.FIELD_IS_COMPRESSED) != 0;
            boolean tokenize = (bits & FieldsWriter.FIELD_IS_TOKENIZED) != 0;

            //分各种情况读字段信息
            if ((bits & index.FieldsWriter.FIELD_IS_BINARY) != 0) {
                final byte[] b = new byte[datastream.readVInt()];
                datastream.readBytes(b, 0, b.length);
                if (compressed) {
                    doc.add(new Field(fieldinfo.name, uncompress(b), Field.Store.COMPRESS));
                } else {
                    doc.add(new Field(fieldinfo.name, b, Field.Store.COMPRESS));
                }
            } else {
                //获取字段的各种配置状态
                Field.Index index;
                Field.Store store = Field.Store.YES;

                if (fieldinfo.isIndexed && tokenize)
                    index = Field.Index.TOKENIZED;
                else if (fieldinfo.isIndexed && !tokenize)
                    index = Field.Index.UN_TOKENIZED;
                else
                    index = Field.Index.NO;
                Field.TermVector termVector = null;
                if (fieldinfo.storeTermVector) {
                    if (fieldinfo.storeOffsetWithTermVector) {
                        if (fieldinfo.storePositionWithTermVector) {
                            termVector = Field.TermVector.WITH_POSITIONS_OFFSETS;
                        } else {
                            termVector = Field.TermVector.WITH_OFFSETS;
                        }
                    } else if (fieldinfo.storePositionWithTermVector) {
                        termVector = Field.TermVector.WITH_POSITIONS;
                    } else {
                        termVector = Field.TermVector.YES;
                    }
                } else {
                    termVector = Field.TermVector.NO;
                }

                if (compressed) {
                    store = Field.Store.COMPRESS;
                    final byte[] b = new byte[datastream.readVInt()];
                    datastream.readBytes(b, 0, b.length);
                    Field f = new Field(fieldinfo.name, new String(uncompress(b), "UTF-8"), // uncompress the value and add as string
                                    store, index, termVector);
                    f.setOmitNorms(fieldinfo.omitNorms);
                    doc.add(f);
                } else {
                    Field f = new Field(fieldinfo.name, // name
                                    datastream.readString(), // read value
                                    store, index, termVector);
                    f.setOmitNorms(fieldinfo.omitNorms);
                    doc.add(f);
                }
            }
        }

        return doc;
    }

    public int getSize() {
        return size;
    }

    public void close() throws IOException {
        datastream.clone();
        indexstream.clone();
    }

    private final byte[] uncompress(final byte[] input) throws IOException {

        Inflater decompressor = new Inflater();
        decompressor.setInput(input);

        // Create an expandable byte array to hold the decompressed data
        ByteArrayOutputStream bos = new ByteArrayOutputStream(input.length);

        // Decompress the data
        byte[] buf = new byte[1024];
        while (!decompressor.finished()) {
            try {
                int count = decompressor.inflate(buf);
                bos.write(buf, 0, count);
            } catch (DataFormatException e) {
                // this will happen if the field is not compressed
                throw new IOException("field data are in wrong format: " + e.toString());
            }
        }

        decompressor.end();

        // Get the decompressed data
        return bos.toByteArray();
    }

}
