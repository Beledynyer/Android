package com.example.theagora;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ViewForumPostActivity extends AppCompatActivity {

    public static Bitmap decodeBase64(String base64String) {
        byte[] bytes=Base64.decode(base64String,Base64.NO_WRAP);
        // Initialize bitmap
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }
    User user;
    UserService userService;
    TextView userName, postTitle, postContent, likeCount,creatorView;
    ImageView postImage;
    FloatingActionButton backFb;
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
        backFb = findViewById(R.id.floatingActionButton2);
        backFb.setOnClickListener(v ->{
            finish();
        });

        Intent userAndPost = getIntent();
        user = userAndPost.getParcelableExtra("user");
        int postId = userAndPost.getIntExtra("forumPostId", -1); // Receive the post ID

        userName.setText(user.getfName() + " " + user.getlName());

        // Fetch the forum post details using the post ID
        ForumPostService forumPostService = RetrofitClientInstance.getRetrofitInstance().create(ForumPostService.class);
        forumPostService.getForumPostById(postId).enqueue(new Callback<ForumPost>() {
            @Override
            public void onResponse(@NonNull Call<ForumPost> call, @NonNull Response<ForumPost> response) {
                if (response.isSuccessful()) {
                    ForumPost post = response.body();
                    if (post != null) {
                        // Set post title and content
                        postTitle.setText(post.getTitle() + ",");
                        postContent.setText(post.getContent());

                        // Set number of likes
                        likeCount.setText(String.valueOf(post.getNumberOfLikes()));

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
                        userService = RetrofitClientInstance.getRetrofitInstance().create(UserService.class);
                        Call<User> callUser = userService.getUserById(post.getUserId());
                        callUser.enqueue(new Callback<User>() {
                            @Override
                            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                                if (response.isSuccessful()) {
                                    User creator = response.body();
                                    if (creator != null) {
                                        if(!post.getTags().equals("Anonymous")){
                                            creatorView.setText(creator.getfName() + " " + creator.getlName());
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                                creatorView.setText("Unknown User");
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ForumPost> call, @NonNull Throwable t) {
                // Handle failure
            }
        });
    }

    public void back(View v){
        finish();
    }
}