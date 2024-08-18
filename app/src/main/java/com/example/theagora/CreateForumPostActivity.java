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
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;

public class CreateForumPostActivity extends AppCompatActivity {

    TextView titleStar;
    TextView contextStar;
    TextView tagsStar;
    ImageView im;
    private static final int REQUEST_PERMISSIONS_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_forum_post);

        // Check if permission is granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS_CODE);
        }

        titleStar = findViewById(R.id.Title_star);
        contextStar = findViewById(R.id.Content_star);
        tagsStar = findViewById(R.id.Tags_star);
        im = findViewById(R.id.imageView);

        // Initially hide the stars
        titleStar.setVisibility(View.GONE);
        contextStar.setVisibility(View.GONE);
        tagsStar.setVisibility(View.GONE);

        // Convert the image to byte array, handle null image case
        byte[] imageByteArray = convertImageViewToByteArray(im);

        String title = ((EditText) findViewById(R.id.title_editText)).getText().toString();
        String content = ((EditText) findViewById(R.id.content_editText)).getText().toString();
        String tags = ((EditText) findViewById(R.id.tags_editText)).getText().toString();

       /* Intent userIntent = getIntent();
        User user = userIntent.getParcelableExtra("user");

        // Create ForumPost object with imageByteArray; assuming it handles null values properly
        ForumPost post = new ForumPost(user.getId(), content, 0, imageByteArray, tags, title);*/
    }

    // Method to convert ImageView to byte array
    private byte[] convertImageViewToByteArray(ImageView imageView) {
        if (imageView.getDrawable() != null) {
            if (imageView.getDrawable() instanceof BitmapDrawable) {
                Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                return stream.toByteArray();
            } else if (imageView.getDrawable() instanceof VectorDrawable) {
                // Convert VectorDrawable to Bitmap
                VectorDrawable vectorDrawable = (VectorDrawable) imageView.getDrawable();
                Bitmap bitmap = Bitmap.createBitmap(
                        vectorDrawable.getIntrinsicWidth(),
                        vectorDrawable.getIntrinsicHeight(),
                        Bitmap.Config.ARGB_8888
                );
                Canvas canvas = new Canvas(bitmap);
                vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                vectorDrawable.draw(canvas);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                return stream.toByteArray();
            } else {
                // Handle other drawable types or return an empty array
                return new byte[0];
            }
        } else {
            // Return an empty array or handle the case where image is null
            return new byte[0];
        }
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
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
            } else {
                // Permission denied
            }
        }
    }
}
