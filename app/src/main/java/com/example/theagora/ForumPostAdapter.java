package com.example.theagora;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ForumPostAdapter extends RecyclerView.Adapter<ForumPostAdapter.MyViewHolder> {

    Context context;

    ArrayList<ForumPost> forumPosts;

    public ForumPostAdapter(Context context,ArrayList<ForumPost> forumPosts){
        this.context = context;
        this.forumPosts = forumPosts;
    }
    @NonNull
    @Override
    public ForumPostAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_view_row,parent,false);
        return new ForumPostAdapter.MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ForumPostAdapter.MyViewHolder holder, int position) {
        ForumPost forumPost = forumPosts.get(position);
        User user = forumPost.getUser();
        if (user != null) {
            holder.name.setText(user.getfName() + " " + user.getlName());
        } else {
            holder.name.setText("Unknown User");
        }
        holder.title.setText(forumPosts.get(position).getTitle());
        holder.tags.setText(forumPosts.get(position).getTags());
    }

    @Override
    public int getItemCount() {
        return forumPosts.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView name,title,tags;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.forum_post_username);
            title = itemView.findViewById(R.id.title_view);
            tags = itemView.findViewById(R.id.tags_view);
        }
    }

    public  void add(ForumPost p){
        forumPosts.add(p);
        notifyItemChanged(forumPosts.size()-1);
    }
}
