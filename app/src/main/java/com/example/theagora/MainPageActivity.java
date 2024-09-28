package com.example.theagora;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainPageActivity extends AppCompatActivity implements ForumPostAdapter.OnPostActionListener {

    ArrayList<ForumPost> forumPosts = new ArrayList<>();
    User user;
    ForumPostAdapter adapter;
    RecyclerView recyclerView;
    ForumPostService forumPostService;
    SearchView searchView;

    private ImageView filterIcon;
    private ArrayList<String> selectedTags = new ArrayList<>();
    private boolean showMyPosts = false;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        user = getIntent().getParcelableExtra("user");
        TextView userNameView = findViewById(R.id.user_name);
        assert user != null;
        userNameView.setText(user.getfName() + " " + user.getlName());

        recyclerView = findViewById(R.id.mRecycleView);
        forumPostService = RetrofitClientInstance.getRetrofitInstance().create(ForumPostService.class);
        adapter = new ForumPostAdapter(this, forumPosts,user,forumPostService);
        adapter.setOnPostActionListener(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        filterIcon = findViewById(R.id.filter_icon);
        filterIcon.setOnClickListener(v -> showFilterDialog());

        searchView = findViewById(R.id.search);
        setupSearchView();
        // Load all forum posts
        setUpForumPosts();


        FloatingActionButton fab;
        fab = findViewById(R.id.floatingActionButton);

        if(user.isStaffMember()){
            fab.setVisibility(View.GONE);
        }
        else{
            // Hide/Show FloatingActionButton on scroll
            recyclerView.addOnScrollListener(new HideShowScrollListener() {
                @Override
                public void onHide() {
                    fab.animate().setInterpolator(new AccelerateDecelerateInterpolator()).scaleX(0).scaleY(0);
                }

                @Override
                public void onShow() {
                    fab.animate().setInterpolator(new AccelerateDecelerateInterpolator()).scaleX(1).scaleY(1);
                }
            });
        }


    }

    private void showFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.filter_dialog, null);
        builder.setView(dialogView);

        CheckBox myPostsCheckbox = dialogView.findViewById(R.id.checkbox_my_posts);
        CheckBox selectAllCheckbox = dialogView.findViewById(R.id.checkbox_select_all);
        LinearLayout tagsContainer = dialogView.findViewById(R.id.tags_container);

        String[] tagsArray = getResources().getStringArray(R.array.tags_array);
        List<CheckBox> tagCheckboxes = new ArrayList<>();

        for (String tag : tagsArray) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(tag);
            checkBox.setChecked(selectedTags.contains(tag));
            tagsContainer.addView(checkBox);
            tagCheckboxes.add(checkBox);
        }

        selectAllCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            for (CheckBox checkBox : tagCheckboxes) {
                checkBox.setChecked(isChecked);
            }
        });

        myPostsCheckbox.setChecked(showMyPosts);

        AlertDialog dialog = builder.create();

        dialogView.findViewById(R.id.button_cancel).setOnClickListener(v -> dialog.dismiss());

        dialogView.findViewById(R.id.button_filter).setOnClickListener(v -> {
            showMyPosts = myPostsCheckbox.isChecked();
            selectedTags.clear();
            for (CheckBox checkBox : tagCheckboxes) {
                if (checkBox.isChecked()) {
                    selectedTags.add(checkBox.getText().toString());
                }
            }
            applyFilters();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void applyFilters() {
        adapter.filterList(selectedTags, showMyPosts);
    }
    private void setupSearchView() {
        // Increase text size

        int id = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView textView = searchView.findViewById(id);
        if (textView != null) {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 72); // Adjust this value as needed
        }

// Increase icon size
        ImageView searchIcon = searchView.findViewById(androidx.appcompat.R.id.search_mag_icon);
        ViewGroup.LayoutParams layoutParams = searchIcon.getLayoutParams();
        layoutParams.width = 200; // Adjust this value to make the icon larger or smaller
        layoutParams.height = 200; // Adjust this value to make the icon larger or smaller
        searchIcon.setLayoutParams(layoutParams);

// Optionally, adjust the padding to accommodate the larger icon
        searchView.setPadding(16, searchView.getPaddingTop(), searchView.getPaddingRight(), searchView.getPaddingBottom());
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterPosts(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterPosts(newText);
                return false;
            }
        });
    }

    private void filterPosts(String query) {
        ArrayList<ForumPost>  filteredForumPosts = new ArrayList<>();
        if (query.isEmpty()) {
            filteredForumPosts.addAll(forumPosts);
        } else {
            String lowercaseQuery = query.toLowerCase(Locale.getDefault());
            filteredForumPosts.addAll(forumPosts.stream()
                    .filter(post -> post.getTitle().toLowerCase(Locale.getDefault()).contains(lowercaseQuery))
                    .collect(Collectors.toList()));
        }
        adapter.filteredList(filteredForumPosts);
    }

    public void createForumPost(View v) {
        Intent i = new Intent(this, CreateForumPostActivity.class);
        i.putExtra("user", user);
        startActivity(i);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            int position = data.getIntExtra("position", -1);
            boolean isBanned = data.getBooleanExtra("isBanned", false);
            boolean isApproved = data.getBooleanExtra("isApproved", false);

            if (position != -1) {
                if (isBanned) {
                    onPostBanned(position);
                } else if (isApproved) {
                    onPostApproved(position);
                }
            }
        }
    }
    private void setUpForumPosts() {
        UserService userService = RetrofitClientInstance.getRetrofitInstance().create(UserService.class);

        Call<List<ForumPost>> call;
        if (user.isStaffMember()) {
            call = forumPostService.getUnapprovedForumPosts();
        } else {
            call = forumPostService.getApprovedForumPosts();
        }

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
                                    applyFilters();
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                                Log.e("MainPageActivity", "Failed to load user details", t);
                                loadedPosts[0]++;
                                if (loadedPosts[0] == totalPosts) {
                                    adapter.notifyDataSetChanged();  // Update the adapter after all users are set
                                    applyFilters();
                                }
                            }
                        });
                    }
                } else {
                    Log.e("MainPageActivity", "No forum posts available.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ForumPost>> call, @NonNull Throwable t) {
                Log.e("MainPageActivity", "Failed to load forum posts", t);
            }
        });
    }



    @Override
    public void onPostBanned(int position) {
        adapter.removePost(position);
    }

    @Override
    public void onPostApproved(int position) {
        if (user.isStaffMember()) {
            adapter.removePost(position);
        }
    }
}
