package com.example.agrobd.Adapter;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.example.agrobd.MainActivity;
import com.example.agrobd.Model.CommentList;

import com.example.agrobd.Model.UserList;
import com.example.agrobd.OthersProfileActivity;
import com.example.agrobd.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    Context context;
    List<CommentList> lists;

    public CommentAdapter(Context context, List<CommentList> lists) {
        this.context = context;
        this.lists = lists;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_comment,parent,false);

        return new ViewHolder(view);

    }




    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        CommentList commentList=lists.get(position);

        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(Long.parseLong(commentList.getDate()));
        String date = DateFormat.format("dd-MMM-yyyy  hh:mm a",calendar).toString();

        holder.date.setText(""+date);
        holder.comment.setText(commentList.getComment());
        getUserInfo(holder.profile,holder.username,commentList.getPublisher());



        holder.profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, OthersProfileActivity.class);
                intent.putExtra("uid",commentList.getPublisher());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//add this line
                context.startActivity(intent);
            }
        });

        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, OthersProfileActivity.class);
                intent.putExtra("uid",commentList.getPublisher());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//add this line
                context.startActivity(intent);
            }
        });






    }

    @Override
    public int getItemCount() {
        return lists.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView comment,username,date;
        CircleImageView profile;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            comment=itemView.findViewById(R.id.comment);
            username=itemView.findViewById(R.id.username);
            profile=itemView.findViewById(R.id.profile_image);
            date=itemView.findViewById(R.id.date);

        }
    }

    private void getUserInfo(final ImageView imageView, final TextView username, final String publisher )
    {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference()
                .child("Users").child(publisher);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                UserList userList = snapshot.getValue(UserList.class);

                username.setText(userList.getUsername());
                Glide.with(context).load(userList.getUrl()).placeholder(R.drawable.profile_image).into(imageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}

