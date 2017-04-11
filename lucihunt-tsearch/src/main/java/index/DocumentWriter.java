package index;

import java.io.IOException;

import analysis.Analyzer;
import document.Document;
import search.Similarity;
import store.Directory;

public class DocumentWriter {

    private Analyzer analyzer;
    private Directory directory;
    private Similarity similarity;
    private FieldInfos fieldInfos;
    private int maxFieldLength;
    private int termIndexInterval = 128;

    DocumentWriter(Directory directory, Analyzer analyzer, Similarity similarity, int maxFieldLength) {
        this.directory = directory;
        this.analyzer = analyzer;
        this.similarity = similarity;
        this.maxFieldLength = maxFieldLength;
    }

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
    final void addDocument(String segment, Document doc) throws IOException {

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
