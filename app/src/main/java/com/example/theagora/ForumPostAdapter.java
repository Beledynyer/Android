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
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForumPostAdapter extends RecyclerView.Adapter<ForumPostAdapter.MyViewHolder> {

    private final Context context;
    private final Activity activity;
    private final User user;
    private final ArrayList<ForumPost> originalPosts;
    private final ArrayList<ForumPost> filteredPosts;
    private final ForumPostService forumPostService;
    private int lastPosition = -1;

    public interface OnPostActionListener {
        void onPostBanned(int position);
        void onPostApproved(int position);
        void onEmptyList();
    }

    private OnPostActionListener onPostActionListener;

    public void setOnPostActionListener(OnPostActionListener listener) {
        this.onPostActionListener = listener;
    }

    public ForumPostAdapter(Activity context, ArrayList<ForumPost> forumPosts, User user, ForumPostService forumPostService) {
        this.context = context;
        this.activity = context;
        this.originalPosts = new ArrayList<>(forumPosts);
        this.filteredPosts = new ArrayList<>(forumPosts);
        this.user = user;
        this.forumPostService = forumPostService;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_view_row, parent, false);
        return new MyViewHolder(view, originalPosts, this, forumPostService, this.user, activity);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ForumPost forumPost = filteredPosts.get(position);
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
        return filteredPosts.size();
    }

    public void removePost(int position) {
        if (position >= 0 && position < filteredPosts.size()) {
            ForumPost removedPost = filteredPosts.get(position);
            filteredPosts.remove(position);
            originalPosts.remove(removedPost);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, filteredPosts.size());
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filterList(ArrayList<String> selectedTags, boolean showMyPosts) {
        filteredPosts.clear();
        for (ForumPost post : originalPosts) {
            boolean matchesTags = selectedTags.isEmpty() || selectedTags.contains(post.getTags());
            boolean matchesUser = !showMyPosts || post.getUserId() == user.getId();
            if (matchesTags && matchesUser) {
                filteredPosts.add(post);
            }
        }
        sortPosts();
        notifyDataSetChanged();
        if (filteredPosts.isEmpty() && onPostActionListener != null) {
            onPostActionListener.onEmptyList();
        }
    }

    public void add(ForumPost p) {
        originalPosts.add(p);
        filteredPosts.add(p);
        notifyItemInserted(filteredPosts.size() - 1);
    }

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            anim.setDuration(550);
            viewToAnimate.startAnimation(anim);
            lastPosition = position;
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filteredList(ArrayList<ForumPost> arr) {
        originalPosts.clear();
        originalPosts.addAll(arr);
        filteredPosts.clear();
        filteredPosts.addAll(arr);
        sortPosts();
        notifyDataSetChanged();
        if (filteredPosts.isEmpty() && onPostActionListener != null) {
            onPostActionListener.onEmptyList();
        }
    }

    private void sortPosts() {
        Collections.sort(filteredPosts, (post1, post2) ->
                post1.getTitle().compareToIgnoreCase(post2.getTitle()));
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
                        ForumPost post = adapter.filteredPosts.get(position);
                        int postId = post.getPostId();

                        Intent intent = new Intent(itemView.getContext(), ManagePostStatusActivity.class);
                        intent.putExtra("forumPostId", postId);
                        intent.putExtra("position", position);
                        intent.putExtra("user", user);
                        context.startActivityForResult(intent,2);
                    }
                });
            } else {
                itemView.setOnClickListener(view -> {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        ForumPost post = adapter.filteredPosts.get(position);
                        int postId = post.getPostId();

                        Intent intent = new Intent(itemView.getContext(), ViewForumPostActivity.class);
                        intent.putExtra("forumPostId", postId);
                        intent.putExtra("user", user);
                        itemView.getContext().startActivity(intent);
                    }
                });
            }

            bin.setOnClickListener(view -> {
                String postTitle = title.getText().toString();

                LayoutInflater inflater = LayoutInflater.from(itemView.getContext());
                View dialogView = inflater.inflate(R.layout.dialog_buttons, null);

                AlertDialog dialog = new AlertDialog.Builder(itemView.getContext())
                        .setTitle("Confirm delete")
                        .setMessage("Are you sure you want to delete the post titled: \"" + postTitle + "\"?")
                        .setView(dialogView)
                        .create();

                Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
                Button btnDelete = dialogView.findViewById(R.id.btn_delete);

                btnCancel.setOnClickListener(v -> dialog.dismiss());

                btnDelete.setOnClickListener(v -> {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        ForumPost postToDelete = adapter.filteredPosts.get(position);
                        int postId = postToDelete.getPostId();

                        forumPostService.deleteForumPost(postId).enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.isSuccessful()) {
                                    adapter.removePost(position);
                                    Toast.makeText(context,"Forum post deleted successfully",Toast.LENGTH_LONG).show();
                                } else {
                                    Log.e("ForumPostAdapter", "Failed to delete post: " + response.message());
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Log.e("ForumPostAdapter", "Error deleting post", t);
                            }
                        });
                    }
                    dialog.dismiss();
                });

                dialog.show();
            });
        }
    }
}