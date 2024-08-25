package com.example.theagora;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainPageActivity extends AppCompatActivity {

    ArrayList<ForumPost> forumPosts = new ArrayList<>();
    User user;
    ForumPostAdapter adapter;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        user = getIntent().getParcelableExtra("user");
        TextView userNameView = findViewById(R.id.user_name);
        assert user != null;
        userNameView.setText(user.getfName() + " " + user.getlName());

        RecyclerView recyclerView = findViewById(R.id.mRecycleView);
        adapter = new ForumPostAdapter(this, forumPosts);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load all forum posts
        setUpForumPosts();
    }

    public void createForumPost(View v) {
        Intent i = new Intent(this, CreateForumPostActivity.class);
        i.putExtra("user", user);
        //noinspection deprecation
        startActivityForResult(i, 1);
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

    private void setUpForumPosts() {
        // Get an instance of ForumPostService
        ForumPostService forumPostService = RetrofitClientInstance.getRetrofitInstance().create(ForumPostService.class);

        // Make the API call to get all forum posts
        Call<List<ForumPost>> call = forumPostService.getForumPosts();
        call.enqueue(new Callback<List<ForumPost>>() {
            @Override
            public void onResponse(Call<List<ForumPost>> call, Response<List<ForumPost>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Clear the existing list and add the new posts
                    forumPosts.clear();
                    forumPosts.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    // Handle the case where the response is not successful
                    Log.e("MainPageActivity", "Failed to load forum posts. Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<ForumPost>> call, Throwable t) {
                // Handle the failure case, such as network errors
                Log.e("MainPageActivity", "Failed to load forum posts", t);
            }
        });
    }
}
