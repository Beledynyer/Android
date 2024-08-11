package com.example.theagora;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.List;

public class ForumPostAdapter extends ArrayAdapter<ForumPost> {

    private Context context;
    private List<ForumPost> forumPostList;

    public ForumPostAdapter(Context context,List<ForumPost> forumPostList){
        super(context,0,forumPostList);
        this.context = context;
        this.forumPostList = forumPostList;
    }

}
