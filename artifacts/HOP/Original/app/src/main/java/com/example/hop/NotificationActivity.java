package com.example.hop;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * Handles SMS notification settings for HOP.
 *
 * Users can enter a phone number and enable SMS goal notifications.
 * The application requests SMS permission only when the user chooses
 * to enable the feature.
 */
public class NotificationActivity extends AppCompatActivity {

    private static final int SMS_PERMISSION_REQUEST_CODE = 100;

    private String username;

    private Switch switchSmsNotifications;
    private EditText editPhoneNumber;
    private Button buttonEnableSms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notification);

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

        // Get the currently logged-in username.
        username =
                getIntent().getStringExtra("username");

        // Connect UI elements.
        switchSmsNotifications =
                findViewById(
                        R.id.switchSmsNotifications
                );

        editPhoneNumber =
                findViewById(
                        R.id.editPhoneNumber
                );

        buttonEnableSms =
                findViewById(
                        R.id.buttonEnableSms
                );

        // Load previously saved notification settings.
        loadNotificationSettings();

        // Enable SMS notifications.
        buttonEnableSms.setOnClickListener(v -> {

            String phoneNumber =
                    editPhoneNumber
                            .getText()
                            .toString()
                            .trim();

            if (phoneNumber.isEmpty()) {

                editPhoneNumber.setError(
                        "Enter a phone number"
                );

                editPhoneNumber.requestFocus();

                return;
            }

            // Request permission if it has not been granted.
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.SEND_SMS
            ) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                        this,
                        new String[]{
                                Manifest.permission.SEND_SMS
                        },
                        SMS_PERMISSION_REQUEST_CODE
                );

                // Save the number now so it is available
                // if permission is granted.
                saveNotificationSettings(phoneNumber);

            } else {

                enableNotifications(phoneNumber);
            }
        });

        // Allow the switch to disable notifications.
        switchSmsNotifications.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {

                    if (!isChecked) {

                        getSharedPreferences(
                                "HOP_PREFS",
                                MODE_PRIVATE
                        )
                                .edit()
                                .putBoolean(
                                        "smsNotificationsEnabled",
                                        false
                                )
                                .apply();
                    }
                }
        );
    }

    /**
     * Enables SMS notifications after permission is granted.
     */
    private void enableNotifications(
            String phoneNumber) {

        saveNotificationSettings(phoneNumber);

        switchSmsNotifications.setChecked(true);

        Toast.makeText(
                this,
                "SMS notifications enabled!",
                Toast.LENGTH_SHORT
        ).show();
    }

    /**
     * Saves the user's SMS notification settings.
     */
    private void saveNotificationSettings(
            String phoneNumber) {

        getSharedPreferences(
                "HOP_PREFS",
                MODE_PRIVATE
        )
                .edit()
                .putString(
                        "smsPhoneNumber",
                        phoneNumber
                )
                .putBoolean(
                        "smsNotificationsEnabled",
                        true
                )
                .apply();
    }

    /**
     * Loads previously saved notification settings.
     */
    private void loadNotificationSettings() {

        android.content.SharedPreferences preferences =
                getSharedPreferences(
                        "HOP_PREFS",
                        MODE_PRIVATE
                );

        String savedPhone =
                preferences.getString(
                        "smsPhoneNumber",
                        ""
                );

        boolean enabled =
                preferences.getBoolean(
                        "smsNotificationsEnabled",
                        false
                );

        editPhoneNumber.setText(savedPhone);

        switchSmsNotifications.setChecked(
                enabled
        );
    }

    /**
     * Handles the user's SMS permission response.
     */
    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String[] permissions,
            int[] grantResults) {

        super.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
        );

        if (requestCode ==
                SMS_PERMISSION_REQUEST_CODE) {

            if (grantResults.length > 0 &&
                    grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED) {

                String phoneNumber =
                        editPhoneNumber
                                .getText()
                                .toString()
                                .trim();

                enableNotifications(
                        phoneNumber
                );

            } else {

                // The rest of the application continues
                // working even when SMS permission is denied.
                switchSmsNotifications.setChecked(false);

                getSharedPreferences(
                        "HOP_PREFS",
                        MODE_PRIVATE
                )
                        .edit()
                        .putBoolean(
                                "smsNotificationsEnabled",
                                false
                        )
                        .apply();

                Toast.makeText(
                        this,
                        "SMS permission denied. HOP will continue to work without SMS notifications.",
                        Toast.LENGTH_LONG
                ).show();
            }
        }
    }

    /**
     * Sends an SMS goal notification.
     *
     * This method can be called when the user's current
     * weight reaches their goal weight.
     */
    public static void sendGoalReachedNotification(
            android.content.Context context) {

        android.content.SharedPreferences preferences =
                context.getSharedPreferences(
                        "HOP_PREFS",
                        android.content.Context.MODE_PRIVATE
                );

        boolean enabled =
                preferences.getBoolean(
                        "smsNotificationsEnabled",
                        false
                );

        String phoneNumber =
                preferences.getString(
                        "smsPhoneNumber",
                        ""
                );

        if (!enabled ||
                phoneNumber.isEmpty()) {

            return;
        }

        // Permission must have been granted before SMS can be sent.
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.SEND_SMS
        ) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        try {

            SmsManager smsManager =
                    SmsManager.getDefault();

            smsManager.sendTextMessage(
                    phoneNumber,
                    null,
                    "HOP: Congratulations! 🎉 You reached your goal weight!",
                    null,
                    null
            );

        } catch (Exception e) {

            Toast.makeText(
                    context,
                    "Unable to send SMS notification.",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}