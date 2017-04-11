package test;

import org.junit.Test;

public class TestInt {

    @Test
    public void testbyte() {
        int i = 1112111;
        System.out.println((byte) (i >> 24));
        System.out.println((byte) (i >> 16));
        System.out.println((byte) (i >> 8));
        System.out.println((byte) i);
    }
    
}
