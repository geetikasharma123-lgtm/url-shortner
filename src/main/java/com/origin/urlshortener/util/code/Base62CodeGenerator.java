package com.origin.urlshortener.util.code;

import java.security.SecureRandom;

/**
 * Generates short, non-sequential Base62 codes.
 */
public final class Base62CodeGenerator {

    private static final char[] ALPHABET =
            "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
    private static final int ALPHABET_LENGTH = ALPHABET.length;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public String generate(int length) {
        if (length < 1) {
            throw new IllegalArgumentException("length must be greater than 0");
        }

        char[] buf = new char[length];
        for (int i = 0; i < length; i++) {
            buf[i] = ALPHABET[SECURE_RANDOM.nextInt(ALPHABET_LENGTH)];
        }

        return new String(buf);
    }
}
