package com.example.theagora;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private List<Comment> comments;

    public CommentAdapter(List<Comment> comments) {
        this.comments = comments;
    }

    public void setComments(List<Comment> comments){
        this.comments =comments;
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        ImageView ivUserIcon;
        TextView tvUserName;
        TextView tvCommentContent;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            ivUserIcon = itemView.findViewById(R.id.ivUserIcon);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvCommentContent = itemView.findViewById(R.id.tvCommentContent);
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
        holder.tvUserName.setText(comment.getUser().getfName() + " " + comment.getUser().getlName()); // Assuming User class has a 'getUsername()' method
        holder.tvCommentContent.setText(comment.getContent());
        // You can set a custom icon for the user here if needed
        // holder.ivUserIcon.setImageResource(...);
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }
}