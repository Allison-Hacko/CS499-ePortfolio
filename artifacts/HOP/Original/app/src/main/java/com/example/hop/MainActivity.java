package com.example.hop;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * Handles the HOP login screen and account creation.
 */
public class MainActivity extends AppCompatActivity {

    private EditText editUsername;
    private EditText editPassword;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {

                    Insets systemBars = insets.getInsets(
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

        // Connect the login fields.
        editUsername = findViewById(R.id.editUsername);
        editPassword = findViewById(R.id.editPassword);

        Button buttonLogin = findViewById(R.id.buttonLogin);
        Button buttonCreateAccount = findViewById(R.id.buttonCreateAccount);

        // Create the database helper.
        databaseHelper = new DatabaseHelper(this);

        // Login button.
        buttonLogin.setOnClickListener(v -> loginUser());

        // Create account button.
        buttonCreateAccount.setOnClickListener(v -> createAccount());
    }

    /**
     * Attempts to log the user into HOP.
     */
    private void loginUser() {

        String username = editUsername.getText().toString().trim();
        String password = editPassword.getText().toString();

        if (username.isEmpty() || password.isEmpty()) {

            Toast.makeText(
                    this,
                    "Please enter your username and password.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // Check the username and password against the database.
        if (databaseHelper.checkUser(username, password)) {

            Toast.makeText(
                    this,
                    "Login successful!",
                    Toast.LENGTH_SHORT
            ).show();

            /*
             * Send the logged-in username to the rest of the app.
             * This lets the other screens know which user's
             * information should be displayed.
             */
            Intent intent = new Intent(
                    MainActivity.this,
                    WeightHistoryActivity.class
            );

            intent.putExtra("username", username);

            startActivity(intent);

            // Prevent returning to the login screen with the back button.
            finish();

        } else {

            Toast.makeText(
                    this,
                    "Incorrect username or password.",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    /**
     * Creates a new HOP account.
     */
    private void createAccount() {

        String username = editUsername.getText().toString().trim();
        String password = editPassword.getText().toString();

        if (username.isEmpty() || password.isEmpty()) {

            Toast.makeText(
                    this,
                    "Please enter a username and password.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // Require a minimum password length.
        if (password.length() < 8) {

            Toast.makeText(
                    this,
                    "Password must be at least 8 characters.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // Try to create the account.
        boolean accountCreated =
                databaseHelper.addUser(username, password);

        if (accountCreated) {

            Toast.makeText(
                    this,
                    "Account created! You can now log in.",
                    Toast.LENGTH_LONG
            ).show();

            // Clear the password after account creation.
            editPassword.setText("");

        } else {

            Toast.makeText(
                    this,
                    "That username is already in use.",
                    Toast.LENGTH_LONG
            ).show();
        }
    }
}