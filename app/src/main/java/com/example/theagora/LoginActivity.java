package com.example.theagora;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginActivity extends AppCompatActivity {

    private EditText passwordEditText;
    private EditText emailEditText;
    private boolean isPasswordVisible = false;
    Connection connect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        emailEditText = findViewById(R.id.email_login);
        passwordEditText = findViewById(R.id.passwordUI);

        passwordEditText.setOnTouchListener((v, event) -> {
            final int DRAWABLE_END = 2;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (passwordEditText.getRight() - passwordEditText.getCompoundDrawables()[DRAWABLE_END].getBounds().width())) {
                    togglePasswordVisibility();
                    return true;
                }
            }
            return false;
        });

        Button logInButton = findViewById(R.id.logIn_Button);
        logInButton.setOnClickListener(v -> loginUser());
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            passwordEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.eye_off, 0);
        } else {
            passwordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            passwordEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.eye_on, 0);
        }
        isPasswordVisible = !isPasswordVisible;
        passwordEditText.setSelection(passwordEditText.getText().length()); // Move cursor to end of text
    }

    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        ConSql conSql = new ConSql();
        connect = conSql.conclass();
        if (connect != null) {
            try {
                String query = "SELECT * FROM [User] WHERE email = ? AND password = ?";
                PreparedStatement preparedStatement = connect.prepareStatement(query);
                preparedStatement.setString(1, email);
                preparedStatement.setString(2, password);

                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    // Successfully logged in
                    Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();

                    // Create User object
                    User user = new User(
                            resultSet.getInt("UserID"),
                            resultSet.getString("FName"),
                            resultSet.getString("LName"),
                            resultSet.getString("Email"),
                            resultSet.getString("Password"),
                            resultSet.getInt("isStaffMember") == 1,
                            resultSet.getString("PhoneNumber")
                    );

                    // Pass User object to MainPageActivity
                    Intent i = new Intent(this, MainPageActivity.class);
                    i.putExtra("user", user);
                    startActivity(i);
                } else {
                    // Login failed
                    Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                }

                resultSet.close();
                preparedStatement.close();
            } catch (Exception e) {
                Toast.makeText(this, "Database query error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Connection failed. Please check your internet connection.", Toast.LENGTH_SHORT).show();
        }
    }

    public void registerAccount(View v) {
        Intent i = new Intent(this, RegisterActivity.class);
        startActivity(i);
    }
}
