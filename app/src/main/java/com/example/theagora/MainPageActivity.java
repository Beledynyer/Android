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
    User user;

    ForumPostAdapter adapter ;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        user = new User(0,"El","Bisho","email","pass",false,"123123");
        TextView userNameView = findViewById(R.id.user_name);
        assert user != null;
        userNameView.setText(user.getfName() + " " + user.getlName());

        RecyclerView recyclerView = findViewById(R.id.mRecycleView);
        adapter = new ForumPostAdapter(this,forumPosts);
        setUpForumPosts();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //load all posts :/


    }
    public void createForumPost(View v){
        Intent i = new Intent(this,CreateForumPostActivity.class);
        i.putExtra("user",user);
        //noinspection deprecation
        startActivityForResult(i,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            ForumPost newPost = data.getParcelableExtra("newPost");
            if (newPost != null) {
                adapter.add(newPost);
            }
        }
    }

    private void setUpForumPosts(){
        //get all forum posts
        adapter.add(new ForumPost(user, user.getId(), "", 0, null, "Funny", "Please Work"));
        adapter.add(new ForumPost(user, user.getId(), "", 0, null, "Funny", "Please Work"));
        adapter.add(new ForumPost(user, user.getId(), "", 0, null, "Funny", "Please Work"));
        adapter.add(new ForumPost(user, user.getId(), "", 0, null, "Funny", "Please Work"));
        adapter.add(new ForumPost(user, user.getId(), "", 0, null, "Funny", "Please Work"));
        adapter.add(new ForumPost(user, user.getId(), "", 0, null, "Funny", "Please Work"));
        adapter.add(new ForumPost(user, user.getId(), "", 0, null, "Funny", "Please Work"));
        adapter.add(new ForumPost(user, user.getId(), "", 0, null, "Funny", "Please Work"));
        adapter.add(new ForumPost(user, user.getId(), "", 0, null, "Funny", "Please Work"));
        adapter.add(new ForumPost(user, user.getId(), "", 0, null, "Funny", "Please Work"));
    }
}