package com.example.theagora;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    private EditText firstName;
    private EditText surname;
    private EditText email;
    private EditText phoneNum;
    private EditText passwordEditText;
    private EditText checkPassword;
    private boolean isPasswordVisible1 = false;
    private boolean isPasswordVisible2 = false;
    private UserService userService;

    private TextView first_name_star;
    private TextView surname_star;
    private TextView email_star;
    private TextView phone_star;
    private TextView password_star;
    private TextView confirm_password_star;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firstName = findViewById(R.id.name_EditText);
        surname = findViewById(R.id.surname_EditText);
        email = findViewById(R.id.email_EditText);
        phoneNum = findViewById(R.id.phoneNumber_EditText);
        passwordEditText = findViewById(R.id.passwordRegister);
        checkPassword = findViewById(R.id.confirmPassword);

        first_name_star = findViewById(R.id.first_name_star);
        first_name_star.setVisibility(View.GONE);
        surname_star = findViewById(R.id.surname_star);
        surname_star.setVisibility(View.GONE);
        email_star = findViewById(R.id.email_star);
        email_star.setVisibility(View.GONE);
        phone_star = findViewById(R.id.phone_star);
        phone_star.setVisibility(View.GONE);
        password_star = findViewById(R.id.password_star);
        password_star.setVisibility(View.GONE);
        confirm_password_star = findViewById(R.id.confirm_password_star);
        password_star.setVisibility(View.GONE);
        confirm_password_star.setVisibility(View.GONE);

        ConApi conApi = new ConApi();
        userService = conApi.getUserService();

        passwordEditText.setOnTouchListener((v, event) -> {
            final int DRAWABLE_END = 2;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (passwordEditText.getRight() - passwordEditText.getCompoundDrawables()[DRAWABLE_END].getBounds().width())) {
                    togglePasswordVisibility(passwordEditText, isPasswordVisible1);
                    isPasswordVisible1 = !isPasswordVisible1;
                    return true;
                }
            }
            return false;
        });

        checkPassword.setOnTouchListener((v, event) -> {
            final int DRAWABLE_END = 2;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (checkPassword.getRight() - checkPassword.getCompoundDrawables()[DRAWABLE_END].getBounds().width())) {
                    togglePasswordVisibility(checkPassword, isPasswordVisible2);
                    isPasswordVisible2 = !isPasswordVisible2;
                    return true;
                }
            }
            return false;
        });

        Button b = findViewById(R.id.register_button);
        b.setOnClickListener(v -> {
            String firstNameStr = firstName.getText().toString();
            String surnameStr = surname.getText().toString();
            String emailStr = email.getText().toString();
            String phoneNumStr = phoneNum.getText().toString();
            String passwordStr = passwordEditText.getText().toString();
            String confirmPasswordStr = checkPassword.getText().toString();

            if (fieldsAreEmpty()) {
                Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (passwordStr.equals(confirmPasswordStr)) {
                boolean isStaffMember = emailStr.charAt(0) != 's';
                registerUser(firstNameStr, surnameStr, emailStr, passwordStr, phoneNumStr, isStaffMember);
            } else {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean fieldsAreEmpty() {
        boolean isEmpty = false;

        if (firstName.getText().toString().trim().isEmpty()) {
            first_name_star.setVisibility(View.VISIBLE);
            isEmpty = true;
        } else {
            first_name_star.setVisibility(View.GONE);
        }

        if (surname.getText().toString().trim().isEmpty()) {
            surname_star.setVisibility(View.VISIBLE);
            isEmpty = true;
        } else {
            surname_star.setVisibility(View.GONE);
        }

        if (email.getText().toString().trim().isEmpty()) {
            email_star.setVisibility(View.VISIBLE);
            isEmpty = true;
        } else {
            email_star.setVisibility(View.GONE);
        }

        if (phoneNum.getText().toString().trim().isEmpty()) {
            phone_star.setVisibility(View.VISIBLE);
            isEmpty = true;
        } else {
            phone_star.setVisibility(View.GONE);
        }

        if (passwordEditText.getText().toString().trim().isEmpty()) {
            password_star.setVisibility(View.VISIBLE);
            isEmpty = true;
        } else {
            password_star.setVisibility(View.GONE);
        }

        if (checkPassword.getText().toString().trim().isEmpty()) {
            confirm_password_star.setVisibility(View.VISIBLE);
            isEmpty = true;
        } else {
            confirm_password_star.setVisibility(View.GONE);
        }

        return isEmpty;
    }

    private void togglePasswordVisibility(EditText editText, boolean isPasswordVisible) {
        if (isPasswordVisible) {
            editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            editText.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.eye_off, 0);
        } else {
            editText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            editText.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.eye_on, 0);
        }
        editText.setSelection(editText.getText().length());
    }

    public void launchLogin(View v) {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
    }

    private void registerUser(String firstName, String surname, String email, String password, String phoneNum, boolean isStaffMember) {
        User newUser = new User(0, firstName, surname, email, password, isStaffMember, phoneNum);
        Call<User> call = userService.register(newUser);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                    User user = response.body();
                    Intent i = new Intent(RegisterActivity.this, MainPageActivity.class);
                    i.putExtra("user", user);
                    startActivity(i);
                } else {
                    Toast.makeText(RegisterActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "API call error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
