package com.example.hop;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.security.MessageDigest;
import java.security.SecureRandom;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "HOP.db";
    private static final int DATABASE_VERSION = 3;

    // ============================================================
    // USERS TABLE
    // ============================================================

    private static final String TABLE_USERS = "Users";

    private static final String USER_ID = "id";
    private static final String USERNAME = "username";
    private static final String PASSWORD_HASH = "password_hash";
    private static final String PASSWORD_SALT = "password_salt";

    // ============================================================
    // WEIGHT ENTRIES TABLE
    // ============================================================

    private static final String TABLE_WEIGHTS = "WeightEntries";

    private static final String WEIGHT_ID = "id";
    private static final String WEIGHT_USERNAME = "username";
    private static final String WEIGHT = "weight";
    private static final String DATE = "date";

    // ============================================================
    // PROFILE TABLE
    // ============================================================

    private static final String TABLE_PROFILE = "Profile";

    private static final String PROFILE_USERNAME = "username";
    private static final String FIRST_NAME = "first_name";
    private static final String GENDER = "gender";
    private static final String HEIGHT = "height";
    private static final String PHONE = "phone";
    private static final String EMAIL = "email";
    private static final String BIO = "bio";
    private static final String GOAL_WEIGHT = "goal_weight";

    // ============================================================
    // PASSWORD SECURITY
    // ============================================================

    private static final int SALT_LENGTH = 16;
    private static final int HASH_LENGTH = 256;
    private static final int ITERATIONS = 120000;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // ============================================================
    // DATABASE CREATION
    // ============================================================

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createUsersTable =
                "CREATE TABLE " + TABLE_USERS + " (" +
                        USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        USERNAME + " TEXT UNIQUE NOT NULL, " +
                        PASSWORD_HASH + " TEXT NOT NULL, " +
                        PASSWORD_SALT + " TEXT NOT NULL)";

        db.execSQL(createUsersTable);

        String createWeightsTable =
                "CREATE TABLE " + TABLE_WEIGHTS + " (" +
                        WEIGHT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        WEIGHT_USERNAME + " TEXT NOT NULL, " +
                        WEIGHT + " REAL NOT NULL, " +
                        DATE + " TEXT NOT NULL)";

        db.execSQL(createWeightsTable);

        String createProfileTable =
                "CREATE TABLE " + TABLE_PROFILE + " (" +
                        PROFILE_USERNAME + " TEXT PRIMARY KEY, " +
                        FIRST_NAME + " TEXT NOT NULL, " +
                        GENDER + " TEXT, " +
                        HEIGHT + " TEXT, " +
                        PHONE + " TEXT, " +
                        EMAIL + " TEXT, " +
                        BIO + " TEXT, " +
                        GOAL_WEIGHT + " REAL DEFAULT 150.0)";

        db.execSQL(createProfileTable);
    }

    // ============================================================
    // DATABASE UPGRADES
    // ============================================================

    @Override
    public void onUpgrade(
            SQLiteDatabase db,
            int oldVersion,
            int newVersion) {

        if (oldVersion < 2) {

            String createProfileTable =
                    "CREATE TABLE IF NOT EXISTS " +
                            TABLE_PROFILE + " (" +
                            PROFILE_USERNAME + " TEXT PRIMARY KEY, " +
                            FIRST_NAME + " TEXT NOT NULL, " +
                            GENDER + " TEXT, " +
                            HEIGHT + " TEXT, " +
                            PHONE + " TEXT, " +
                            EMAIL + " TEXT, " +
                            BIO + " TEXT)";

            db.execSQL(createProfileTable);
        }

        if (oldVersion < 3) {

            db.execSQL(
                    "ALTER TABLE " +
                            TABLE_PROFILE +
                            " ADD COLUMN " +
                            GOAL_WEIGHT +
                            " REAL DEFAULT 150.0"
            );
        }
    }

    // ============================================================
    // USER ACCOUNT METHODS
    // ============================================================

    public boolean addUser(
            String username,
            String password) {

        SQLiteDatabase db =
                this.getWritableDatabase();

        byte[] salt =
                generateSalt();

        byte[] hash =
                hashPassword(
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
                username
        );

        values.put(
                PASSWORD_HASH,
                bytesToHex(hash)
        );

        values.put(
                PASSWORD_SALT,
                bytesToHex(salt)
        );

        long result =
                db.insert(
                        TABLE_USERS,
                        null,
                        values
                );

        return result != -1;
    }

    public boolean checkUser(
            String username,
            String password) {

        SQLiteDatabase db =
                this.getReadableDatabase();

        Cursor cursor =
                db.rawQuery(
                        "SELECT " +
                                PASSWORD_HASH +
                                ", " +
                                PASSWORD_SALT +
                                " FROM " +
                                TABLE_USERS +
                                " WHERE " +
                                USERNAME +
                                " = ?",
                        new String[]{
                                username
                        }
                );

        if (!cursor.moveToFirst()) {

            cursor.close();

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

        cursor.close();

        byte[] salt =
                hexToBytes(storedSalt);

        byte[] calculatedHash =
                hashPassword(
                        password,
                        salt
                );

        if (calculatedHash == null) {
            return false;
        }

        byte[] savedHash =
                hexToBytes(storedHash);

        return MessageDigest.isEqual(
                savedHash,
                calculatedHash
        );
    }

    // ============================================================
    // PASSWORD SECURITY
    // ============================================================

    private byte[] generateSalt() {

        byte[] salt =
                new byte[SALT_LENGTH];

        SecureRandom secureRandom =
                new SecureRandom();

        secureRandom.nextBytes(salt);

        return salt;
    }

    private byte[] hashPassword(
            String password,
            byte[] salt) {

        PBEKeySpec spec = null;

        try {

            spec =
                    new PBEKeySpec(
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

            e.printStackTrace();

            return null;

        } finally {

            if (spec != null) {
                spec.clearPassword();
            }
        }
    }

    private String bytesToHex(
            byte[] bytes) {

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

    private byte[] hexToBytes(
            String hex) {

        int length =
                hex.length();

        byte[] bytes =
                new byte[length / 2];

        for (
                int i = 0;
                i < length;
                i += 2
        ) {

            bytes[i / 2] =
                    (byte)
                            (
                                    (
                                            Character.digit(
                                                    hex.charAt(i),
                                                    16
                                            ) << 4
                                    )
                                            +
                                            Character.digit(
                                                    hex.charAt(i + 1),
                                                    16
                                            )
                            );
        }

        return bytes;
    }

    // ============================================================
    // WEIGHT METHODS
    // ============================================================

    public long addWeight(
            String username,
            double weight,
            String date) {

        SQLiteDatabase db =
                this.getWritableDatabase();

        ContentValues values =
                new ContentValues();

        values.put(
                WEIGHT_USERNAME,
                username
        );

        values.put(
                WEIGHT,
                weight
        );

        values.put(
                DATE,
                date
        );

        return db.insert(
                TABLE_WEIGHTS,
                null,
                values
        );
    }

    /**
     * Returns weight entries with the newest DATE first.
     *
     * Dates are expected to be stored as MM/dd/yyyy.
     *
     * The date is converted into yyyyMMdd for sorting.
     * The database ID is used as a secondary sort so that
     * two entries on the same date show newest-added first.
     */
    public Cursor getWeights(
            String username) {

        SQLiteDatabase db =
                this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM " +
                        TABLE_WEIGHTS +
                        " WHERE " +
                        WEIGHT_USERNAME +
                        " = ? " +
                        "ORDER BY " +
                        "substr(" + DATE + ", 7, 4) || " +
                        "substr(" + DATE + ", 1, 2) || " +
                        "substr(" + DATE + ", 4, 2) DESC, " +
                        WEIGHT_ID + " DESC",
                new String[]{
                        username
                }
        );
    }

    public boolean updateWeight(
            int id,
            double weight,
            String date) {

        SQLiteDatabase db =
                this.getWritableDatabase();

        ContentValues values =
                new ContentValues();

        values.put(
                WEIGHT,
                weight
        );

        values.put(
                DATE,
                date
        );

        int result =
                db.update(
                        TABLE_WEIGHTS,
                        values,
                        WEIGHT_ID +
                                " = ?",
                        new String[]{
                                String.valueOf(id)
                        }
                );

        return result > 0;
    }

    public boolean deleteWeight(
            int id) {

        SQLiteDatabase db =
                this.getWritableDatabase();

        int result =
                db.delete(
                        TABLE_WEIGHTS,
                        WEIGHT_ID +
                                " = ?",
                        new String[]{
                                String.valueOf(id)
                        }
                );

        return result > 0;
    }

    // ============================================================
    // PROFILE METHODS
    // ============================================================

    public boolean saveProfile(
            String username,
            String firstName,
            String gender,
            String height,
            String phone,
            String email,
            String bio) {

        SQLiteDatabase db =
                this.getWritableDatabase();

        ContentValues values =
                new ContentValues();

        values.put(
                PROFILE_USERNAME,
                username
        );

        values.put(
                FIRST_NAME,
                firstName
        );

        values.put(
                GENDER,
                gender
        );

        values.put(
                HEIGHT,
                height
        );

        values.put(
                PHONE,
                phone
        );

        values.put(
                EMAIL,
                email
        );

        values.put(
                BIO,
                bio
        );

        long result =
                db.insertWithOnConflict(
                        TABLE_PROFILE,
                        null,
                        values,
                        SQLiteDatabase.CONFLICT_REPLACE
                );

        return result != -1;
    }

    public Cursor getProfile(
            String username) {

        SQLiteDatabase db =
                this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM " +
                        TABLE_PROFILE +
                        " WHERE " +
                        PROFILE_USERNAME +
                        " = ?",
                new String[]{
                        username
                }
        );
    }

    // ============================================================
    // GOAL WEIGHT
    // ============================================================

    public double getGoalWeight(
            String username) {

        SQLiteDatabase db =
                this.getReadableDatabase();

        Cursor cursor =
                db.rawQuery(
                        "SELECT " +
                                GOAL_WEIGHT +
                                " FROM " +
                                TABLE_PROFILE +
                                " WHERE " +
                                PROFILE_USERNAME +
                                " = ?",
                        new String[]{
                                username
                        }
                );

        if (!cursor.moveToFirst()) {

            cursor.close();

            return 150.0;
        }

        double goalWeight =
                cursor.getDouble(
                        cursor.getColumnIndexOrThrow(
                                GOAL_WEIGHT
                        )
                );

        cursor.close();

        return goalWeight;
    }

    public boolean saveGoalWeight(
            String username,
            double goalWeight) {

        SQLiteDatabase db =
                this.getWritableDatabase();

        ContentValues values =
                new ContentValues();

        values.put(
                GOAL_WEIGHT,
                goalWeight
        );

        int result =
                db.update(
                        TABLE_PROFILE,
                        values,
                        PROFILE_USERNAME +
                                " = ?",
                        new String[]{
                                username
                        }
                );

        return result > 0;
    }
}