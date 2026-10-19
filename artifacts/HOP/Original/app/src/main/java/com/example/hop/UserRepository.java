package com.example.hop;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.security.MessageDigest;

public class UserRepository {

    private static final String TABLE_USERS = "Users";
    private static final String USERNAME = "username";
    private static final String PASSWORD_HASH = "password_hash";
    private static final String PASSWORD_SALT = "password_salt";

    private final DatabaseHelper databaseHelper;

    public UserRepository(Context context) {
        databaseHelper = new DatabaseHelper(context);
    }

    /**
     * Creates a new user account.
     *
     * @return true if the account was created successfully.
     */
    public boolean addUser(
            String username,
            String password) {

        if (username == null
                || username.trim().isEmpty()
                || password == null
                || password.isEmpty()) {

            return false;
        }

        byte[] salt =
                PasswordUtils.generateSalt();

        byte[] hash =
                PasswordUtils.hashPassword(
                        password,
                        salt
                );

        if (hash == null) {
            return false;
        }

        ContentValues values =
                new ContentValues();

        values.put(
                USERNAME,
                username.trim()
        );

        values.put(
                PASSWORD_HASH,
                PasswordUtils.bytesToHex(hash)
        );

        values.put(
                PASSWORD_SALT,
                PasswordUtils.bytesToHex(salt)
        );

        SQLiteDatabase db =
                databaseHelper.getWritableDatabase();

        long result =
                db.insert(
                        TABLE_USERS,
                        null,
                        values
                );

        return result != -1;
    }

    /**
     * Checks whether the supplied username and password
     * match an existing account.
     *
     * @return true when the credentials are valid.
     */
    public boolean checkUser(
            String username,
            String password) {

        if (username == null
                || username.trim().isEmpty()
                || password == null
                || password.isEmpty()) {

            return false;
        }

        SQLiteDatabase db =
                databaseHelper.getReadableDatabase();

        Cursor cursor =
                db.rawQuery(
                        "SELECT "
                                + PASSWORD_HASH
                                + ", "
                                + PASSWORD_SALT
                                + " FROM "
                                + TABLE_USERS
                                + " WHERE "
                                + USERNAME
                                + " = ?",
                        new String[]{
                                username.trim()
                        }
                );

        try {

            if (!cursor.moveToFirst()) {
                return false;
            }

            String storedHash =
                    cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    PASSWORD_HASH
                            )
                    );

            String storedSalt =
                    cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    PASSWORD_SALT
                            )
                    );

            byte[] salt =
                    PasswordUtils.hexToBytes(
                            storedSalt
                    );

            byte[] calculatedHash =
                    PasswordUtils.hashPassword(
                            password,
                            salt
                    );

            if (calculatedHash == null) {
                return false;
            }

            byte[] savedHash =
                    PasswordUtils.hexToBytes(
                            storedHash
                    );

            return MessageDigest.isEqual(
                    savedHash,
                    calculatedHash
            );

        } finally {

            cursor.close();
        }
    }
}