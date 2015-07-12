package au.com.zacher.popularmovies;

import java.util.Random;

/**
 * Adapted from http://stackoverflow.com/a/41156/3736051
 */
public class RandomString {

    private static final char[] symbols;

    static {
        StringBuilder tmp = new StringBuilder();
        for (char ch = '0'; ch <= '9'; ++ch) {
            tmp.append(ch);
        }
        for (char ch = 'a'; ch <= 'z'; ++ch) {
            tmp.append(ch);
        }
        symbols = tmp.toString().toCharArray();
    }

    private final Random random;

    private final char[] buf;

    public RandomString(Random random, int length) {
        if (length < 1) {
            throw new IllegalArgumentException("length < 1: " + length);
        }
        this.buf = new char[length];
        this.random = random;
    }

    public String nextString() {
        for (int idx = 0; idx < this.buf.length; ++idx) {
            this.buf[idx] = symbols[this.random.nextInt(symbols.length)];
        }
        return new String(this.buf);
    }
}