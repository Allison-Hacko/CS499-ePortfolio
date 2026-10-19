package com.example.hop;

import java.security.SecureRandom;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class PasswordUtils {

    private static final int SALT_LENGTH = 16;
    private static final int HASH_LENGTH = 256;
    private static final int ITERATIONS = 120000;

    private PasswordUtils() {
        // Prevent creating instances of this utility class.
    }

    /**
     * Generates a random salt for password hashing.
     */
    public static byte[] generateSalt() {

        byte[] salt = new byte[SALT_LENGTH];

        SecureRandom secureRandom =
                new SecureRandom();

        secureRandom.nextBytes(salt);

        return salt;
    }

    /**
     * Creates a PBKDF2 password hash using the supplied salt.
     */
    public static byte[] hashPassword(
            String password,
            byte[] salt) {

        PBEKeySpec spec = null;

        try {

            spec = new PBEKeySpec(
                    password.toCharArray(),
                    salt,
                    ITERATIONS,
                    HASH_LENGTH
            );

            SecretKeyFactory factory =
                    SecretKeyFactory.getInstance(
                            "PBKDF2WithHmacSHA256"
                    );

            return factory
                    .generateSecret(spec)
                    .getEncoded();

        } catch (Exception e) {

            return null;

        } finally {

            if (spec != null) {
                spec.clearPassword();
            }
        }
    }

    /**
     * Converts a byte array into a hexadecimal string.
     */
    public static String bytesToHex(byte[] bytes) {

        StringBuilder result =
                new StringBuilder();

        for (byte b : bytes) {

            result.append(
                    String.format(
                            "%02x",
                            b
                    )
            );
        }

        return result.toString();
    }

    /**
     * Converts a hexadecimal string back into bytes.
     */
    public static byte[] hexToBytes(String hex) {

        int length = hex.length();

        byte[] bytes =
                new byte[length / 2];

        for (int i = 0; i < length; i += 2) {

            bytes[i / 2] =
                    (byte) (
                            (Character.digit(
                                    hex.charAt(i),
                                    16
                            ) << 4)
                                    +
                                    Character.digit(
                                            hex.charAt(i + 1),
                                            16
                                    )
                    );
        }

        return bytes;
    }
}