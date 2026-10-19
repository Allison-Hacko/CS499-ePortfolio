package com.example.hop;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ProfileRepository {

    private static final String TABLE_PROFILE = "Profile";

    private static final String PROFILE_USERNAME = "username";
    private static final String FIRST_NAME = "first_name";
    private static final String GENDER = "gender";
    private static final String HEIGHT = "height";
    private static final String PHONE = "phone";
    private static final String EMAIL = "email";
    private static final String BIO = "bio";
    private static final String GOAL_WEIGHT = "goal_weight";

    private final DatabaseHelper databaseHelper;

    public ProfileRepository(Context context) {
        databaseHelper = new DatabaseHelper(context);
    }

    public boolean saveProfile(
            String username,
            String firstName,
            String gender,
            String height,
            String phone,
            String email,
            String bio) {

        if (username == null
                || username.trim().isEmpty()
                || firstName == null
                || firstName.trim().isEmpty()) {
            return false;
        }

        ContentValues values = new ContentValues();

        values.put(
                PROFILE_USERNAME,
                username.trim()
        );

        values.put(
                FIRST_NAME,
                firstName.trim()
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

        SQLiteDatabase db =
                databaseHelper.getWritableDatabase();

        int updatedRows = db.update(
                TABLE_PROFILE,
                values,
                PROFILE_USERNAME + " = ?",
                new String[]{
                        username.trim()
                }
        );

        if (updatedRows > 0) {
            return true;
        }

        long result = db.insert(
                TABLE_PROFILE,
                null,
                values
        );

        return result != -1;
    }

    public Cursor getProfile(String username) {

        if (username == null
                || username.trim().isEmpty()) {
            return null;
        }

        SQLiteDatabase db =
                databaseHelper.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM "
                        + TABLE_PROFILE
                        + " WHERE "
                        + PROFILE_USERNAME
                        + " = ?",
                new String[]{
                        username.trim()
                }
        );
    }

    public double getGoalWeight(String username) {

        if (username == null
                || username.trim().isEmpty()) {
            return 150.0;
        }

        SQLiteDatabase db =
                databaseHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT "
                        + GOAL_WEIGHT
                        + " FROM "
                        + TABLE_PROFILE
                        + " WHERE "
                        + PROFILE_USERNAME
                        + " = ?",
                new String[]{
                        username.trim()
                }
        );

        try {

            if (!cursor.moveToFirst()) {
                return 150.0;
            }

            return cursor.getDouble(
                    cursor.getColumnIndexOrThrow(
                            GOAL_WEIGHT
                    )
            );

        } finally {
            cursor.close();
        }
    }

    public boolean saveGoalWeight(
            String username,
            double goalWeight) {

        if (username == null
                || username.trim().isEmpty()
                || goalWeight <= 0) {
            return false;
        }

        SQLiteDatabase db =
                databaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(
                GOAL_WEIGHT,
                goalWeight
        );

        int updatedRows = db.update(
                TABLE_PROFILE,
                values,
                PROFILE_USERNAME + " = ?",
                new String[]{
                        username.trim()
                }
        );

        if (updatedRows > 0) {
            return true;
        }

        ContentValues newProfile =
                new ContentValues();

        newProfile.put(
                PROFILE_USERNAME,
                username.trim()
        );

        newProfile.put(
                FIRST_NAME,
                ""
        );

        newProfile.put(
                GOAL_WEIGHT,
                goalWeight
        );

        long result = db.insert(
                TABLE_PROFILE,
                null,
                newProfile
        );

        return result != -1;
    }
}