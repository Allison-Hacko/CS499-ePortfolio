package com.example.hop;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ProfileActivity extends AppCompatActivity {

    private EditText editFirstName;
    private EditText editGender;
    private EditText editHeight;
    private EditText editProfilePhone;
    private EditText editProfileEmail;
    private EditText editBio;

    private ProfileRepository profileRepository;

    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_profile);

        // Get username from previous screen.
        username =
                getIntent().getStringExtra("username");

        // If username was not passed, use saved username.
        if (username == null || username.isEmpty()) {

            android.content.SharedPreferences preferences =
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

        // Handle system bar spacing.
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

        profileRepository =
                new ProfileRepository(this);

        // Connect profile fields.
        editFirstName =
                findViewById(R.id.editFirstName);

        editGender =
                findViewById(R.id.editGender);

        editHeight =
                findViewById(R.id.editHeight);

        editProfilePhone =
                findViewById(R.id.editProfilePhone);

        editProfileEmail =
                findViewById(R.id.editProfileEmail);

        editBio =
                findViewById(R.id.editBio);

        // Load previously saved profile information.
        loadProfile();

        // Save profile button.
        Button buttonSaveProfile =
                findViewById(R.id.buttonSaveProfile);

        buttonSaveProfile.setOnClickListener(
                v -> saveProfile()
        );

        // Notification bell.
        TextView notifications =
                findViewById(R.id.textNotifications);

        notifications.setOnClickListener(v -> {

            Intent intent = new Intent(
                    ProfileActivity.this,
                    NotificationActivity.class
            );

            intent.putExtra(
                    "username",
                    username
            );

            startActivity(intent);
        });

        setupBottomNavigation();
    }

    /**
     * Loads the user's saved profile information.
     */
    private void loadProfile() {

        Cursor cursor =
                profileRepository.getProfile(username);

        if (cursor == null) {
            return;
        }

        try {

            if (cursor.moveToFirst()) {

                editFirstName.setText(
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "first_name"
                                )
                        )
                );

                editGender.setText(
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "gender"
                                )
                        )
                );

                editHeight.setText(
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "height"
                                )
                        )
                );

                editProfilePhone.setText(
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "phone"
                                )
                        )
                );

                editProfileEmail.setText(
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "email"
                                )
                        )
                );

                editBio.setText(
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "bio"
                                )
                        )
                );
            }

        } finally {
            cursor.close();
        }
    }

    /**
     * Saves the user's profile information.
     */
    private void saveProfile() {

        String firstName =
                editFirstName.getText()
                        .toString()
                        .trim();

        String gender =
                editGender.getText()
                        .toString()
                        .trim();

        String height =
                editHeight.getText()
                        .toString()
                        .trim();

        String phone =
                editProfilePhone.getText()
                        .toString()
                        .trim();

        String email =
                editProfileEmail.getText()
                        .toString()
                        .trim();

        String bio =
                editBio.getText()
                        .toString()
                        .trim();

        // First name is required.
        if (firstName.isEmpty()) {

            editFirstName.setError(
                    "First name is required"
            );

            editFirstName.requestFocus();

            Toast.makeText(
                    this,
                    "Please enter your first name.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        boolean saved =
                profileRepository.saveProfile(
                        username,
                        firstName,
                        gender,
                        height,
                        phone,
                        email,
                        bio
                );

        if (saved) {

            Toast.makeText(
                    this,
                    "Profile saved!",
                    Toast.LENGTH_SHORT
            ).show();

        } else {

            Toast.makeText(
                    this,
                    "Unable to save profile.",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    /**
     * Sets up the bottom navigation.
     */
    private void setupBottomNavigation() {

        // Progress.
        findViewById(R.id.navProgress)
                .setOnClickListener(v -> {

                    Intent intent = new Intent(
                            ProfileActivity.this,
                            WeightHistoryActivity.class
                    );

                    intent.putExtra(
                            "username",
                            username
                    );

                    startActivity(intent);
                });

        // Diary.
        findViewById(R.id.navDiary)
                .setOnClickListener(v -> {

                    Intent intent = new Intent(
                            ProfileActivity.this,
                            DiaryActivity.class
                    );

                    intent.putExtra(
                            "username",
                            username
                    );

                    startActivity(intent);
                });

        // Add Weight.
        findViewById(R.id.navAddWeight)
                .setOnClickListener(v -> {

                    Intent intent = new Intent(
                            ProfileActivity.this,
                            AddWeightActivity.class
                    );

                    intent.putExtra(
                            "username",
                            username
                    );

                    startActivity(intent);
                });

        // Profile.
        findViewById(R.id.navProfile)
                .setOnClickListener(v -> {
                    // Already on Profile.
                });
    }
}