package com.example.agrobd.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agrobd.Model.Post;
import com.example.agrobd.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class SavedAdapter extends RecyclerView.Adapter<SavedAdapter.ViewHolder> {

    Context context;
    List<Post> list;

    public SavedAdapter(Context context, List<Post> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_saved,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post=list.get(position);

        //Picasso.get().load(post.getPostImage()).into(holder.photo);
        Picasso.get().load(post.getPostImage()).placeholder(R.drawable.ic_baseline_image_24).into(holder.photo);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView photo;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            photo=itemView.findViewById(R.id.photo_fav);

        }
    }
}
