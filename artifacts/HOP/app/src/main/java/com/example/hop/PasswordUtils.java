package com.example.hop;

import java.security.GeneralSecurityException;
import java.security.SecureRandom;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class PasswordUtils {

    private static final int SALT_LENGTH = 16;
    private static final int HASH_LENGTH = 256;
    private static final int ITERATIONS = 120000;
    private static final char[] HEX_DIGITS =
            "0123456789abcdef".toCharArray();

    private PasswordUtils() {
        // Prevent creating instances of this utility class.
    }

    public static byte[] generateSalt() {
        byte[] salt = new byte[SALT_LENGTH];
        new SecureRandom().nextBytes(salt);
        return salt;
    }

    /**
     * Generates a PBKDF2-HMAC-SHA256 password hash.
     * Returns null for invalid inputs or cryptographic failure.
     */
    public static byte[] hashPassword(String password, byte[] salt) {
        if (password == null || salt == null
                || salt.length != SALT_LENGTH) {
            return null;
        }

        PBEKeySpec spec = null;

        try {
            spec = new PBEKeySpec(
                    password.toCharArray(),
                    salt,
                    ITERATIONS,
                    HASH_LENGTH
            );

            SecretKeyFactory factory =
                    SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

            return factory.generateSecret(spec).getEncoded();

        } catch (GeneralSecurityException e) {
            return null;

        } finally {
            if (spec != null) {
                spec.clearPassword();
            }
        }
    }

    /**
     * Converts bytes to lowercase hexadecimal without formatting overhead.
     */
    public static String bytesToHex(byte[] bytes) {
        if (bytes == null) {
            throw new IllegalArgumentException("Bytes cannot be null.");
        }

        char[] result = new char[bytes.length * 2];

        for (int i = 0; i < bytes.length; i++) {
            int value = bytes[i] & 0xff;
            result[i * 2] = HEX_DIGITS[value >>> 4];
            result[i * 2 + 1] = HEX_DIGITS[value & 0x0f];
        }

        return new String(result);
    }

    /**
     * Converts hexadecimal to bytes, rejecting malformed input.
     */
    public static byte[] hexToBytes(String hex) {
        if (hex == null) {
            throw new IllegalArgumentException("Hex cannot be null.");
        }

        if ((hex.length() & 1) != 0) {
            throw new IllegalArgumentException(
                    "Hex must contain an even number of characters."
            );
        }

        byte[] bytes = new byte[hex.length() / 2];

        for (int i = 0; i < hex.length(); i += 2) {
            int high = Character.digit(hex.charAt(i), 16);
            int low = Character.digit(hex.charAt(i + 1), 16);

            if (high < 0 || low < 0) {
                throw new IllegalArgumentException(
                        "Hex contains an invalid character."
                );
            }

            bytes[i / 2] = (byte) ((high << 4) | low);
        }

        return bytes;
    }
}