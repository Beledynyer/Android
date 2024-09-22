package com.example.theagora;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManagePostStatusActivity extends AppCompatActivity {

    private User staffMember;
    private ForumPostService forumPostService;
    private int postId;

    private TextView staffNameView, postCreatorNameView, postTitleView, postContentView;
    private ImageView postImageView, banIcon, approveIcon;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_post_status);

        forumPostService = RetrofitClientInstance.getRetrofitInstance().create(ForumPostService.class);

        // Retrieve the postId and User objects from the intent
        postId = getIntent().getIntExtra("forumPostId", -1);
        staffMember = getIntent().getParcelableExtra("user");

        // Initialize UI elements
        initializeViews();

        // Populate UI elements
        populateStaffInfo();

        // Fetch forum post details
        fetchForumPost();

        // Set up click listeners
        setupClickListeners();
    }

    private void initializeViews() {
        staffNameView = findViewById(R.id.user_name);
        postCreatorNameView = findViewById(R.id.post_creator_name);
        postTitleView = findViewById(R.id.post_title);
        postContentView = findViewById(R.id.post_content);
        postImageView = findViewById(R.id.post_image);
        banIcon = findViewById(R.id.ban_icon);
        approveIcon = findViewById(R.id.approve_icon);
        backButton = findViewById(R.id.back_button);
    }

    private void populateStaffInfo() {
        staffNameView.setText(staffMember.getfName() + " " + staffMember.getlName());
    }

    private void fetchForumPost() {
        forumPostService.getForumPostById(postId).enqueue(new Callback<ForumPost>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<ForumPost> call, @NonNull Response<ForumPost> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ForumPost post = response.body();
                    populatePostDetails(post);
                } else {
                    Toast.makeText(ManagePostStatusActivity.this, "Failed to fetch post details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ForumPost> call, @NonNull Throwable t) {
                Toast.makeText(ManagePostStatusActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void populatePostDetails(ForumPost post) {
        postTitleView.setText(post.getTitle());
        postContentView.setText(post.getContent());

        // Fetch and display the creator's name
        fetchCreatorName(post);

        // Load image if available
        if (post.getImage() != null && !post.getImage().isEmpty()) {
            if (!post.getImage().equals("iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/wcAAwAB/TrTKEwAAAAASUVORK5CYII=")) {
                postImageView.setVisibility(View.VISIBLE);
                Bitmap bitmap = ViewForumPostActivity.decodeBase64(post.getImage());
                postImageView.setImageBitmap(bitmap);
            } else {
                postImageView.setVisibility(View.GONE);
            }
        } else {
            postImageView.setVisibility(View.GONE);
        }
    }

    private void fetchCreatorName(ForumPost post) {
        UserService userService = RetrofitClientInstance.getRetrofitInstance().create(UserService.class);
        userService.getUserById(post.getUserId()).enqueue(new Callback<User>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User creator = response.body();
                    if (!post.getTags().equals("Anonymous") || creator.getId() == staffMember.getId()) {
                        postCreatorNameView.setText(creator.getfName() + " " + creator.getlName());
                    } else {
                        postCreatorNameView.setText("####");
                    }
                }
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                postCreatorNameView.setText("Unknown User");
            }
        });
    }

    private void setupClickListeners() {
        banIcon.setOnClickListener(v -> banPost());
        approveIcon.setOnClickListener(v -> approvePost());
        backButton.setOnClickListener(v -> finish());
    }

    private void banPost() {
        forumPostService.deleteForumPost(postId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ManagePostStatusActivity.this, "Post banned successfully", Toast.LENGTH_SHORT).show();
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("position", getIntent().getIntExtra("position", -1));
                    resultIntent.putExtra("isBanned", true);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                } else {
                    Toast.makeText(ManagePostStatusActivity.this, "Failed to ban post", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(ManagePostStatusActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void approvePost() {
        forumPostService.approveForumPost(postId).enqueue(new Callback<ForumPost>() {
            @Override
            public void onResponse(@NonNull Call<ForumPost> call, @NonNull Response<ForumPost> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(ManagePostStatusActivity.this, "Post approved successfully", Toast.LENGTH_SHORT).show();
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("position", getIntent().getIntExtra("position", -1));
                    resultIntent.putExtra("isApproved", true);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                } else {
                    String errorBody = "Unknown error";
                    if (response.errorBody() != null) {
                        try {
                            errorBody = response.errorBody().string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    Log.e("ApprovePost", "Error: " + response.code() + " " + response.message() + "\n" + errorBody);
                    Toast.makeText(ManagePostStatusActivity.this, "Failed to approve post. Error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ForumPost> call, @NonNull Throwable t) {
                Log.e("ApprovePost", "Network error: " + t.getMessage(), t);
                Toast.makeText(ManagePostStatusActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}