package com.example.theagora;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText passwordEditText;
    private EditText emailEditText;
    private boolean isPasswordVisible = false;
    private UserService userService;

    private TextView first_name_star;
    private TextView password_star;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        first_name_star = findViewById(R.id.first_name_star);
        password_star = findViewById(R.id.password_star);
        first_name_star.setVisibility(View.GONE); // Hide first name star
        password_star.setVisibility(View.GONE);   // Hide password star

        emailEditText = findViewById(R.id.email_login);
        passwordEditText = findViewById(R.id.passwordUI);

        userService = RetrofitClientInstance.getRetrofitInstance().create(UserService.class);

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
        passwordEditText.setSelection(passwordEditText.getText().length());
    }

    private void loginUser() {
        if (fieldsIsEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        Call<User> call = userService.login(email, password);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                    User user = response.body();
                    Intent i = new Intent(LoginActivity.this, MainPageActivity.class);
                    i.putExtra("user", user);
                    startActivity(i);
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "API call error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("LoginError", t.getMessage());
            }
        });
    }

    private boolean fieldsIsEmpty() {
        boolean isEmpty = false;
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty()) {
            first_name_star.setVisibility(View.VISIBLE); // Show email star if empty
            isEmpty = true;
        } else {
            first_name_star.setVisibility(View.GONE); // Hide email star if not empty
        }

        if (password.isEmpty()) {
            password_star.setVisibility(View.VISIBLE); // Show password star if empty
            isEmpty = true;
        } else {
            password_star.setVisibility(View.GONE); // Hide password star if not empty
        }

        return isEmpty;
    }

    public void registerAccount(View v) {
        Intent i = new Intent(this, RegisterActivity.class);
        startActivity(i);
    }
}
