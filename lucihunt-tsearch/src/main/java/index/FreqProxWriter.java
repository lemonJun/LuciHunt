package index;

import java.io.IOException;

import store.Directory;
import store.IndexOutput;

public class FreqProxWriter {

    private IndexOutput freq;
    private IndexOutput prox;

    public FreqProxWriter(Directory directory, String seg) throws IOException {
        freq = directory.createOutput(seg + ".frq");
        prox = directory.createOutput(seg + ".prx");
    }

    public void close() throws IOException {
        freq.close();
        prox.close();
    }

    public long getfreqpointer() {
        return freq.getFilePointer();
    }

    public long getproxpointer() {
        return prox.getFilePointer();
    }

    public IndexOutput getFreq() {
        return freq;
    }

    public void setFreq(IndexOutput freq) {
        this.freq = freq;
    }

    public IndexOutput getProx() {
        return prox;
    }

    public void setProx(IndexOutput prox) {
        this.prox = prox;
    }

}
