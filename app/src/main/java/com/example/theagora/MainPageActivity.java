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

import com.google.android.material.floatingactionbutton.FloatingActionButton;

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
        ForumPostService forumPostService = RetrofitClientInstance.getRetrofitInstance().create(ForumPostService.class);
        adapter = new ForumPostAdapter(this, forumPosts,user,forumPostService);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load all forum posts
        setUpForumPosts();
        FloatingActionButton fab;
        fab = findViewById(R.id.floatingActionButton);

        // Hide/Show FloatingActionButton on scroll
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && fab.isShown()) {
                    fab.hide();  // Hide FAB on scroll down
                } else if (dy < 0) {
                    fab.show();  // Show FAB on scroll up
                }
            }
        });
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
                int userId = newPost.getUserId();
                UserService userService = RetrofitClientInstance.getRetrofitInstance().create(UserService.class);
                Call<User> userCall = userService.getUserById(userId);
                userCall.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(@NonNull Call<User> call, @NonNull Response<User> userResponse) {
                        if (userResponse.isSuccessful() && userResponse.body() != null) {
                            newPost.setUser(userResponse.body());
                        }
                        adapter.add(newPost);
                    }
                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Log.e("MainPageActivity", "Failed to load user details", t);
                    }
                });
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
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    forumPosts.clear();
                    forumPosts.addAll(response.body());

                    // Fetch user details for each forum post
                    int totalPosts = forumPosts.size();
                    final int[] loadedPosts = {0};  // track the number of posts that have their users loaded

                    for (int i = 0; i < totalPosts; i++) {
                        int userId = forumPosts.get(i).getUserId();
                        int finalI = i;
                        Call<User> userCall = userService.getUserById(userId);
                        userCall.enqueue(new Callback<User>() {
                            @Override
                            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> userResponse) {
                                if (userResponse.isSuccessful() && userResponse.body() != null) {
                                    forumPosts.get(finalI).setUser(userResponse.body());
                                }
                                loadedPosts[0]++;
                                if (loadedPosts[0] == totalPosts) {
                                    adapter.notifyDataSetChanged();  // Update the adapter after all users are set
                                }
                            }

                            @Override
                            public void onFailure(Call<User> call, Throwable t) {
                                Log.e("MainPageActivity", "Failed to load user details", t);
                                loadedPosts[0]++;
                                if (loadedPosts[0] == totalPosts) {
                                    adapter.notifyDataSetChanged();  // Update the adapter after all users are set
                                }
                            }
                        });
                    }
                } else {
                    Log.e("MainPageActivity", "No forum posts available.");
                }
            }

            @Override
            public void onFailure(Call<List<ForumPost>> call, Throwable t) {
                Log.e("MainPageActivity", "Failed to load forum posts", t);
            }
        });
    }
}
