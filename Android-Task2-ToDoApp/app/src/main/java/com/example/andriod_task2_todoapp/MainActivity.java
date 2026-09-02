package com.example.andriod_task2_todoapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {

    EditText etEmail, etPassword;
    Button btnLogin;
    TextView tvSignup;

    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        boolean isLoggedIn = getSharedPreferences(
                "TodoSession",
                MODE_PRIVATE
        ).getBoolean("isLoggedIn", false);

        if (isLoggedIn) {

            int userId = getSharedPreferences(
                    "TodoSession",
                    MODE_PRIVATE
            ).getInt("USER_ID", -1);

            String userName = getSharedPreferences(
                    "TodoSession",
                    MODE_PRIVATE
            ).getString("USER_NAME", null);

            Intent intent = new Intent(
                    MainActivity.this,
                    TodoActivity.class
            );

            intent.putExtra("USER_ID", userId);
            intent.putExtra("USER_NAME", userName);

            startActivity(intent);
            finish();

            return;
        }

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        btnLogin = findViewById(R.id.btnLogin);
        tvSignup = findViewById(R.id.tvSignup);

        databaseHelper = new DatabaseHelper(this);

        btnLogin.setOnClickListener(v -> loginUser());

        tvSignup.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    SignupActivity.class
            );

            startActivity(intent);
        });
    }

    private void loginUser() {

        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();

        // Validate empty fields
        if (email.isEmpty() || password.isEmpty()) {

            Toast.makeText(
                    this,
                    "Please enter email and password",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // Hash entered password
        String hashedPassword = hashPassword(password);

        if (hashedPassword == null) {

            Toast.makeText(
                    this,
                    "Password processing failed",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // Check database
        Cursor cursor = databaseHelper.loginUser(
                email,
                hashedPassword
        );

        if (cursor.moveToFirst()) {

            int userId = cursor.getInt(
                    cursor.getColumnIndexOrThrow("id")
            );

            String name = cursor.getString(
                    cursor.getColumnIndexOrThrow("name")
            );

            Toast.makeText(
                    this,
                    "Welcome " + name,
                    Toast.LENGTH_SHORT
            ).show();
            getSharedPreferences(
                    "TodoSession",
                    MODE_PRIVATE
            ).edit()
                    .putBoolean("isLoggedIn", true)
                    .putInt("USER_ID", userId)
                    .putString("USER_NAME", name)
                    .apply();
            // We'll use these values for the To-Do screen
            Intent intent = new Intent(
                    MainActivity.this,
                    TodoActivity.class
            );
            intent.putExtra("USER_ID", userId);
            intent.putExtra("USER_NAME", name);
            startActivity(intent);
            finish();

        } else {

            Toast.makeText(
                    this,
                    "Invalid email or password",
                    Toast.LENGTH_SHORT
            ).show();
        }

        cursor.close();
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