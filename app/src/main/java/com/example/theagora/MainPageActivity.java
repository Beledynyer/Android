package com.example.theagora;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainPageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        User user = getIntent().getParcelableExtra("user");


    }
    public void createForumPost(View v){
        Intent i = new Intent(this,CreateForumPostActivity.class);
        startActivity(i);
    }
}