package com.origin.urlshortener.common.code;

import java.security.SecureRandom;

/**
 * Generates short, non-sequential Base62 codes.
 */
public class Base62CodeGenerator {

    private static final char[] ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
    private final SecureRandom random = new SecureRandom();

    public String generate(int length) {
        if (length <= 0) throw new IllegalArgumentException("length must be > 0");
        char[] buf = new char[length];
        for (int i = 0; i < length; i++) {
            buf[i] = ALPHABET[random.nextInt(ALPHABET.length)];
        }
        return new String(buf);
    }
}
