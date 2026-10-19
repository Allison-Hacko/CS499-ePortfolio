package com.example.hop;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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

    public DatabaseHelper(Context context) {
        super(
                context,
                DATABASE_NAME,
                null,
                DATABASE_VERSION
        );
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
}