package com.example.hop;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class WeightRepository {

    private static final String TABLE_WEIGHTS = "WeightEntries";

    private static final String WEIGHT_ID = "id";
    private static final String WEIGHT_USERNAME = "username";
    private static final String WEIGHT = "weight";
    private static final String DATE = "date";

    private final DatabaseHelper databaseHelper;

    public WeightRepository(Context context) {
        databaseHelper = new DatabaseHelper(context);
    }

    /**
     * Adds a new weight entry for the specified user.
     *
     * @return the new row ID, or -1 if the entry could not be saved.
     */
    public long addWeight(
            String username,
            double weight,
            String date) {

        if (username == null
                || username.trim().isEmpty()
                || weight <= 0
                || date == null
                || date.trim().isEmpty()) {

            return -1;
        }

        ContentValues values =
                new ContentValues();

        values.put(
                WEIGHT_USERNAME,
                username.trim()
        );

        values.put(
                WEIGHT,
                weight
        );

        values.put(
                DATE,
                date.trim()
        );

        SQLiteDatabase db =
                databaseHelper.getWritableDatabase();

        return db.insert(
                TABLE_WEIGHTS,
                null,
                values
        );
    }

    /**
     * Returns all weight entries for a specific user.
     *
     * Entries are sorted newest date first.
     */
    public Cursor getWeights(String username) {

        if (username == null
                || username.trim().isEmpty()) {

            return null;
        }

        SQLiteDatabase db =
                databaseHelper.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM "
                        + TABLE_WEIGHTS
                        + " WHERE "
                        + WEIGHT_USERNAME
                        + " = ? "
                        + "ORDER BY "
                        + "substr("
                        + DATE
                        + ", 7, 4) || "
                        + "substr("
                        + DATE
                        + ", 1, 2) || "
                        + "substr("
                        + DATE
                        + ", 4, 2) DESC, "
                        + WEIGHT_ID
                        + " DESC",
                new String[]{
                        username.trim()
                }
        );
    }

    /**
     * Updates a weight entry only when the entry belongs
     * to the specified user.
     */
    public boolean updateWeight(
            String username,
            int id,
            double weight,
            String date) {

        if (username == null
                || username.trim().isEmpty()
                || id < 1
                || weight <= 0
                || date == null
                || date.trim().isEmpty()) {

            return false;
        }

        ContentValues values =
                new ContentValues();

        values.put(
                WEIGHT,
                weight
        );

        values.put(
                DATE,
                date.trim()
        );

        SQLiteDatabase db =
                databaseHelper.getWritableDatabase();

        int result =
                db.update(
                        TABLE_WEIGHTS,
                        values,
                        WEIGHT_ID
                                + " = ? AND "
                                + WEIGHT_USERNAME
                                + " = ?",
                        new String[]{
                                String.valueOf(id),
                                username.trim()
                        }
                );

        return result > 0;
    }

    /**
     * Deletes a weight entry only when the entry belongs
     * to the specified user.
     */
    public boolean deleteWeight(
            String username,
            int id) {

        if (username == null
                || username.trim().isEmpty()
                || id < 1) {

            return false;
        }

        SQLiteDatabase db =
                databaseHelper.getWritableDatabase();

        int result =
                db.delete(
                        TABLE_WEIGHTS,
                        WEIGHT_ID
                                + " = ? AND "
                                + WEIGHT_USERNAME
                                + " = ?",
                        new String[]{
                                String.valueOf(id),
                                username.trim()
                        }
                );

        return result > 0;
    }
}