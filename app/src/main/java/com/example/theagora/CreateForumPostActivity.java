package com.example.theagora;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import java.io.IOException;

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
    String imageByteArray;

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

    public void addImage(View v) {
        im.setImageBitmap(null);
        // Initialize intent
        Intent intent=new Intent(Intent.ACTION_PICK);
        // set type
        intent.setType("image/*");
        // start activity result
        //noinspection deprecation
        startActivityForResult(Intent.createChooser(intent,"Select Image"),1);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {


            // Get the URI of the selected image
            Uri imageUri = data.getData();
            im.setVisibility(View.VISIBLE);
            im.setImageURI(imageUri); // Set the image to ImageView
            Bitmap bitmap= null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imageUri);
                ByteArrayOutputStream stream=new ByteArrayOutputStream();
                // compress Bitmap
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);
                // Initialize byte array
                byte[] bytes=stream.toByteArray();
                // get base64 encoded string
                imageByteArray = Base64.encodeToString(bytes,Base64.NO_WRAP);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            // initialize byte stream

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
            // Inflate the custom layout
            LayoutInflater inflater = LayoutInflater.from(this);
            View dialogView = inflater.inflate(R.layout.dialog_buttons, null);

            // Create the AlertDialog and set the custom view
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Confirm submission")
                    .setMessage("Are you sure you want to submit the post titled: \"" + title + "\"?")
                    .setView(dialogView)
                    .create();

            // Get references to the buttons in the custom layout
            Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
            Button btnSubmit = dialogView.findViewById(R.id.btn_delete);
            btnSubmit.setText("Submit"); // Change the text of the button

            // Set click listeners for the buttons
            btnCancel.setOnClickListener(view -> dialog.dismiss());

            btnSubmit.setOnClickListener(view -> {
                // Create ForumPost object
                ForumPost post = new ForumPost(0, user.getId(), content, 0, false, imageByteArray, tags, title);

                // Convert ForumPost object to JSON string using Gson
                Gson gson = new Gson();
                String jsonPayload = gson.toJson(post);
                Log.d("RetrofitRequest", "Request JSON: " + jsonPayload);

                // Make the API call to create the post
                Call<ForumPost> call = forumPostService.createForumPost(post);
                call.enqueue(new Callback<ForumPost>() {
                    @Override
                    public void onResponse(@NonNull Call<ForumPost> call, @NonNull Response<ForumPost> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(CreateForumPostActivity.this, "Post submitted successfully!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(CreateForumPostActivity.this, "Failed to create post.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ForumPost> call, @NonNull Throwable t) {
                        Toast.makeText(CreateForumPostActivity.this, "An error occurred: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.dismiss();
            });

            // Show the dialog
            dialog.show();
        } else {
            Toast.makeText(this, "Please fill in all the required fields", Toast.LENGTH_SHORT).show();
        }
    }
    @SuppressLint("SetTextI18n")
    public void cancelPost(View v){
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_buttons, null);

        // Create the AlertDialog and set the custom view
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Cancel confirmation")
                .setMessage("Are you sure you want to go back? All content entered will be discarded.")
                .setView(dialogView)
                .create();

        // Get references to the buttons in the custom layout
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        Button btnSubmit = dialogView.findViewById(R.id.btn_delete);
        btnSubmit.setText("Yes"); // Change the text of the button
        btnCancel.setText("No");
        btnSubmit.setOnClickListener(view -> {
            dialog.dismiss();
            kill();
        });
        btnCancel.setOnClickListener(view -> dialog.dismiss());
        dialog.show();
    }

    private void kill() {
        finish();
    }
}
