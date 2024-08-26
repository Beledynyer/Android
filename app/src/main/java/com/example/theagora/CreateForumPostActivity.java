package com.example.theagora;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.VectorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateForumPostActivity extends AppCompatActivity {

    TextView titleStar;
    TextView contextStar;
    TextView tagsStar;
    ImageView im;
    Spinner tagsSpinner;
    private static final int REQUEST_PERMISSIONS_CODE = 100;
    User user;
    byte[] imageByteArray;

    private ForumPostService forumPostService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_forum_post);

        // Check if permission is granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS_CODE);
        }

        tagsSpinner = findViewById(R.id.tags_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.tags_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        tagsSpinner.setAdapter(adapter);
        titleStar = findViewById(R.id.Title_star);
        contextStar = findViewById(R.id.Content_star);
        tagsStar = findViewById(R.id.Tags_star);
        im = findViewById(R.id.imageView);

        // Initially hide the stars
        titleStar.setVisibility(View.GONE);
        contextStar.setVisibility(View.GONE);
        tagsStar.setVisibility(View.GONE);

        Intent userIntent = getIntent();
        user = userIntent.getParcelableExtra("user");

        forumPostService = RetrofitClientInstance.getRetrofitInstance().create(ForumPostService.class);

    }

    private static final int MAX_IMAGE_WIDTH = 800; // Max width for the image
    private static final int MAX_IMAGE_HEIGHT = 800; // Max height for the image
    private static final int COMPRESS_QUALITY = 70; // Quality of the compressed image

    // Method to convert ImageView to byte array with resizing and compression
    private byte[] convertImageViewToByteArray(ImageView imageView) {
        if (imageView.getDrawable() != null) {
            Bitmap bitmap;

            if (imageView.getDrawable() instanceof BitmapDrawable) {
                bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            } else if (imageView.getDrawable() instanceof VectorDrawable) {
                // Convert VectorDrawable to Bitmap
                VectorDrawable vectorDrawable = (VectorDrawable) imageView.getDrawable();
                bitmap = Bitmap.createBitmap(
                        vectorDrawable.getIntrinsicWidth(),
                        vectorDrawable.getIntrinsicHeight(),
                        Bitmap.Config.ARGB_8888
                );
                Canvas canvas = new Canvas(bitmap);
                vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                vectorDrawable.draw(canvas);
            } else {
                // Handle other drawable types or return an empty array
                return new byte[0];
            }

            // Resize the bitmap to 1x1 pixel
            Bitmap smallBitmap = Bitmap.createScaledBitmap(bitmap, 1, 1, true);

            // Compress the bitmap to reduce file size
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            smallBitmap.compress(Bitmap.CompressFormat.JPEG, 10, stream); // Set very low quality
            return stream.toByteArray();
        } else {
            // Return an empty array or handle the case where image is null
            return new byte[0];
        }
    }

    // Method to resize the bitmap
    private Bitmap resizeBitmap(Bitmap originalBitmap, int maxWidth, int maxHeight) {
        int width = originalBitmap.getWidth();
        int height = originalBitmap.getHeight();
        float aspectRatio = (float) width / height;

        if (width > maxWidth) {
            width = maxWidth;
            height = Math.round(width / aspectRatio);
        }

        if (height > maxHeight) {
            height = maxHeight;
            width = Math.round(height * aspectRatio);
        }

        return Bitmap.createScaledBitmap(originalBitmap, width, height, true);
    }



    public void addImage(View v) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*"); // To select images
        //noinspection deprecation
        startActivityForResult(intent, 1); // Request code 1
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Get the URI of the selected image
            Uri imageUri = data.getData();
            im.setVisibility(View.VISIBLE);
            im.setImageURI(imageUri); // Set the image to ImageView
            imageByteArray = convertImageViewToByteArray(im);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
            } else {
                // Permission denied
            }
        }
    }
    public void submitPost(View v) {
        String title = ((EditText) findViewById(R.id.title_editText)).getText().toString().trim();
        String content = ((EditText) findViewById(R.id.content_editText)).getText().toString().trim();
        String tags = tagsSpinner.getSelectedItem().toString().trim();

        boolean isValid = true;

        // Check if title is empty
        if (title.isEmpty()) {
            titleStar.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            titleStar.setVisibility(View.GONE);
        }

        // Check if content is empty
        if (content.isEmpty()) {
            contextStar.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            contextStar.setVisibility(View.GONE);
        }

        if (isValid) {
            // Create ForumPost object
            ForumPost post = new ForumPost(0,user.getId(),content,0,true,imageByteArray,tags,title);

            // Convert ForumPost object to JSON string using Gson
            Gson gson = new Gson();
            String jsonPayload = gson.toJson(post);
            Log.d("RetrofitRequest", "Request JSON: " + jsonPayload);

            // Make the API call to create the post
            Call<ForumPost> call = forumPostService.createForumPost(post);
            call.enqueue(new Callback<ForumPost>() {
                @Override
                public void onResponse(Call<ForumPost> call, Response<ForumPost> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(CreateForumPostActivity.this, "Post created successfully!", Toast.LENGTH_SHORT).show();
                        // Finish the activity
                        finish();
                    } else {
                        Toast.makeText(CreateForumPostActivity.this, "Failed to create post.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ForumPost> call, Throwable t) {
                    Toast.makeText(CreateForumPostActivity.this, "An error occurred: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Please fill in all the required fields", Toast.LENGTH_SHORT).show();
        }
    }

    public void cancelPost(View v){
        finish();
    }
}
