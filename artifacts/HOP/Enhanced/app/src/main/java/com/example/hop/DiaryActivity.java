package com.example.hop;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DiaryActivity extends AppCompatActivity {

    private TextView textDiaryDate;
    private EditText editDiaryEntry;
    private TextView textDiaryStatus;

    private Calendar selectedDate;

    private SharedPreferences diaryPreferences;

    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_diary);

        // ---------------------------------------------------------
        // Edge-to-edge padding
        // ---------------------------------------------------------

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
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

        // ---------------------------------------------------------
        // Get username
        // ---------------------------------------------------------

        username =
                getIntent().getStringExtra("username");

        if (username == null || username.isEmpty()) {

            SharedPreferences preferences =
                    getSharedPreferences(
                            "HOP_PREFS",
                            MODE_PRIVATE
                    );

            username =
                    preferences.getString(
                            "username",
                            ""
                    );
        }

        // ---------------------------------------------------------
        // Diary preferences
        // ---------------------------------------------------------

        diaryPreferences =
                getSharedPreferences(
                        "HOP_DIARY",
                        MODE_PRIVATE
                );

        // ---------------------------------------------------------
        // Find views
        // ---------------------------------------------------------

        textDiaryDate =
                findViewById(R.id.textDiaryDate);

        editDiaryEntry =
                findViewById(R.id.editDiaryEntry);

        textDiaryStatus =
                findViewById(R.id.textDiaryStatus);

        Button buttonPreviousDay =
                findViewById(R.id.buttonPreviousDay);

        Button buttonNextDay =
                findViewById(R.id.buttonNextDay);

        Button buttonSaveDiary =
                findViewById(R.id.buttonSaveDiary);

        // ---------------------------------------------------------
        // Start on today's date
        // ---------------------------------------------------------

        selectedDate =
                Calendar.getInstance();

        updateDate();

        // ---------------------------------------------------------
        // Previous day
        // ---------------------------------------------------------

        buttonPreviousDay.setOnClickListener(v -> {

            saveCurrentEntryIfNeeded();

            selectedDate.add(
                    Calendar.DAY_OF_MONTH,
                    -1
            );

            updateDate();
        });

        // ---------------------------------------------------------
        // Next day
        // ---------------------------------------------------------

        buttonNextDay.setOnClickListener(v -> {

            saveCurrentEntryIfNeeded();

            selectedDate.add(
                    Calendar.DAY_OF_MONTH,
                    1
            );

            updateDate();
        });

        // ---------------------------------------------------------
        // Save diary entry
        // ---------------------------------------------------------

        buttonSaveDiary.setOnClickListener(v -> {

            String entry =
                    editDiaryEntry
                            .getText()
                            .toString()
                            .trim();

            if (entry.isEmpty()) {

                textDiaryStatus.setText(
                        "Please enter something before saving."
                );

                return;
            }

            String key =
                    getDiaryKey();

            diaryPreferences
                    .edit()
                    .putString(
                            key,
                            entry
                    )
                    .apply();

            textDiaryStatus.setText(
                    "Diary entry saved!"
            );
        });

        // ---------------------------------------------------------
        // Notification bell
        // ---------------------------------------------------------

        TextView notificationButton =
                findViewById(
                        R.id.textDiaryNotifications
                );

        notificationButton.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            DiaryActivity.this,
                            NotificationActivity.class
                    );

            intent.putExtra(
                    "username",
                    username
            );

            startActivity(intent);
        });

        // ---------------------------------------------------------
        // Bottom navigation
        // ---------------------------------------------------------

        setupBottomNavigation();
    }

    // =============================================================
    // DATE
    // =============================================================

    private void updateDate() {

        SimpleDateFormat dateFormat =
                new SimpleDateFormat(
                        "MMMM d, yyyy",
                        Locale.getDefault()
                );

        textDiaryDate.setText(
                dateFormat.format(
                        selectedDate.getTime()
                )
        );

        // Load the entry belonging to this specific date.
        loadDiaryEntry();
    }

    // =============================================================
    // DIARY STORAGE
    // =============================================================

    /**
     * Creates a unique key using the logged-in user and
     * the currently selected date.
     *
     * This keeps diary entries separate for every user
     * and every calendar day.
     */
    private String getDiaryKey() {

        SimpleDateFormat keyFormat =
                new SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.US
                );

        String date =
                keyFormat.format(
                        selectedDate.getTime()
                );

        return username + "_" + date;
    }

    /**
     * Loads the diary entry for the currently selected date.
     *
     * If no entry exists for that date, the text box
     * is cleared.
     */
    private void loadDiaryEntry() {

        String savedEntry =
                diaryPreferences.getString(
                        getDiaryKey(),
                        ""
                );

        editDiaryEntry.setText(
                savedEntry
        );

        if (savedEntry.isEmpty()) {

            textDiaryStatus.setText(
                    ""
            );

        } else {

            textDiaryStatus.setText(
                    "Saved entry loaded."
            );
        }
    }

    /**
     * Saves the current text only if the user has entered
     * something. This prevents moving between dates from
     * accidentally overwriting existing entries with blanks.
     */
    private void saveCurrentEntryIfNeeded() {

        String entry =
                editDiaryEntry
                        .getText()
                        .toString()
                        .trim();

        if (!entry.isEmpty()) {

            diaryPreferences
                    .edit()
                    .putString(
                            getDiaryKey(),
                            entry
                    )
                    .apply();
        }
    }

    // =============================================================
    // BOTTOM NAVIGATION
    // =============================================================

    private void setupBottomNavigation() {

        // ---------------------------------------------------------
        // Progress
        // ---------------------------------------------------------

        findViewById(
                R.id.navProgress
        ).setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            DiaryActivity.this,
                            WeightHistoryActivity.class
                    );

            intent.putExtra(
                    "username",
                    username
            );

            startActivity(intent);
        });

        // ---------------------------------------------------------
        // Diary
        // ---------------------------------------------------------

        findViewById(
                R.id.navDiary
        ).setOnClickListener(v -> {
            // Already on the Diary page.
        });

        // ---------------------------------------------------------
        // Add Weight
        // ---------------------------------------------------------

        findViewById(
                R.id.navAddWeight
        ).setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            DiaryActivity.this,
                            AddWeightActivity.class
                    );

            intent.putExtra(
                    "username",
                    username
            );

            startActivity(intent);
        });

        // ---------------------------------------------------------
        // Profile
        // ---------------------------------------------------------

        findViewById(
                R.id.navProfile
        ).setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            DiaryActivity.this,
                            ProfileActivity.class
                    );

            intent.putExtra(
                    "username",
                    username
            );

            startActivity(intent);
        });
    }
}
