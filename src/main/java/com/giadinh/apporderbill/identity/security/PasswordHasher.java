package com.giadinh.apporderbill.identity.security;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;

public final class PasswordHasher {

    private static final String SHA256_PREFIX = "sha256";
    private static final String PBKDF2_PREFIX = "pbkdf2";
    private static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int SALT_LENGTH = 16;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final HexFormat HEX = HexFormat.of();

    private PasswordHasher() {
    }

    public static String hash(String rawPassword) {
        byte[] salt = new byte[SALT_LENGTH];
        SECURE_RANDOM.nextBytes(salt);
        byte[] digest = sha256WithSalt(rawPassword, salt);
        return SHA256_PREFIX + "$" + HEX.formatHex(salt) + "$" + HEX.formatHex(digest);
    }

    public static boolean matches(String rawPassword, String storedValue) {
        if (rawPassword == null || storedValue == null || storedValue.isBlank()) {
            return false;
        }
        if (storedValue.startsWith(SHA256_PREFIX + "$")) {
            return matchesSha256(rawPassword, storedValue);
        }
        if (storedValue.startsWith(PBKDF2_PREFIX + "$")) {
            return matchesPbkdf2Legacy(rawPassword, storedValue);
        }
        return constantTimeEquals(rawPassword, storedValue);
    }

    /**
     * True when stored value is not the current SHA-256 format (plain text, legacy PBKDF2, etc.).
     */
    public static boolean needsRehash(String storedValue) {
        return storedValue == null || !storedValue.startsWith(SHA256_PREFIX + "$");
    }

    private static boolean matchesSha256(String rawPassword, String storedValue) {
        String[] parts = storedValue.split("\\$", 3);
        if (parts.length != 3) {
            return false;
        }
        byte[] salt;
        byte[] expected;
        try {
            salt = HEX.parseHex(parts[1]);
            expected = HEX.parseHex(parts[2]);
        } catch (IllegalArgumentException ex) {
            return false;
        }
        byte[] actual = sha256WithSalt(rawPassword, salt);
        return constantTimeEquals(actual, expected);
    }

    private static byte[] sha256WithSalt(String rawPassword, byte[] salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            md.update(rawPassword.getBytes(StandardCharsets.UTF_8));
            return md.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    private static boolean matchesPbkdf2Legacy(String rawPassword, String storedValue) {
        String[] parts = storedValue.split("\\$");
        if (parts.length != 4) {
            return false;
        }
        int iterations;
        try {
            iterations = Integer.parseInt(parts[1]);
        } catch (NumberFormatException ex) {
            return false;
        }
        byte[] salt;
        byte[] expected;
        try {
            salt = Base64.getDecoder().decode(parts[2]);
            expected = Base64.getDecoder().decode(parts[3]);
        } catch (IllegalArgumentException ex) {
            return false;
        }
        byte[] actual = pbkdf2(rawPassword.toCharArray(), salt, iterations, expected.length * 8);
        return constantTimeEquals(actual, expected);
    }

    private static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int keyLength) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLength);
            SecretKeyFactory skf = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM);
            return skf.generateSecret(spec).getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Unable to verify legacy password hash", e);
        }
    }

    private static boolean constantTimeEquals(byte[] left, byte[] right) {
        if (left.length != right.length) {
            return false;
        }
        int result = 0;
        for (int i = 0; i < left.length; i++) {
            result |= left[i] ^ right[i];
        }
        return result == 0;
    }

    private static boolean constantTimeEquals(String left, String right) {
        return constantTimeEquals(
                left.getBytes(StandardCharsets.UTF_8),
                right.getBytes(StandardCharsets.UTF_8)
        );
    }
}
