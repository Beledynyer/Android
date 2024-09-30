package com.example.theagora;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForumPostAdapter extends RecyclerView.Adapter<ForumPostAdapter.MyViewHolder> {

    Context context;

    Activity activity;
    User user;
    ArrayList<ForumPost> forumPosts;
    ArrayList<ForumPost> filteredForumPosts;
    ForumPostService forumPostService;
    private  int lastPosition = -1;

    public interface OnPostActionListener {
        void onPostBanned(int position);
        void onPostApproved(int position);
        void onEmptyList();
    }

    private OnPostActionListener onPostActionListener;

    public void setOnPostActionListener(OnPostActionListener listener) {
        this.onPostActionListener = listener;
    }

    public void removePost(int position) {
        if (position >= 0 && position < filteredForumPosts.size()) {
            ForumPost removedPost = filteredForumPosts.get(position);
            filteredForumPosts.remove(position);
            forumPosts.remove(removedPost);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, filteredForumPosts.size());
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filterList(ArrayList<String> selectedTags, boolean showMyPosts) {
        filteredForumPosts.clear();
        for (ForumPost post : forumPosts) {
            boolean matchesTags = selectedTags.isEmpty() || selectedTags.contains(post.getTags());
            boolean matchesUser = !showMyPosts || post.getUserId() == user.getId();
            if (matchesTags && matchesUser) {
                filteredForumPosts.add(post);
            }
        }
        notifyDataSetChanged();
        if (filteredForumPosts.isEmpty() && onPostActionListener != null) {
            onPostActionListener.onEmptyList();
        }
    }

    public ForumPostAdapter(Activity context, ArrayList<ForumPost> forumPosts, User user, ForumPostService forumPostService) {
        this.context = context;
        activity = context;
        this.forumPosts = forumPosts;
        this.filteredForumPosts = new ArrayList<>(forumPosts);
        this.user = user;
        this.forumPostService = forumPostService;
    }
    @NonNull
    @Override
    public ForumPostAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_view_row,parent,false);
        return new ForumPostAdapter.MyViewHolder(view,forumPosts,this,forumPostService,this.user,activity);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ForumPostAdapter.MyViewHolder holder, int position) {
        ForumPost forumPost = filteredForumPosts.get(position);
        User forumPostUser = forumPost.getUser();
        if (forumPostUser != null) {
            if(forumPostUser.getId() == user.getId() || !forumPost.getTags().equals("Anonymous")){
                holder.name.setText(forumPostUser.getfName() + " " + forumPostUser.getlName());
            }
        } else {
            holder.name.setText("Unknown User");
        }
        holder.title.setText(forumPost.getTitle());
        holder.tags.setText(forumPost.getTags());

        if (forumPostUser != null && forumPostUser.getId() == user.getId()) {
            holder.bin.setVisibility(View.VISIBLE);
        } else {
            holder.bin.setVisibility(View.GONE);
        }

        setAnimation(holder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return filteredForumPosts.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name, title, tags;
        ImageView bin;

        public MyViewHolder(@NonNull View itemView, ArrayList<ForumPost> forumPosts, ForumPostAdapter adapter, ForumPostService forumPostService, User user, Activity context) {
            super(itemView);

            name = itemView.findViewById(R.id.forum_post_username);
            title = itemView.findViewById(R.id.title_view);
            tags = itemView.findViewById(R.id.tags_view);
            bin = itemView.findViewById(R.id.bin_icon);

            if(user.isStaffMember()){
                itemView.setOnClickListener(view -> {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        ForumPost post = forumPosts.get(position);
                        int postId = post.getPostId();

                        // Send only the post ID to ManagePostStatusActivity
                        Intent intent = new Intent(itemView.getContext(), ManagePostStatusActivity.class);
                        intent.putExtra("forumPostId", postId);
                        intent.putExtra("position", position); // Add position to intent
                        intent.putExtra("user", user);
                        context.startActivityForResult(intent,2);
                    }
                });
            }
            else{
                itemView.setOnClickListener(view -> {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        ForumPost post = forumPosts.get(position);
                        int postId = post.getPostId();  // Get the post ID

                        // Send only the post ID to ViewForumPostActivity
                        Intent intent = new Intent(itemView.getContext(), ViewForumPostActivity.class);
                        intent.putExtra("forumPostId", postId); // Pass the forumPost ID
                        intent.putExtra("user", user);
                        itemView.getContext().startActivity(intent);
                    }
                });
            }


            bin.setOnClickListener(view -> {
                String postTitle = title.getText().toString();

                // Inflate the custom layout
                LayoutInflater inflater = LayoutInflater.from(itemView.getContext());
                View dialogView = inflater.inflate(R.layout.dialog_buttons, null);

                // Create the AlertDialog and set the custom view
                AlertDialog dialog = new AlertDialog.Builder(itemView.getContext())
                        .setTitle("Confirm delete")
                        .setMessage("Are you sure you want to delete the post titled: \"" + postTitle + "\"?")
                        .setView(dialogView)
                        .create();

                // Get references to the buttons in the custom layout
                Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
                Button btnDelete = dialogView.findViewById(R.id.btn_delete);

                // Set click listeners for the buttons
                btnCancel.setOnClickListener(v -> dialog.dismiss());

                btnDelete.setOnClickListener(v -> {
                    // Perform the delete action
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        ForumPost postToDelete = adapter.filteredForumPosts.get(position);
                        int postId = postToDelete.getPostId();

                        // Call API to delete the post from the database
                        forumPostService.deleteForumPost(postId).enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.isSuccessful()) {
                                    // Remove the item from the list and notify the adapter
                                    adapter.removePost(position);
                                    Toast.makeText(context,"Forum post deleted successfully",Toast.LENGTH_LONG).show();
                                } else {
                                    // Handle the case where the server responds with an error
                                    Log.e("ForumPostAdapter", "Failed to delete post: " + response.message());
                                }
                            }
                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                // Handle the error here (e.g., show a message to the user)
                                Log.e("ForumPostAdapter", "Error deleting post", t);
                            }
                        });
                    }
                    dialog.dismiss();
                });

                // Show the dialog
                dialog.show();
            });

        }
    }

    public  void add(ForumPost p){
        forumPosts.add(p);
        notifyItemChanged(forumPosts.size()-1);
    }

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            //TranslateAnimation anim = new TranslateAnimation(0,-1000,0,-1000);
            ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            //anim.setDuration(new Random().nextInt(501));//to make duration random number between [0,501)
            anim.setDuration(550);//to make duration random number between [0,501)
            viewToAnimate.startAnimation(anim);
            lastPosition = position;

        }
    }

    public void filteredList(ArrayList<ForumPost> arr) {
        this.filteredForumPosts = arr;
        notifyDataSetChanged();
        if (filteredForumPosts.isEmpty() && onPostActionListener != null) {
            onPostActionListener.onEmptyList();
        }
    }
}
