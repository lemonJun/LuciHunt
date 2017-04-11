package test;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Test;

import analysis.Token;
import analysis.TokenStream;
import analysis.WhitespaceAnalyzer;

public class TestWhiteSpacAnalyzer {

    @Test
    public void testanalyzer() {
        try {
            WhitespaceAnalyzer ana = new WhitespaceAnalyzer();
            TokenStream stream = ana.tokenStream("name", new StringReader("java is good php is bad"));
            while (true) {
                Token token = stream.next();
                if (null == token) {
                    break;
                }
                System.out.println("token:" + token.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
