package com.example.theagora;

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

    @Override
    public void onBindViewHolder(@NonNull ForumPostAdapter.MyViewHolder holder, int position) {
        String fullUserName = forumPosts.get(position).getUser().getfName() + " " + forumPosts.get(position).getUser().getlName();
        holder.name.setText(fullUserName);
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
}
