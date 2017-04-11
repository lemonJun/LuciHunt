package test;

import java.io.IOException;

import org.junit.Test;

import index.IndexFileNames;
import index.SegmentInfos;
import store.Directory;
import store.FSDirectory;
import store.IndexInput;

public class TestSegmentInfo {

    @Test
    public void write() {
        try {
            SegmentInfos seginfos = new SegmentInfos();
            seginfos.write(FSDirectory.getDirectory("D:/testdir", false));
            seginfos.write(FSDirectory.getDirectory("D:/testdir", false));
            seginfos.write(FSDirectory.getDirectory("D:/testdir", false));
            seginfos.write(FSDirectory.getDirectory("D:/testdir", false));
            seginfos.write(FSDirectory.getDirectory("D:/testdir", false));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void read() {
        try {
            Directory directory = FSDirectory.getDirectory("D:/testdir", false);
            IndexInput input = directory.openInput(IndexFileNames.SEGMENTS);
            System.out.println("format:" + input.readInt());
            System.out.println("version:" + input.readLong());
            System.out.println("counter:" + input.readInt());
            System.out.println("segsize:" + input.readInt());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
