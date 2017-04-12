package index;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;

import analysis.Analyzer;
import analysis.Token;
import analysis.TokenStream;
import document.Document;
import document.Field;
import search.Similarity;
import store.Directory;

public class DocumentWriter {

    private Analyzer analyzer;
    private Directory directory;
    private Similarity similarity;
    private FieldInfos fieldInfos;
    private int maxFieldLength;
    private int termIndexInterval = 128;

    public DocumentWriter(Directory directory, Analyzer analyzer, Similarity similarity, int maxFieldLength) {
        this.directory = directory;
        this.analyzer = analyzer;
        this.similarity = similarity;
        this.maxFieldLength = maxFieldLength;
    }

    private final Hashtable<Term, Posting> postingTable = new Hashtable<Term, Posting>();
    private int[] fieldLengths;
    private int[] fieldPositions;
    private int[] fieldOffsets;
    private float[] fieldBoosts;

    /**
     * 建索引的主要类
     * Step1:保存域的元数据信息
     * Step2:保存域的索引与原始内容
     * Step3:构造倒排表信息并保存
     * Step4:合并段信息
     * @param segment
     * @param doc 
     * @throws IOException
     */
    final public void addDocument(String segment, Document doc) throws IOException {
        //默认为是一个documentwriter写到一个索引段中  所以此处是直接new了一个  但这样一个段就只能包含一个文档了；
        fieldInfos = new FieldInfos();
        fieldInfos.add(doc);
        fieldInfos.write(directory, segment + ".fnm");//写完域的元数据信息
        //开始写入字段 
        FieldsWriter fieldwriter = new FieldsWriter(directory, segment, fieldInfos);
        fieldwriter.addDocument(doc);

        //准备写入倒排表
        initinvert(doc);

        //准备倒排表数据 
        invertdocument(doc);

    }

    //生成倒排表所需要的数据 
    public void invertdocument(Document doc) throws IOException {
        Enumeration<Field> enu = doc.fields();
        while (enu.hasMoreElements()) {
            Field field = enu.nextElement();
            //第几个域
            int fieldnum = fieldInfos.fieldNumber(field.name());
            //同一个域出现的次数   做为一个考虑成熟的搜索引擎    它允许一个DOC添加多个相同的域的 
            int length = fieldLengths[fieldnum];//
            //位置是以域为单位的
            int position = fieldPositions[fieldnum];
            //对域名相同的两个字段    可以选择默认的从0开始计位置  也可以做位置累加   实现方式就是覆盖下面这个方法    
            if (length > 0) {
                position += analyzer.getPositionIncrementGap(field.name());
            }
            int offset = fieldOffsets[fieldnum];
            if (field.isIndexed()) {
                //不分词
                if (!field.isTokenized()) {
                    //存储向量信息
                    if (field.isStoreOffsetWithTermVector()) {
                        addPosition(field.name(), field.stringValue(), position, new TermVectorOffsetInfo(offset, offset + field.stringValue().length()));
                    } else {
                        addPosition(field.name(), field.stringValue(), position, null);
                    }
                } else {//分词
                    Reader reader = null;
                    if (field.readerValue() != null)
                        reader = field.readerValue();
                    else if (field.stringValue() != null)
                        reader = new StringReader(field.stringValue());
                    else
                        throw new IllegalArgumentException("field must have either String or Reader value");

                    TokenStream tokenstream = analyzer.tokenStream(field.name(), reader);
                    try {
                        Token lasttoken = null;
                        for (Token t = tokenstream.next(); t != null; t = tokenstream.next()) {
                            System.out.println(t.termText());
                            position += t.getPositionIncrement() - 1;//在外部维护位置
                            if (field.isStoreOffsetWithTermVector()) {
                                addPosition(field.name(), t.termText(), position++, new TermVectorOffsetInfo(offset, offset + field.stringValue().length()));
                            } else {
                                addPosition(field.name(), t.termText(), position++, null);
                            }
                            lasttoken = t;
                            //
                            if (++length > maxFieldLength) {
                                System.out.println("maxFieldLength " + maxFieldLength + " reached, ignoring following tokens");
                                break;
                            }
                        }
                        //这个是留给后面保存位置用的
                        if (lasttoken != null) {
                            offset += lasttoken.endOffset() + 1;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        tokenstream.close();
                    }
                }
                //
                fieldLengths[fieldnum] = length; // save field length
                fieldPositions[fieldnum] = position; // save field position
                fieldBoosts[fieldnum] *= field.getBoost();
                fieldOffsets[fieldnum] = offset;
            }
        }
    }

    //添加一个位置信息
    private final void addPosition(String field, String text, int position, TermVectorOffsetInfo offset) {
        Term term = new Term(field, text);
        Posting posting = new Posting(term, position, offset);
        
    }

    public void initinvert(Document doc) {
        postingTable.clear();
        fieldLengths = new int[fieldInfos.size()]; // init fieldLengths
        fieldPositions = new int[fieldInfos.size()]; // init fieldPositions
        fieldOffsets = new int[fieldInfos.size()]; // init fieldOffsets
        fieldBoosts = new float[fieldInfos.size()]; // init fieldBoosts
        Arrays.fill(fieldBoosts, doc.getBoost());//把域的权重   默认设置为文档的权重
    }

    public Analyzer getAnalyzer() {
        return analyzer;
    }

    public void setAnalyzer(Analyzer analyzer) {
        this.analyzer = analyzer;
    }

    public Directory getDirectory() {
        return directory;
    }

    public void setDirectory(Directory directory) {
        this.directory = directory;
    }

    public Similarity getSimilarity() {
        return similarity;
    }

    public void setSimilarity(Similarity similarity) {
        this.similarity = similarity;
    }

    public FieldInfos getFieldInfos() {
        return fieldInfos;
    }

    public void setFieldInfos(FieldInfos fieldInfos) {
        this.fieldInfos = fieldInfos;
    }

    public int getMaxFieldLength() {
        return maxFieldLength;
    }

    public void setMaxFieldLength(int maxFieldLength) {
        this.maxFieldLength = maxFieldLength;
    }

    public int getTermIndexInterval() {
        return termIndexInterval;
    }

    public void setTermIndexInterval(int termIndexInterval) {
        this.termIndexInterval = termIndexInterval;
    }

}
