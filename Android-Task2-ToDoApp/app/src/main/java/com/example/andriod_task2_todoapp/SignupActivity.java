package com.example.andriod_task2_todoapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SignupActivity extends AppCompatActivity {

    EditText etName, etEmail, etPassword, etConfirmPassword;
    Button btnSignup;
    TextView tvLogin;

    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        btnSignup = findViewById(R.id.btnSignup);
        tvLogin = findViewById(R.id.tvLogin);

        databaseHelper = new DatabaseHelper(this);

        btnSignup.setOnClickListener(v -> registerUser());

        tvLogin.setOnClickListener(v -> {
            Intent intent = new Intent(SignupActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void registerUser() {

        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();

        // Check empty fields
        if (name.isEmpty() || email.isEmpty()
                || password.isEmpty() || confirmPassword.isEmpty()) {

            Toast.makeText(this,
                    "Please fill all fields",
                    Toast.LENGTH_SHORT).show();

            return;
        }

        // Check email
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

            Toast.makeText(this,
                    "Enter a valid email address",
                    Toast.LENGTH_SHORT).show();

            return;
        }

        // Check password length
        if (password.length() < 6) {

            Toast.makeText(this,
                    "Password must contain at least 6 characters",
                    Toast.LENGTH_SHORT).show();

            return;
        }

        // Check passwords match
        if (!password.equals(confirmPassword)) {

            Toast.makeText(this,
                    "Passwords do not match",
                    Toast.LENGTH_SHORT).show();

            return;
        }

        // Hash password
        String hashedPassword = hashPassword(password);

        if (hashedPassword == null) {

            Toast.makeText(this,
                    "Password processing failed",
                    Toast.LENGTH_SHORT).show();

            return;
        }

        // Save user
        boolean registered = databaseHelper.registerUser(
                name,
                email,
                hashedPassword
        );

        if (registered) {

            Toast.makeText(this,
                    "Registration successful",
                    Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(
                    SignupActivity.this,
                    MainActivity.class
            );

            startActivity(intent);
            finish();

        } else {

            Toast.makeText(this,
                    "Email already registered",
                    Toast.LENGTH_SHORT).show();
        }
    }

    // SHA-256 password hashing
    private String hashPassword(String password) {

        try {

            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash =
                    digest.digest(
                            password.getBytes(StandardCharsets.UTF_8)
                    );

            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {

                String hex =
                        Integer.toHexString(0xff & b);

                if (hex.length() == 1) {
                    hexString.append('0');
                }

                hexString.append(hex);
            }

            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {

            return null;
        }
    }
}