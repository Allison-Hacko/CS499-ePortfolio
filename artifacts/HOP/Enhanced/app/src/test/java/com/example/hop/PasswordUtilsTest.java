package com.example.hop;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class PasswordUtilsTest {

    @Test
    public void generatedSaltHasExpectedLength() {
        assertEquals(16, PasswordUtils.generateSalt().length);
    }

    @Test
    public void generatedSaltsAreDifferent() {
        assertFalse(Arrays.equals(
                PasswordUtils.generateSalt(),
                PasswordUtils.generateSalt()
        ));
    }

    @Test
    public void samePasswordAndSaltProduceSameHash() {
        byte[] salt = PasswordUtils.generateSalt();

        assertArrayEquals(
                PasswordUtils.hashPassword("test-password", salt),
                PasswordUtils.hashPassword("test-password", salt)
        );
    }

    @Test
    public void differentSaltsProduceDifferentHashes() {
        assertFalse(Arrays.equals(
                PasswordUtils.hashPassword(
                        "test-password", PasswordUtils.generateSalt()),
                PasswordUtils.hashPassword(
                        "test-password", PasswordUtils.generateSalt())
        ));
    }

    @Test
    public void hashHasExpectedLength() {
        assertEquals(
                32,
                PasswordUtils.hashPassword(
                        "test-password",
                        PasswordUtils.generateSalt()
                ).length
        );
    }

    @Test
    public void hexadecimalConversionRoundTrips() {
        byte[] original = new byte[] {0, 1, 15, 16, 127, -1};

        assertArrayEquals(
                original,
                PasswordUtils.hexToBytes(
                        PasswordUtils.bytesToHex(original)
                )
        );
    }

    @Test
    public void hexadecimalDecoderAcceptsUppercase() {
        assertArrayEquals(
                new byte[] {(byte) 0xab, (byte) 0xcd},
                PasswordUtils.hexToBytes("ABCD")
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void hexadecimalDecoderRejectsOddLength() {
        PasswordUtils.hexToBytes("abc");
    }

    @Test(expected = IllegalArgumentException.class)
    public void hexadecimalDecoderRejectsInvalidCharacters() {
        PasswordUtils.hexToBytes("zz");
    }

    @Test(expected = IllegalArgumentException.class)
    public void hexadecimalDecoderRejectsNull() {
        PasswordUtils.hexToBytes(null);
    }

    @Test
    public void hashingRejectsInvalidInputs() {
        assertNull(PasswordUtils.hashPassword(
                null, PasswordUtils.generateSalt()));

        assertNull(PasswordUtils.hashPassword(
                "password", null));

        assertNull(PasswordUtils.hashPassword(
                "password", new byte[0]));
    }
}