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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class RegisterActivity extends AppCompatActivity {
    EditText firstName;
    EditText surname;
    EditText email;
    EditText phoneNum;
    EditText passwordEditText;
    EditText checkPassword;
    private boolean isPasswordVisible1 = false;
    private boolean isPasswordVisible2 = false;
    private Executor executor = Executors.newSingleThreadExecutor();
    private Handler handler = new Handler(Looper.getMainLooper());

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
        editText.setSelection(editText.getText().length()); // Move cursor to end of text
    }

    public void launchLogin(View v) {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
    }

    private void registerUser(String firstName, String surname, String email, String password, String phoneNum, boolean isStaffMember) {
        executor.execute(() -> {
            boolean result = false;
            User newUser = null;
            try {
                ConSql c = new ConSql();
                Connection connection = c.conclass(); // Use your method to get a SQL Connection
                String sql = "INSERT INTO User (FName, LName, Email, Password, isStaffMember, PhoneNumber) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setString(1, firstName);
                preparedStatement.setString(2, surname);
                preparedStatement.setString(3, email);
                preparedStatement.setString(4, password);
                preparedStatement.setBoolean(5, isStaffMember);
                preparedStatement.setString(6, phoneNum);
                int rowsInserted = preparedStatement.executeUpdate();
                if (rowsInserted > 0) {
                    ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        int userId = generatedKeys.getInt(1);
                        newUser = new User(userId, firstName, surname, email, password, isStaffMember, phoneNum);
                    }
                    result = true;
                }
                preparedStatement.close();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            boolean finalResult = result;
            User finalNewUser = newUser;
            handler.post(() -> {
                if (finalResult) {
                    Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(this, MainPageActivity.class);
                    i.putExtra("user", finalNewUser);
                    startActivity(i);
                } else {
                    Toast.makeText(RegisterActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
