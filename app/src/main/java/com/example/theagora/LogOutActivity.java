package com.example.theagora;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class LogOutActivity extends AppCompatActivity {

    private User user;
    private TextView nameSurnameTextView;
    private TextView emailTextView;
    private TextView phoneNumberTextView;
    private Button backButton;
    private Button logoutButton;

    private ImageView userIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_out);

        // Get the User object from the intent
        user = getIntent().getParcelableExtra("user");

        // Initialize views
        nameSurnameTextView = findViewById(R.id.name_surname);
        emailTextView = findViewById(R.id.email);
        phoneNumberTextView = findViewById(R.id.phone_number);
        backButton = findViewById(R.id.back_button);
        logoutButton = findViewById(R.id.logout_button);
        userIcon = findViewById(R.id.user_icon);
        userIcon.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);

        // Populate views with user data
        if (user != null) {
            nameSurnameTextView.setText(user.getfName() + " " + user.getlName());
            emailTextView.setText(user.getEmail());
            phoneNumberTextView.setText(user.getPhoneNumber());
        }

        // Set up button click listeners
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Close this activity and return to the previous one
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform logout operations (e.g., clear session, remove stored credentials)
                // For this example, we'll just navigate to the LoginActivity
                Intent intent = new Intent(LogOutActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }
}