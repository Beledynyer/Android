package com.example.theagora;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewForumPostActivity extends AppCompatActivity implements CommentDeletionListener{

    public static Bitmap decodeBase64(String base64String) {
        byte[] bytes = Base64.decode(base64String, Base64.NO_WRAP);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    User user;
    UserService userService;
    LikeService likeService;
    TextView userName, postTitle, postContent, likeCount, creatorView, commentCount;
    ImageView postImage, likeIcon, commentIcon;
    FloatingActionButton backFb;
    int postId;
    boolean isLiked = false;

    RecyclerView commentsRecyclerView;
    CommentAdapter commentAdapter;
    boolean isCommentsVisible = false;

    ForumCommentService forumCommentService;
    CommentService commentService;
    List<Comment> comments = new ArrayList<>();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_forum_post);

        // Initialize views
        userName = findViewById(R.id.user_name);
        creatorView = findViewById(R.id.post_creator_name);
        postTitle = findViewById(R.id.post_title);
        postContent = findViewById(R.id.post_content);
        postImage = findViewById(R.id.post_image);
        likeCount = findViewById(R.id.like_counter);
        likeIcon = findViewById(R.id.like_icon);
        commentIcon = findViewById(R.id.comment_icon);
        commentCount = findViewById(R.id.comment_counter);
        backFb = findViewById(R.id.floatingActionButton2);
        backFb.setOnClickListener(v -> finish());

        Intent userAndPost = getIntent();
        user = userAndPost.getParcelableExtra("user");
        postId = userAndPost.getIntExtra("forumPostId", -1);

        userName.setText(user.getfName() + " " + user.getlName());

        // Initialize services
        likeService = RetrofitClientInstance.getRetrofitInstance().create(LikeService.class);
        ForumPostService forumPostService = RetrofitClientInstance.getRetrofitInstance().create(ForumPostService.class);

        // Fetch like count
        fetchLikeCount();

        // Check if user has liked the post
        checkIfLiked();

        // Set up like icon click listener
        likeIcon.setOnClickListener(v -> toggleLike());

        // Fetch the forum post details
        fetchForumPost(forumPostService);

        commentsRecyclerView = findViewById(R.id.comments_recycler_view);
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentAdapter = new CommentAdapter(comments,user,this);
        commentsRecyclerView.setAdapter(commentAdapter);

        // Set up comment icon click listener
        commentIcon.setOnClickListener(v -> toggleComments());

        forumCommentService = RetrofitClientInstance.getRetrofitInstance().create(ForumCommentService.class);
        commentService = RetrofitClientInstance.getRetrofitInstance().create(CommentService.class);
        userService = RetrofitClientInstance.getRetrofitInstance().create(UserService.class);

        fetchComments();
    }

    private void toggleComments() {
        isCommentsVisible = !isCommentsVisible;
        commentsRecyclerView.setVisibility(isCommentsVisible ? View.VISIBLE : View.GONE);
        commentsRecyclerView.setBackgroundColor(getResources().getColor(R.color.nmu_blue) );
        updateCommentIcon();
    }

    private void updateCommentIcon() {
        int color = isCommentsVisible ?
                ContextCompat.getColor(this, R.color.nmu_yellow) :
                ContextCompat.getColor(this, android.R.color.white);
        ImageViewCompat.setImageTintList(commentIcon, ColorStateList.valueOf(color));
    }


    private void fetchLikeCount() {
        likeService.getLikeCount(postId).enqueue(new Callback<LikeCountResponse>() {
            @Override
            public void onResponse(@NonNull Call<LikeCountResponse> call, @NonNull Response<LikeCountResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    likeCount.setText(String.valueOf(response.body().getCount()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<LikeCountResponse> call, @NonNull Throwable t) {
                Toast.makeText(ViewForumPostActivity.this, "Failed to fetch like count", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkIfLiked() {
        likeService.hasUserLikedPost(user.getId(), postId).enqueue(new Callback<HasLikedResponse>() {
            @Override
            public void onResponse(@NonNull Call<HasLikedResponse> call, @NonNull Response<HasLikedResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    isLiked = response.body().hasLiked();
                    updateLikeIcon();
                }
            }

            @Override
            public void onFailure(@NonNull Call<HasLikedResponse> call, @NonNull Throwable t) {
                Toast.makeText(ViewForumPostActivity.this, "Failed to check like status", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void toggleLike() {
        likeService.toggleLike(user.getId(), postId).enqueue(new Callback<LikeResponse>() {
            @Override
            public void onResponse(@NonNull Call<LikeResponse> call, @NonNull Response<LikeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    isLiked = response.body().isLiked();
                    updateLikeIcon();
                    fetchLikeCount();  // Update the like count after toggling
                }
            }

            @Override
            public void onFailure(@NonNull Call<LikeResponse> call, @NonNull Throwable t) {
                Toast.makeText(ViewForumPostActivity.this, "Failed to toggle like", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateLikeIcon() {
        int color = isLiked ?
                ContextCompat.getColor(this, R.color.nmu_yellow) :
                ContextCompat.getColor(this, android.R.color.white);
        ImageViewCompat.setImageTintList(likeIcon, ColorStateList.valueOf(color));
    }

    private void fetchForumPost(ForumPostService forumPostService) {
        forumPostService.getForumPostById(postId).enqueue(new Callback<ForumPost>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<ForumPost> call, @NonNull Response<ForumPost> response) {
                if (response.isSuccessful()) {
                    ForumPost post = response.body();
                    if (post != null) {
                        // Set post title and content
                        postTitle.setText(post.getTitle() + ",");
                        postContent.setText(post.getContent());

                        // Load image if available, else hide the ImageView
                        if (post.getImage() != null && !post.getImage().isEmpty()) {
                            if (!post.getImage().equals("iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/wcAAwAB/TrTKEwAAAAASUVORK5CYII=")) {
                                postImage.setVisibility(View.VISIBLE);
                                Bitmap bitmap = decodeBase64(post.getImage());
                                postImage.setImageBitmap(bitmap);
                            }
                        } else {
                            postImage.setVisibility(View.GONE);
                        }

                        // Fetch and display the creator's name
                        fetchCreatorName(post);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ForumPost> call, @NonNull Throwable t) {
                // Handle failure
                Toast.makeText(ViewForumPostActivity.this, "Failed to fetch forum post", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchCreatorName(ForumPost post) {
        userService = RetrofitClientInstance.getRetrofitInstance().create(UserService.class);
        Call<User> callUser = userService.getUserById(post.getUserId());
        callUser.enqueue(new Callback<User>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful()) {
                    User creator = response.body();
                    if (creator != null) {
                        if (!post.getTags().equals("Anonymous") || creator.getId() == user.getId()) {
                            creatorView.setText(creator.getfName() + " " + creator.getlName());
                        }
                    }
                }
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                creatorView.setText("Unknown User");
            }
        });
    }

    private void fetchComments() {
        forumCommentService.getCommentsForPost(postId).enqueue(new Callback<List<ForumComment>>() {
            @Override
            public void onResponse(@NonNull Call<List<ForumComment>> call, @NonNull Response<List<ForumComment>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ForumComment> forumComments = response.body();
                    for (ForumComment forumComment : forumComments) {
                        fetchCommentDetails(forumComment);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ForumComment>> call, @NonNull Throwable t) {
                Toast.makeText(ViewForumPostActivity.this, "Failed to fetch comments", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchCommentDetails(ForumComment forumComment) {
        commentService.getComment(forumComment.getCommentId()).enqueue(new Callback<Comment>() {
            @Override
            public void onResponse(@NonNull Call<Comment> call, @NonNull Response<Comment> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Comment comment = response.body();
                    fetchCommentUser(comment, forumComment.getUserId());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Comment> call, @NonNull Throwable t) {
                Toast.makeText(ViewForumPostActivity.this, "Failed to fetch comment details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchCommentUser(Comment comment, int userId) {
        userService.getUserById(userId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    comment.setUser(user);
                    comments.add(comment);
                    updateCommentAdapter();
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Toast.makeText(ViewForumPostActivity.this, "Failed to fetch comment user", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateCommentAdapter() {
        runOnUiThread(() -> {
            commentAdapter.setComments(comments);
            commentAdapter.notifyDataSetChanged();
            commentCount.setText(String.valueOf(comments.size()));
        });
    }

    public void back(View v) {
        finish();
    }

    @Override
    public void onCommentDeleted() {
        runOnUiThread(() -> {
            int currentCount = Integer.parseInt(commentCount.getText().toString());
            commentCount.setText(String.valueOf(currentCount - 1));
        });
    }
}