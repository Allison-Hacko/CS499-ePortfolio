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
 * Allows the user to add a weight entry to HOP.
 */
public class AddWeightActivity extends AppCompatActivity {

    private EditText editWeight;
    private EditText editDate;

    private WeightRepository weightRepository;

    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_weight);

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

        // Retrieve the username of the logged-in user.
        username = getIntent().getStringExtra("username");

        editWeight = findViewById(R.id.editWeight);
        editDate = findViewById(R.id.editDate);

        Button buttonSaveWeight =
                findViewById(R.id.buttonSaveWeight);

        Button buttonCancel =
                findViewById(R.id.buttonCancel);

        weightRepository =
                new WeightRepository(this);

        buttonSaveWeight.setOnClickListener(
                v -> saveWeight()
        );

        buttonCancel.setOnClickListener(
                v -> finish()
        );
    }

    /**
     * Validates the user's information and saves the weight entry.
     */
    private void saveWeight() {

        String weightText =
                editWeight.getText().toString().trim();

        String date =
                editDate.getText().toString().trim();

        // Make sure the required fields are completed.
        if (weightText.isEmpty()) {

            Toast.makeText(
                    this,
                    "Please enter your weight.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (date.isEmpty()) {

            Toast.makeText(
                    this,
                    "Please enter a date.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        double weight;

        // Convert the entered weight into a number.
        try {

            weight =
                    Double.parseDouble(weightText);

        } catch (NumberFormatException e) {

            Toast.makeText(
                    this,
                    "Please enter a valid weight.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // Prevent invalid weight values.
        if (weight <= 0) {

            Toast.makeText(
                    this,
                    "Weight must be greater than zero.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // Save the entry through the repository.
        long result =
                weightRepository.addWeight(
                        username,
                        weight,
                        date
                );

        if (result != -1) {

            Toast.makeText(
                    this,
                    "Weight entry saved!",
                    Toast.LENGTH_SHORT
            ).show();

            // Return to the user's progress screen.
            Intent intent =
                    new Intent(
                            AddWeightActivity.this,
                            WeightHistoryActivity.class
                    );

            intent.putExtra(
                    "username",
                    username
            );

            startActivity(intent);

            finish();

        } else {

            Toast.makeText(
                    this,
                    "Unable to save weight entry.",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}