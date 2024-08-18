package com.example.theagora;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainPageActivity extends AppCompatActivity {

    ArrayList<ForumPost> forumPosts = new ArrayList<>();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        User user = getIntent().getParcelableExtra("user");
        TextView userNameView = findViewById(R.id.user_name);
        assert user != null;
        userNameView.setText(user.getfName() + " " + user.getlName());

        RecyclerView recyclerView = findViewById(R.id.mRecycleView);
        setUpForumPosts();
        ForumPostAdapter adapter = new ForumPostAdapter(this,forumPosts);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //load all posts :/


    }
    public void createForumPost(View v){
        Intent i = new Intent(this,CreateForumPostActivity.class);
        startActivity(i);
    }

    private void setUpForumPosts(){
        //get all forum posts for specific user

    }
}