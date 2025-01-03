package com.example.theagora;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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
    private ImageView postImageView;
    private Button banButton, approveButton, backButton;

    ImageView userIcon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_post_status);

        forumPostService = RetrofitClientInstance.getRetrofitInstance().create(ForumPostService.class);

        // Retrieve the postId and User objects from the intent
        postId = getIntent().getIntExtra("forumPostId", -1);
        staffMember = getIntent().getParcelableExtra("user");
        userIcon = findViewById(R.id.user_icon);

        userIcon.setOnClickListener(v -> {
            Intent intent = new Intent(ManagePostStatusActivity.this, LogOutActivity.class);
            intent.putExtra("user", staffMember);
            startActivity(intent);
        });


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
        banButton = findViewById(R.id.ban_button);
        approveButton = findViewById(R.id.approve_button);
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
        banButton.setOnClickListener(v -> showBanDialog());
        approveButton.setOnClickListener(v -> showApproveDialog());
        backButton.setOnClickListener(v -> finish());
    }

    private void showBanDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_buttons, null);

        builder.setTitle("Confirm ban")
                .setMessage("Are you sure you want to ban this post?")
                .setView(dialogView);

        AlertDialog dialog = builder.create();

        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        Button btnBan = dialogView.findViewById(R.id.btn_delete);
        btnBan.setText("Ban");

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnBan.setOnClickListener(v -> {
            banPost();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void showApproveDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_buttons, null);

        builder.setTitle("Confirm approve")
                .setMessage("Are you sure you want to approve this post?")
                .setView(dialogView);

        AlertDialog dialog = builder.create();

        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        Button btnApprove = dialogView.findViewById(R.id.btn_delete);
        btnApprove.setText("Approve");

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnApprove.setOnClickListener(v -> {
            approvePost();
            dialog.dismiss();
        });

        dialog.show();
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
                    ForumPost approvedPost = response.body();
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