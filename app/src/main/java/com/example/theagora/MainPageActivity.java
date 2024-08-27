package com.example.theagora;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
        ForumPostService forumPostService = RetrofitClientInstance.getRetrofitInstance().create(ForumPostService.class);
        UserService userService = RetrofitClientInstance.getRetrofitInstance().create(UserService.class);

        Call<List<ForumPost>> call = forumPostService.getForumPosts();
        call.enqueue(new Callback<List<ForumPost>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<List<ForumPost>> call, @NonNull Response<List<ForumPost>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    forumPosts.clear();
                    forumPosts.addAll(response.body());

                    // Fetch user details for each forum post
                    for (ForumPost post : forumPosts) {
                        int userId = post.getUserId();
                        Call<User> userCall = userService.getUserById(userId);
                        userCall.enqueue(new Callback<User>() {
                            @Override
                            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> userResponse) {
                                if (userResponse.isSuccessful() && userResponse.body() != null) {
                                    post.setUser(userResponse.body());
                                }
                                adapter.notifyDataSetChanged(); // Update the adapter after setting the user
                            }

                            @Override
                            public void onFailure(Call<User> call, Throwable t) {
                                Log.e("MainPageActivity", "Failed to load user details", t);
                            }
                        });
                    }
                } else {
                    Log.e("MainPageActivity", "Failed to load forum posts. Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<ForumPost>> call, Throwable t) {
                Log.e("MainPageActivity", "Failed to load forum posts", t);
            }
        });
    }

}
