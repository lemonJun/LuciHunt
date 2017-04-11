package test;

import java.io.IOException;

import org.junit.Test;

import store.FSDirectory;
import store.IndexInput;
import store.IndexOutput;

public class TestDir {

    @Test
    public void initOut() {
        try {
            IndexOutput out = FSDirectory.getDirectory("D:/testdir", true).createOutput("1.tii");
            out.writeInt(1);
            out.writeLong(2222l);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void initIn() {
        try {
            IndexInput input = FSDirectory.getDirectory("D:/testdir", false).openInput("1.tii");
            System.out.println(input.readInt());
            System.out.println(input.readLong());
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
