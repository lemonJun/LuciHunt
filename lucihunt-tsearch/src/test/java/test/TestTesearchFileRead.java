package test;

import java.io.IOException;

import org.junit.Test;

import store.FSDirectory;
import store.IndexInput;

public class TestTesearchFileRead {
    @Test
    public void readseg() {
        try {
            IndexInput input = FSDirectory.getDirectory("D:/testdir", false).openInput("segments");
            System.out.println(input.readInt());
            System.out.println(input.readLong());
            System.out.println(input.readInt());
            int size = input.readInt();
            System.out.println("size" + size);
            if (size > 0) {
                for (int i = 0; i < size; i++) {
                    System.out.println(input.readString());
                    System.out.println(input.readInt());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
