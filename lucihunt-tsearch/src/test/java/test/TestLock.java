package test;

import com.takin.emmet.file.FileLock;

import junit.framework.TestCase;
import store.FSDirectory;

public class TestLock extends TestCase {

    public static void main(String[] args) {
        try {
            FileLock lock = FSDirectory.getDirectory("D:/testdir", false).makeLock(".lock");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
