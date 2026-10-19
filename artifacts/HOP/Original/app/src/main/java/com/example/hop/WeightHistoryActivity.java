package com.example.hop;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class WeightHistoryActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;

    private LinearLayout weightEntriesContainer;

    private TextView textCurrentWeight;
    private TextView textProgressAmount;

    private ProgressBar progressWeight;

    private EditText editGoalWeight;
    private Button buttonSaveGoal;

    private String username;

    private double goalWeight = 150.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_weight_history);

        // =========================================================
        // GET USERNAME
        // =========================================================

        username = getIntent().getStringExtra("username");

        if (username == null || username.isEmpty()) {

            android.content.SharedPreferences preferences =
                    getSharedPreferences(
                            "HOP_PREFS",
                            MODE_PRIVATE
                    );

            username = preferences.getString(
                    "username",
                    ""
            );
        }

        // =========================================================
        // DATABASE
        // =========================================================

        databaseHelper = new DatabaseHelper(this);

        // =========================================================
        // CONNECT VIEWS
        // =========================================================

        textCurrentWeight =
                findViewById(R.id.textCurrentWeight);

        textProgressAmount =
                findViewById(R.id.textProgressAmount);

        progressWeight =
                findViewById(R.id.progressWeight);

        editGoalWeight =
                findViewById(R.id.editGoalWeight);

        buttonSaveGoal =
                findViewById(R.id.buttonSaveGoal);

        weightEntriesContainer =
                findViewById(R.id.weightEntriesContainer);

        Button buttonAddWeight =
                findViewById(R.id.buttonAddWeight);

        // =========================================================
        // EDGE-TO-EDGE PADDING
        // =========================================================

        View mainView =
                findViewById(R.id.main);

        if (mainView != null) {

            ViewCompat.setOnApplyWindowInsetsListener(
                    mainView,
                    (v, insets) -> {

                        Insets systemBars =
                                insets.getInsets(
                                        WindowInsetsCompat.Type.systemBars()
                                );

                        v.setPadding(
                                systemBars.left,
                                systemBars.top,
                                systemBars.right,
                                systemBars.bottom
                        );

                        return insets;
                    }
            );
        }

        // =========================================================
        // LOAD SAVED GOAL
        // =========================================================

        loadSavedGoal();

        // =========================================================
        // SAVE GOAL
        // =========================================================

        buttonSaveGoal.setOnClickListener(
                v -> saveGoal()
        );

        // =========================================================
        // ADD WEIGHT
        // =========================================================

        buttonAddWeight.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            WeightHistoryActivity.this,
                            AddWeightActivity.class
                    );

            intent.putExtra(
                    "username",
                    username
            );

            startActivity(intent);
        });

        // =========================================================
        // NOTIFICATIONS
        // =========================================================

        TextView notificationButton =
                findViewById(R.id.textNotifications);

        notificationButton.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            WeightHistoryActivity.this,
                            NotificationActivity.class
                    );

            intent.putExtra(
                    "username",
                    username
            );

            startActivity(intent);
        });

        // =========================================================
        // PROFILE
        // =========================================================

        TextView profileButton =
                findViewById(R.id.textProfile);

        profileButton.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            WeightHistoryActivity.this,
                            ProfileActivity.class
                    );

            intent.putExtra(
                    "username",
                    username
            );

            startActivity(intent);
        });

        // =========================================================
        // BOTTOM NAVIGATION
        // =========================================================

        setupBottomNavigation();

        // =========================================================
        // LOAD WEIGHT HISTORY
        // =========================================================

        loadWeightEntries();
    }

    // =============================================================
    // GOAL WEIGHT
    // =============================================================

    private void loadSavedGoal() {

        goalWeight =
                databaseHelper.getGoalWeight(username);

        editGoalWeight.setText(
                String.format(
                        Locale.US,
                        "%.1f",
                        goalWeight
                )
        );
    }

    private void saveGoal() {

        String goalText =
                editGoalWeight
                        .getText()
                        .toString()
                        .trim();

        if (goalText.isEmpty()) {

            editGoalWeight.setError(
                    "Enter a goal weight"
            );

            editGoalWeight.requestFocus();

            return;
        }

        double newGoal;

        try {

            newGoal =
                    Double.parseDouble(goalText);

        } catch (NumberFormatException e) {

            editGoalWeight.setError(
                    "Enter a valid number"
            );

            editGoalWeight.requestFocus();

            return;
        }

        if (newGoal <= 0) {

            editGoalWeight.setError(
                    "Enter a valid weight"
            );

            editGoalWeight.requestFocus();

            return;
        }

        goalWeight = newGoal;

        ContentValues values =
                new ContentValues();

        values.put(
                "goal_weight",
                goalWeight
        );

        int updated =
                databaseHelper
                        .getWritableDatabase()
                        .update(
                                "Profile",
                                values,
                                "username = ?",
                                new String[]{username}
                        );

        if (updated == 0) {

            ContentValues profileValues =
                    new ContentValues();

            profileValues.put(
                    "username",
                    username
            );

            profileValues.put(
                    "first_name",
                    ""
            );

            profileValues.put(
                    "goal_weight",
                    goalWeight
            );

            databaseHelper
                    .getWritableDatabase()
                    .insertWithOnConflict(
                            "Profile",
                            null,
                            profileValues,
                            SQLiteDatabase.CONFLICT_REPLACE
                    );
        }

        Toast.makeText(
                this,
                "Goal weight saved!",
                Toast.LENGTH_SHORT
        ).show();

        loadWeightEntries();
    }

    // =============================================================
    // WEIGHT HISTORY
    // =============================================================

    private void loadWeightEntries() {

        weightEntriesContainer.removeAllViews();

        Cursor cursor =
                databaseHelper.getWeights(username);

        if (cursor == null) {

            showNoWeightData();

            return;
        }

        if (!cursor.moveToFirst()) {

            cursor.close();

            showNoWeightData();

            return;
        }

        /*
         * Entries are sorted by date with the newest date first.
         * Therefore, the first entry is the current weight.
         */
        double currentWeight =
                cursor.getDouble(
                        cursor.getColumnIndexOrThrow(
                                "weight"
                        )
                );

        double startingWeight =
                currentWeight;

        do {

            int id =
                    cursor.getInt(
                            cursor.getColumnIndexOrThrow(
                                    "id"
                            )
                    );

            double weight =
                    cursor.getDouble(
                            cursor.getColumnIndexOrThrow(
                                    "weight"
                            )
                    );

            String date =
                    cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    "date"
                            )
                    );

            addWeightRow(
                    id,
                    weight,
                    date
            );

            startingWeight = weight;

        } while (cursor.moveToNext());

        cursor.close();

        updateProgress(
                startingWeight,
                currentWeight
        );
    }

    // =============================================================
    // WEIGHT ROW
    // =============================================================

    private void addWeightRow(
            int id,
            double weight,
            String date) {

        LinearLayout row =
                new LinearLayout(this);

        row.setOrientation(
                LinearLayout.HORIZONTAL
        );

        row.setGravity(
                Gravity.CENTER_VERTICAL
        );

        row.setPadding(
                10,
                10,
                10,
                10
        );

        row.setBackgroundResource(
                R.drawable.input_background
        );

        LinearLayout.LayoutParams rowParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        rowParams.setMargins(
                0,
                0,
                0,
                8
        );

        row.setLayoutParams(rowParams);

        // ---------------------------------------------------------
        // DATE
        // ---------------------------------------------------------

        TextView dateText =
                new TextView(this);

        dateText.setText(date);
        dateText.setTextColor(
                getResources().getColor(
                        android.R.color.black
                )
        );
        dateText.setTextSize(14);

        LinearLayout.LayoutParams dateParams =
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                );

        dateText.setLayoutParams(dateParams);

        // ---------------------------------------------------------
        // WEIGHT
        // ---------------------------------------------------------

        TextView weightText =
                new TextView(this);

        weightText.setText(
                String.format(
                        Locale.US,
                        "%.1f lb",
                        weight
                )
        );

        weightText.setTextColor(
                getResources().getColor(
                        android.R.color.black
                )
        );

        weightText.setTextSize(14);

        LinearLayout.LayoutParams weightParams =
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                );

        weightText.setLayoutParams(weightParams);

        // ---------------------------------------------------------
        // EDIT BUTTON
        // ---------------------------------------------------------

        Button editButton =
                new Button(this);

        editButton.setText("EDIT");
        editButton.setTextSize(11);
        editButton.setTextColor(
                getResources().getColor(
                        android.R.color.white
                )
        );
        editButton.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(
                        android.graphics.Color.rgb(
                                217,
                                111,
                                145
                        )
                )
        );

        LinearLayout.LayoutParams editParams =
                new LinearLayout.LayoutParams(
                        70,
                        48
                );

        editParams.setMargins(
                4,
                0,
                4,
                0
        );

        editButton.setLayoutParams(editParams);

        editButton.setOnClickListener(v ->
                showEditWeightDialog(
                        id,
                        weight,
                        date
                )
        );

        // ---------------------------------------------------------
        // DELETE BUTTON
        // ---------------------------------------------------------

        TextView deleteButton =
                new TextView(this);

        deleteButton.setText("🗑");
        deleteButton.setTextSize(18);
        deleteButton.setGravity(
                Gravity.CENTER
        );

        LinearLayout.LayoutParams deleteParams =
                new LinearLayout.LayoutParams(
                        40,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        deleteButton.setLayoutParams(deleteParams);

        deleteButton.setOnClickListener(v -> {

            boolean deleted =
                    databaseHelper.deleteWeight(id);

            if (deleted) {

                Toast.makeText(
                        WeightHistoryActivity.this,
                        "Weight entry deleted.",
                        Toast.LENGTH_SHORT
                ).show();

                loadWeightEntries();

            } else {

                Toast.makeText(
                        WeightHistoryActivity.this,
                        "Unable to delete entry.",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });

        row.addView(dateText);
        row.addView(weightText);
        row.addView(editButton);
        row.addView(deleteButton);

        weightEntriesContainer.addView(row);
    }

    // =============================================================
    // EDIT WEIGHT
    // =============================================================

    private void showEditWeightDialog(
            int id,
            double currentWeight,
            String currentDate) {

        LinearLayout layout =
                new LinearLayout(this);

        layout.setOrientation(
                LinearLayout.VERTICAL
        );

        layout.setPadding(
                40,
                10,
                40,
                10
        );

        EditText weightInput =
                new EditText(this);

        weightInput.setHint("Weight");
        weightInput.setInputType(
                android.text.InputType.TYPE_CLASS_NUMBER
                        | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
        );

        weightInput.setText(
                String.format(
                        Locale.US,
                        "%.1f",
                        currentWeight
                )
        );

        layout.addView(weightInput);

        EditText dateInput =
                new EditText(this);

        dateInput.setHint("Date");
        dateInput.setText(currentDate);
        dateInput.setFocusable(false);
        dateInput.setClickable(true);

        dateInput.setOnClickListener(v ->
                showDatePicker(
                        dateInput,
                        currentDate
                )
        );

        layout.addView(dateInput);

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Edit Weight Entry")
                .setView(layout)
                .setNegativeButton(
                        "CANCEL",
                        null
                )
                .setPositiveButton(
                        "SAVE",
                        (dialog, which) -> {

                            String weightText =
                                    weightInput
                                            .getText()
                                            .toString()
                                            .trim();

                            String newDate =
                                    dateInput
                                            .getText()
                                            .toString()
                                            .trim();

                            if (weightText.isEmpty()) {

                                Toast.makeText(
                                        this,
                                        "Enter a weight.",
                                        Toast.LENGTH_SHORT
                                ).show();

                                return;
                            }

                            double newWeight;

                            try {

                                newWeight =
                                        Double.parseDouble(
                                                weightText
                                        );

                            } catch (NumberFormatException e) {

                                Toast.makeText(
                                        this,
                                        "Enter a valid weight.",
                                        Toast.LENGTH_SHORT
                                ).show();

                                return;
                            }

                            if (newWeight <= 0) {

                                Toast.makeText(
                                        this,
                                        "Enter a valid weight.",
                                        Toast.LENGTH_SHORT
                                ).show();

                                return;
                            }

                            if (newDate.isEmpty()) {

                                Toast.makeText(
                                        this,
                                        "Enter a date.",
                                        Toast.LENGTH_SHORT
                                ).show();

                                return;
                            }

                            boolean updated =
                                    databaseHelper.updateWeight(
                                            id,
                                            newWeight,
                                            newDate
                                    );

                            if (updated) {

                                Toast.makeText(
                                        this,
                                        "Weight entry updated!",
                                        Toast.LENGTH_SHORT
                                ).show();

                                loadWeightEntries();

                            } else {

                                Toast.makeText(
                                        this,
                                        "Unable to update entry.",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }
                )
                .show();
    }

    // =============================================================
    // DATE PICKER
    // =============================================================

    private void showDatePicker(
            EditText dateInput,
            String currentDate) {

        Calendar calendar =
                Calendar.getInstance();

        try {

            SimpleDateFormat format =
                    new SimpleDateFormat(
                            "MM/dd/yyyy",
                            Locale.US
                    );

            calendar.setTime(
                    format.parse(currentDate)
            );

        } catch (Exception ignored) {
        }

        DatePickerDialog dialog =
                new DatePickerDialog(
                        this,
                        (DatePicker view,
                         int year,
                         int month,
                         int dayOfMonth) -> {

                            String selectedDate =
                                    String.format(
                                            Locale.US,
                                            "%02d/%02d/%04d",
                                            month + 1,
                                            dayOfMonth,
                                            year
                                    );

                            dateInput.setText(
                                    selectedDate
                            );
                        },
                        calendar.get(
                                Calendar.YEAR
                        ),
                        calendar.get(
                                Calendar.MONTH
                        ),
                        calendar.get(
                                Calendar.DAY_OF_MONTH
                        )
                );

        dialog.show();
    }

    // =============================================================
    // PROGRESS
    // =============================================================

    private void updateProgress(
            double startingWeight,
            double currentWeight) {

        textCurrentWeight.setText(
                String.format(
                        Locale.US,
                        "%.1f lb",
                        currentWeight
                )
        );

        // Goal reached.
        if (currentWeight <= goalWeight) {

            textProgressAmount.setText(
                    "Goal reached! 🎉"
            );

            progressWeight.setProgress(100);

            NotificationActivity.sendGoalReachedNotification(
                    this
            );

            return;
        }

        double totalToLose =
                startingWeight - goalWeight;

        if (totalToLose <= 0) {

            progressWeight.setProgress(0);

            textProgressAmount.setText(
                    String.format(
                            Locale.US,
                            "%.1f lb to goal",
                            currentWeight - goalWeight
                    )
            );

            return;
        }

        double lostSoFar =
                startingWeight - currentWeight;

        double progress =
                (lostSoFar / totalToLose) * 100.0;

        progress =
                Math.max(
                        0,
                        Math.min(
                                100,
                                progress
                        )
                );

        progressWeight.setProgress(
                (int) Math.round(progress)
        );

        double remaining =
                currentWeight - goalWeight;

        textProgressAmount.setText(
                String.format(
                        Locale.US,
                        "%.1f lb to goal",
                        remaining
                )
        );
    }

    // =============================================================
    // NO WEIGHT DATA
    // =============================================================

    private void showNoWeightData() {

        textCurrentWeight.setText(
                "No entries yet"
        );

        textProgressAmount.setText(
                "Add a weight entry to begin"
        );

        progressWeight.setProgress(0);
    }

    // =============================================================
    // BOTTOM NAVIGATION
    // =============================================================

    private void setupBottomNavigation() {

        findViewById(
                R.id.navProgress
        ).setOnClickListener(v -> {
            // Already on Progress.
        });

        findViewById(
                R.id.navDiary
        ).setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            WeightHistoryActivity.this,
                            DiaryActivity.class
                    );

            intent.putExtra(
                    "username",
                    username
            );

            startActivity(intent);
        });

        findViewById(
                R.id.navAddWeight
        ).setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            WeightHistoryActivity.this,
                            AddWeightActivity.class
                    );

            intent.putExtra(
                    "username",
                    username
            );

            startActivity(intent);
        });

        findViewById(
                R.id.navProfile
        ).setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            WeightHistoryActivity.this,
                            ProfileActivity.class
                    );

            intent.putExtra(
                    "username",
                    username
            );

            startActivity(intent);
        });
    }

    // =============================================================
    // REFRESH SCREEN
    // =============================================================

    @Override
    protected void onResume() {

        super.onResume();

        if (databaseHelper != null) {

            loadSavedGoal();
            loadWeightEntries();
        }
    }
}
