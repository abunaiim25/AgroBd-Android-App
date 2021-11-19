package com.example.agrobd.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agrobd.Model.UserList;
import com.example.agrobd.OthersProfileActivity;
import com.example.agrobd.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private final Context context;
    List<UserList> lists;

    public SearchAdapter(Context context, List<UserList> lists) {
        this.context = context;
        this.lists = lists;
    }

    //
    FirebaseUser user;
    DatabaseReference reference;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_search, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        //
        reference = FirebaseDatabase.getInstance().getReference().child("Users");
        user = FirebaseAuth.getInstance().getCurrentUser();

        UserList userList = lists.get(position);

        holder.username.setText(userList.getUsername());
        holder.number.setText(userList.getContactNumber());

        try {
            Picasso.get().load(userList.getUrl()).placeholder(R.drawable.profile_image).into(holder.profile);
        } catch (Exception e) {
            holder.profile.setImageResource(R.drawable.profile_image);
        }

        //go to OtherProfileActivity
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, OthersProfileActivity.class);
                intent.putExtra("uid", userList.getUser_id());
                context.startActivity(intent);
            }
        });



        //follow and following
        if (user.getUid().equals(userList.getUser_id())) {
            holder.btn_follow.setVisibility(View.GONE);
            holder.btn_following.setVisibility(View.GONE);
        } else {
            holder.btn_follow.setVisibility(View.VISIBLE);
            holder.btn_following.setVisibility(View.VISIBLE);
        }
        isFollowing(lists.get(position).getUser_id(), holder.btn_follow, holder.btn_following);

        holder.btn_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.btn_follow.getText().toString().equals("Follow")) {

                    FirebaseDatabase.getInstance().getReference().child("Follow")
                            .child(user.getUid())
                            .child("following")
                            .child(lists.get(position).getUser_id())
                            .setValue(true);
                    
                    FirebaseDatabase.getInstance().getReference()
                            .child("Follow")
                            .child(lists.get(position).getUser_id())
                            .child("followers")
                            .child(user.getUid())
                            .setValue(true);
                }
            }
        });
        holder.btn_following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.btn_following.getText().toString().equals("Following")) {
                    FirebaseDatabase.getInstance().getReference().child("Follow")
                            .child(user.getUid())
                            .child("following")
                            .child(lists.get(position).getUser_id())
                            .removeValue();

                    FirebaseDatabase.getInstance().getReference().child("Follow")
                            .child(lists.get(position).getUser_id())
                            .child("followers").child(user.getUid())
                            .removeValue();
                }
            }
        });



    }


    @Override
    public int getItemCount() {
        return lists.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profile;
        TextView username, number;
        Button btn_follow, btn_following;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profile = itemView.findViewById(R.id.profile_image);
            username = itemView.findViewById(R.id.username);
            number = itemView.findViewById(R.id.number);
            btn_follow = itemView.findViewById(R.id.btn_follow);
            btn_following = itemView.findViewById(R.id.btn_following);


        }

    }

    private void isFollowing(final String userid, final Button follow, final Button following) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow")
                .child(user.getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(userid).exists()) {
                    follow.setVisibility(View.GONE);
                    following.setVisibility(View.VISIBLE);
                } else {
                    follow.setVisibility(View.VISIBLE);
                    following.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

}
