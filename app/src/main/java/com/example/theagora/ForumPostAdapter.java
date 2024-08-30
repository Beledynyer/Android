package com.example.theagora;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ForumPostAdapter extends RecyclerView.Adapter<ForumPostAdapter.MyViewHolder> {

    Context context;

    User user;
    ArrayList<ForumPost> forumPosts;

    public ForumPostAdapter(Context context,ArrayList<ForumPost> forumPosts,User user){
        this.context = context;
        this.forumPosts = forumPosts;
        this.user =user;
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
        User forumPostUser = forumPost.getUser();
        if (forumPostUser != null) {
            if(forumPostUser.getId() == user.getId() || !forumPost.getTags().equals("Anonymous")){
                holder.name.setText(forumPostUser.getfName() + " " + forumPostUser.getlName());
            }
        } else {
            holder.name.setText("Unknown User");
        }
        holder.title.setText(forumPosts.get(position).getTitle());
        holder.tags.setText(forumPosts.get(position).getTags());
        assert forumPostUser != null;
        if(forumPostUser.getId() == user.getId()) holder.bin.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return forumPosts.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView name,title,tags;
        ImageView bin;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.forum_post_username);
            title = itemView.findViewById(R.id.title_view);
            tags = itemView.findViewById(R.id.tags_view);
            bin = itemView.findViewById(R.id.bin_icon);

            bin.setOnClickListener(view ->{

            });
        }
    }

    public  void add(ForumPost p){
        forumPosts.add(p);
        notifyItemChanged(forumPosts.size()-1);
    }
}
