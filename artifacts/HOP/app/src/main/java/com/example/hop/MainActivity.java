package com.example.hop;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText editUsername;
    private EditText editPassword;

    private UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editUsername = findViewById(R.id.editUsername);
        editPassword = findViewById(R.id.editPassword);

        userRepository = new UserRepository(this);

        // Password visibility button.
        ImageButton buttonPasswordVisibility =
                findViewById(R.id.buttonPasswordVisibility);

        final boolean[] passwordVisible = {false};

        buttonPasswordVisibility.setOnClickListener(v -> {

            passwordVisible[0] = !passwordVisible[0];

            if (passwordVisible[0]) {

                editPassword.setInputType(
                        InputType.TYPE_CLASS_TEXT
                                | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                );

                buttonPasswordVisibility.setContentDescription(
                        "Hide password"
                );

            } else {

                editPassword.setInputType(
                        InputType.TYPE_CLASS_TEXT
                                | InputType.TYPE_TEXT_VARIATION_PASSWORD
                );

                buttonPasswordVisibility.setContentDescription(
                        "Show password"
                );
            }

            // Keep the cursor at the end of the password.
            editPassword.setSelection(
                    editPassword.getText().length()
            );
        });

        Button loginButton =
                findViewById(R.id.buttonLogin);

        Button createAccountButton =
                findViewById(R.id.buttonCreateAccount);

        loginButton.setOnClickListener(
                v -> loginUser()
        );

        createAccountButton.setOnClickListener(
                v -> createAccount()
        );
    }

    private void loginUser() {

        String username =
                editUsername.getText()
                        .toString()
                        .trim();

        String password =
                editPassword.getText()
                        .toString();

        if (username.isEmpty()
                || password.isEmpty()) {

            Toast.makeText(
                    this,
                    "Please enter your username and password.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        boolean validUser =
                userRepository.checkUser(
                        username,
                        password
                );

        if (validUser) {

            Intent intent =
                    new Intent(
                            MainActivity.this,
                            WeightHistoryActivity.class
                    );

            intent.putExtra(
                    "username",
                    username
            );

            startActivity(intent);

        } else {

            Toast.makeText(
                    this,
                    "Invalid username or password.",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private void createAccount() {

        String username =
                editUsername.getText()
                        .toString()
                        .trim();

        String password =
                editPassword.getText()
                        .toString();

        if (username.isEmpty()
                || password.isEmpty()) {

            Toast.makeText(
                    this,
                    "Please enter your username and password.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (password.length() < 8) {

            Toast.makeText(
                    this,
                    "Password must be at least 8 characters.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        boolean accountCreated =
                userRepository.addUser(
                        username,
                        password
                );

        if (accountCreated) {

            Toast.makeText(
                    this,
                    "Account created successfully!",
                    Toast.LENGTH_SHORT
            ).show();

        } else {

            Toast.makeText(
                    this,
                    "Could not create account. Username may already exist.",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}