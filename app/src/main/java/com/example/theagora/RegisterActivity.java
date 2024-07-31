package com.example.theagora;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    EditText firstName;
    EditText surname;
    EditText email;
    EditText phoneNum;
    EditText passwordEditText;
    EditText checkPassword;
    private boolean isPasswordVisible1 = false;
    private boolean isPasswordVisible2 = false;
    private UserService userService;

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

            if (passwordStr.equals(confirmPasswordStr)) {
                boolean isStaffMember = emailStr.charAt(0) == 's';
                registerUser(firstNameStr, surnameStr, emailStr, passwordStr, phoneNumStr, isStaffMember);
            } else {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            }
        });
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
        Call<Integer> call = userService.register(newUser);
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(RegisterActivity.this, MainPageActivity.class);
                    startActivity(i);
                } else {
                    Toast.makeText(RegisterActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "API call error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
