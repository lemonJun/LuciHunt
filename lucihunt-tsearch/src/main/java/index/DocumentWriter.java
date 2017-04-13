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

        //对倒排表进行排序
        Posting[] postings = sortPostingTable();

        //写入倒排表  对这一部分  其实是可以在另一个文件中写入的  好管理 
        writerPos(postings, segment);

        // 

    }

    private void writerPos(Posting[] postings, String segment) throws IOException {
        TermInfosWriter tw = null;
        FreqProxWriter fw = null;
        TermVectorsWriter termvertorwriter = null;
        try {
            tw = new TermInfosWriter(directory, segment, fieldInfos, termIndexInterval);
            fw = new FreqProxWriter(directory, segment);
            TermInfo ti = new TermInfo();
            String currentfield = null;
            for (int i = 0; i < postings.length; i++) {
                Posting pos = postings[i];
                ti.set(1, fw.getfreqpointer(), fw.getproxpointer(), termIndexInterval);
                tw.add(pos.term, ti);
                int tf = pos.freq;
                if (tf == 1) {//写入频率倒排
                    fw.getFreq().writeInt(1);
                } else {
                    fw.getFreq().writeInt(0);
                    fw.getFreq().writeInt(tf);
                }
                int lastposition = 0;
                //写入位置倒排
                for (int j = 0; j < pos.positions.length; i++) {
                    int position = pos.positions[j];
                    fw.getProx().writeVInt(position - lastposition);
                    lastposition = position;
                }

                //保存正向信息
                String termfield = pos.term.field();
                //换一个域的时候  就要保存一次正向信息  一个域是对应多个位置的   因为之前已经排过序了  所以可以这样做
                if (currentfield != termfield) {
                    currentfield = termfield;
                    FieldInfo fi = fieldInfos.fieldInfoByName(currentfield);
                    if (fi.storeTermVector) {
                        if (termvertorwriter == null) {
                            termvertorwriter = new TermVectorsWriter(directory, segment, fieldInfos);
                            termvertorwriter.openDocument();
                        }
                        termvertorwriter.openField(currentfield);
                    } else if (termvertorwriter != null) {
                        termvertorwriter.closeField();
                    }
                }
                if (termvertorwriter != null && termvertorwriter.isFieldOpen()) {
                    termvertorwriter.addTerm(pos.term.text(), pos.freq, pos.positions, pos.offsets);
                }
                if (termvertorwriter != null) {
                    termvertorwriter.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (tw != null) {
                tw.close();
            }
            if (fw != null) {
                fw.close();
            }
            if (termvertorwriter != null) {
                termvertorwriter.close();
            }
        }
    }

    public Posting[] sortPostingTable() {
        Posting[] sortpos = new Posting[postingTable.size()];
        Enumeration<Posting> enu = postingTable.elements();
        for (int i = 0; i < sortpos.length; i++) {
            sortpos[i] = enu.nextElement();
        }
        quickSort(sortpos, 0, sortpos.length - 1);
        return sortpos;
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

    private Term termBuffer = new Term("", "");

    //如果词已经存在 则更新其位置信息  如果不存在  则生成一个位置
    private final void addPosition(String field, String text, int position, TermVectorOffsetInfo offset) {
        termBuffer.set(field, text);
        Posting ti = postingTable.get(postingTable);
        //同一个域中出现了多次这个词 
        if (ti != null) {//同一个term已经有这个位置了，肯定要变更位置
            int frep = ti.freq;
            if (ti.positions.length == position) {//位置已经满了
                int[] newpositons = new int[position * 2];
                for (int i = 0; i < position; i++) {
                    newpositons[i] = ti.positions[i];
                }
                ti.positions = newpositons;
            }
            ti.positions[frep] = position;
            if (offset != null) {//同样要更新offset的值
                if (ti.offsets.length == position) {
                    TermVectorOffsetInfo[] newoffset = new TermVectorOffsetInfo[position * 2];
                    for (int i = 0; i < position; i++) {
                        newoffset[i] = ti.offsets[i];
                    }
                    ti.offsets = newoffset;
                }
                ti.offsets[position] = offset;
            }
            ti.freq = frep + 1;
        } else {
            Term term = new Term(field, text);
            Posting posting = new Posting(term, position, offset);
            postingTable.put(term, posting);
        }
    }

    //按字典序给排序    term中是先比较内存   再比较字段 
    private static final void quickSort(Posting[] postings, int lo, int hi) {
        if (lo >= hi)
            return;

        int mid = (lo + hi) / 2;

        if (postings[lo].term.compareTo(postings[mid].term) > 0) {
            Posting tmp = postings[lo];
            postings[lo] = postings[mid];
            postings[mid] = tmp;
        }

        if (postings[mid].term.compareTo(postings[hi].term) > 0) {
            Posting tmp = postings[mid];
            postings[mid] = postings[hi];
            postings[hi] = tmp;

            if (postings[lo].term.compareTo(postings[mid].term) > 0) {
                Posting tmp2 = postings[lo];
                postings[lo] = postings[mid];
                postings[mid] = tmp2;
            }
        }

        int left = lo + 1;
        int right = hi - 1;

        if (left >= right)
            return;

        Term partition = postings[mid].term;

        for (;;) {
            while (postings[right].term.compareTo(partition) > 0)
                --right;

            while (left < right && postings[left].term.compareTo(partition) <= 0)
                ++left;

            if (left < right) {
                Posting tmp = postings[left];
                postings[left] = postings[right];
                postings[right] = tmp;
                --right;
            } else {
                break;
            }
        }

        quickSort(postings, lo, left);
        quickSort(postings, left + 1, hi);
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
