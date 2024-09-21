package com.example.theagora;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private List<Comment> comments;
    private User currentUser;
    private CommentDeletionListener deletionListener;

    public CommentAdapter(List<Comment> comments, User currentUser, CommentDeletionListener listener) {
        this.comments = comments;
        this.currentUser = currentUser;
        this.deletionListener = listener;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        ImageView ivUserIcon;
        TextView tvUserName;
        TextView tvCommentContent;
        ImageView ivTrashBin;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            ivUserIcon = itemView.findViewById(R.id.ivUserIcon);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvCommentContent = itemView.findViewById(R.id.tvCommentContent);
            ivTrashBin = itemView.findViewById(R.id.ivTrashBin);
        }
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment_item_layout, parent, false);
        return new CommentViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = comments.get(position);
        holder.tvUserName.setText(comment.getUser().getfName() + " " + comment.getUser().getlName());
        holder.tvCommentContent.setText(comment.getContent());

        // Show trash bin if the comment belongs to the current user
        if (comment.getUser().getId() == currentUser.getId()) {
            holder.ivTrashBin.setVisibility(View.VISIBLE);
            holder.ivTrashBin.setOnClickListener(v -> showDeleteDialog(v.getContext(), comment));
        } else {
            holder.ivTrashBin.setVisibility(View.GONE);
        }
    }

    private void showDeleteDialog(Context context, Comment comment) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_buttons, null);

        builder.setTitle("Confirm delete")
                .setMessage("Are you sure you want to delete this comment?")
                .setView(dialogView);

        AlertDialog dialog = builder.create();

        // Get references to the buttons in the custom layout
        android.widget.Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        android.widget.Button btnDelete = dialogView.findViewById(R.id.btn_delete);

        // Set click listeners for the buttons
        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnDelete.setOnClickListener(v -> {
            deleteComment(context, comment);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void deleteComment(Context context, Comment comment) {
        ForumCommentService forumCommentService = RetrofitClientInstance.getRetrofitInstance().create(ForumCommentService.class);
        forumCommentService.deleteComment(comment.getCommentId()).enqueue(new Callback<Void>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    comments.remove(comment);
                    notifyDataSetChanged();
                    Toast.makeText(context, "Comment deleted successfully", Toast.LENGTH_SHORT).show();
                    if (deletionListener != null) {
                        deletionListener.onCommentDeleted();
                    }
                } else {
                    Toast.makeText(context, "Failed to delete comment", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }
}