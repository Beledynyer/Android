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

import androidx.appcompat.app.AppCompatActivity;

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
    ForumPost post ;
    UserService userService;
    TextView userName, postTitle, postContent, likeCount,creatorView;
    ImageView postImage;
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

        Intent userAndPost = getIntent();
        user = userAndPost.getParcelableExtra("user");
        post = userAndPost.getParcelableExtra("forumPost");
        userName = findViewById(R.id.user_name);
        userName.setText(user.getfName() + " " + user.getlName());


        post = userAndPost.getParcelableExtra("forumPost");

        // Set post title and content
        postTitle.setText(post.getTitle());
        postContent.setText(post.getContent());

        // Set number of likes
        likeCount.setText(String.valueOf(post.getNumberOfLikes()));

        // Load image if available, else hide the ImageView
        if (post.getImage() != null && !post.getImage().isEmpty()) {
            postImage.setVisibility(View.VISIBLE);
            Bitmap bitmap = decodeBase64(post.getImage());
            postImage.setImageBitmap(bitmap);
        } else {
            postImage.setVisibility(View.GONE);
        }

        // Make API call to get the creator of the post
        userService = RetrofitClientInstance.getRetrofitInstance().create(UserService.class);
        Call<User> call = userService.getUserById(post.getUserId());
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    User creator = response.body();
                    if (creator != null) {
                        // Set the creator's name
                       creatorView.setText(creator.getfName() + " " + creator.getlName());
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                // Handle failure to fetch user
                userName.setText("Unknown User");
            }
        });
    }
    public void back(View v){
        finish();
    }
}