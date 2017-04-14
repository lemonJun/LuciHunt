package index;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.Deflater;

import document.Document;
import document.Field;
import store.Directory;
import store.IndexOutput;

/**
 * 写fdt fdx 文件
 *
 * @author WangYazhou
 * @date  2017年4月12日 上午10:33:38
 * @see
 */
public class FieldsWriter {

    static final byte FIELD_IS_TOKENIZED = 0x1;
    static final byte FIELD_IS_BINARY = 0x2;
    static final byte FIELD_IS_COMPRESSED = 0x4;

    private IndexOutput datastream;

    private IndexOutput indexstream;

    public FieldInfos fs;

    public FieldsWriter(Directory directory, String segment, FieldInfos fieldinfos) throws IOException {
        fs = fieldinfos;
        datastream = directory.createOutput(segment + ".fdt");
        indexstream = directory.createOutput(segment + ".fdx");
    }

    public void addDocument(Document doc) throws IOException {
        //在索引中记录数据的位置
        try {
            indexstream.writeLong(datastream.getFilePointer());
            //在数据中写入域的个数
            datastream.writeInt(doc.fieldscnt());
            Enumeration<Field> emu = doc.fields();
            while (emu.hasMoreElements()) {
                Field field = emu.nextElement();
                if (field.isStored()) {
                    int num = fs.fieldNumber(field.name());
                    datastream.writeInt(num);
                    byte bits = 0;
                    if (field.isTokenized()) {
                        bits |= FIELD_IS_TOKENIZED;
                    }
                    if (field.isBinary()) {
                        bits |= FIELD_IS_BINARY;
                    }
                    if (field.isCompressed()) {
                        bits |= FIELD_IS_COMPRESSED;
                    }
                    //写入状态位 
                    datastream.writeByte(bits);

                    if (field.isCompressed()) {
                        byte[] data = null;
                        // check if it is a binary field
                        if (field.isBinary()) {
                            data = compress(field.binaryValue());
                        } else {
                            data = compress(field.stringValue().getBytes("UTF-8"));
                        }
                        final int len = data.length;
                        datastream.writeInt(len);
                        datastream.writeBytes(data, len);
                    } else {
                        if (field.isBinary()) {
                            byte[] data = field.binaryValue();
                            final int len = data.length;
                            datastream.writeInt(len);
                            datastream.writeBytes(data, len);
                        } else {
                            datastream.writeString(field.stringValue());
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    public void close() throws IOException {
        indexstream.close();
        datastream.close();
    }

    //数据压缩
    private final byte[] compress(byte[] input) {
        // Create the compressor with highest level of compression
        Deflater compressor = new Deflater();
        compressor.setLevel(Deflater.BEST_COMPRESSION);

        // Give the compressor the data to compress
        compressor.setInput(input);
        compressor.finish();

        /*
         * Create an expandable byte array to hold the compressed data.
         * You cannot use an array that's the same size as the orginal because
         * there is no guarantee that the compressed data will be smaller than
         * the uncompressed data.
         */
        ByteArrayOutputStream bos = new ByteArrayOutputStream(input.length);

        // Compress the data
        byte[] buf = new byte[1024];
        while (!compressor.finished()) {
            int count = compressor.deflate(buf);
            bos.write(buf, 0, count);
        }

        compressor.end();

        // Get the compressed data
        return bos.toByteArray();
    }

}
