package com.example.agrobd.Adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.example.agrobd.CommentActivity;
import com.example.agrobd.Model.CommentList;

import com.example.agrobd.Model.Post;
import com.example.agrobd.Model.UserList;
import com.example.agrobd.OthersProfileActivity;
import com.example.agrobd.R;
import com.example.agrobd.RegisterActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    Context context;
    List<Post> lists;

    FirebaseUser firebaseUser;

    public PostAdapter(Context context, List<Post> lists) {
        this.context = context;
        this.lists = lists;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_post,parent,false);
        return new ViewHolder(view);

    }




    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Post post = lists.get(position);

        //date and time
        try {
            Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
            calendar.setTimeInMillis(Long.parseLong(post.getDate()));
            String date = DateFormat.format("dd-MMM-yyyy  hh:mm a",calendar).toString();
            holder.date.setText(""+date);
        }catch (NumberFormatException ignored)
        {
        }


        publisherInfo(holder.profile, holder.username,holder.contactNumber,post.getPublisher());
        Picasso.get().load(post.getPostImage()).placeholder(R.drawable.ic_baseline_image_24).into(holder.post_image);



        //description
        if (post.getDescription().isEmpty())
        {
            holder.description.setVisibility(View.GONE);
        }else
        {
            holder.description.setVisibility(View.VISIBLE);
            holder.description.setText(post.getDescription());
        }





        //option
        holder.options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu=new PopupMenu(context,v);

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId())
                        {
                            case R.id.copy:

                                return true;
                            case R.id.delete:
                                FirebaseDatabase.getInstance().getReference().child("Posts")
                                        .child(post.getPostId()).removeValue()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful())
                                                {
                                                    Toast.makeText(context, "Deleted!", Toast.LENGTH_SHORT).show();
                                                }else
                                                {
                                                    Toast.makeText(context, "Unable to delete", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                                return true;

                            case R.id.unfollow:
                                FirebaseDatabase.getInstance().getReference().child("Follow")
                                        .child(firebaseUser.getUid())
                                        .child("following").child(post.getPublisher()).removeValue();

                                FirebaseDatabase.getInstance().getReference().child("Follow")
                                        .child(post.getPublisher())
                                        .child("followers").child(firebaseUser.getUid()).removeValue();
                                return true;
                        }
                        return true;
                    }
                });

                popupMenu.inflate(R.menu.options);
                if (!post.getPublisher().equals(firebaseUser.getUid()))
                {
                    popupMenu.getMenu().findItem(R.id.delete).setVisible(false);
                }
                popupMenu.show();
            }
        });





        //likes
        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (holder.like.getTag().equals("Like"))
                {
                    FirebaseDatabase.getInstance().getReference()
                            .child("Likes")
                            .child(post.getPostId())
                            .child(firebaseUser.getUid())
                            .setValue(true);//like
                }else
                {
                    FirebaseDatabase.getInstance().getReference()
                            .child("Likes")
                            .child(post.getPostId())
                            .child(firebaseUser.getUid())
                            .removeValue();//unlike
                }
            }
        });
        isLiked(post.getPostId(),holder.like,holder.like_count);//count likes




        //comments
        holder.comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (context, CommentActivity.class);
                intent.putExtra("postId",post.getPostId());
                intent.putExtra("publisher",post.getPublisher());
                context.startActivity(intent);
            }
        });
        getCommentsCount(holder.comments_count, post.getPostId());



        //save
        holder.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (holder.save.getTag().equals("save"))
                {
                    Toast.makeText(context, "Added to Saved item", Toast.LENGTH_SHORT).show();
                    FirebaseDatabase.getInstance().getReference().child("Favourites")
                            .child(firebaseUser.getUid())
                            .child(post.getPostId())
                            .setValue(true);
                }else
                {
                    Toast.makeText(context, "Removed from Saved item", Toast.LENGTH_SHORT).show();
                    FirebaseDatabase.getInstance().getReference().child("Favourites")
                            .child(firebaseUser.getUid())
                            .child(post.getPostId())
                            .removeValue();
                }
            }
        });
        isSaved(post.getPostId(),holder.save);



        holder.profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, OthersProfileActivity.class);
                intent.putExtra("uid",post.getPublisher());
                context.startActivity(intent);
            }
        });

        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, OthersProfileActivity.class);
                intent.putExtra("uid",post.getPublisher());
                context.startActivity(intent);
            }
        });

        /*holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareItem(post.getPostImage());
            }
        });*/

    }

    @Override
    public int getItemCount() {
        return lists.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView post_image,like,comments,share,save,options;
        CircleImageView profile;
        TextView username,contactNumber,description,date,like_count,comments_count;
        ProgressDialog pd;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            post_image= itemView.findViewById(R.id.post_image);
            profile= itemView.findViewById(R.id.profile_image);
            username= itemView.findViewById(R.id.username);
            contactNumber= itemView.findViewById(R.id.number);
            description= itemView.findViewById(R.id.description);
            date=itemView.findViewById(R.id.date);
            like=itemView.findViewById(R.id.like);
            like_count=itemView.findViewById(R.id.likes_count);
            comments= itemView.findViewById(R.id.comments);
            comments_count= itemView.findViewById(R.id.comments_count);
            share=itemView.findViewById(R.id.share);
            save=itemView.findViewById(R.id.save);
            options=itemView.findViewById(R.id.options);
        }
    }

    //public void onBindViewHolder(@NonNull viewHolder holder, int position)....for Feed Fragment
    private void publisherInfo(final CircleImageView profile, final TextView username, final TextView contactNumber,final String userid)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");

        reference.child(userid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                UserList userList = snapshot.getValue(UserList.class);

                Picasso.get().load(userList.getUrl()).placeholder(R.drawable.profile_image).into(profile);
                //Picasso.get().load(userList.getProfile()).into(profile);
                username.setText(userList.getUsername());
                contactNumber.setText(userList.getContactNumber());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    //likes
    private void isLiked(String postId, final ImageView imageView, final TextView textView)
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Likes")
                .child(postId);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                textView.setText(snapshot.getChildrenCount()+"");//like count

                if (snapshot.child(user.getUid()).exists())
                {
                    imageView.setImageResource(R.drawable.ic_baseline_favorite_24);
                    imageView.setTag("Liked");
                }
                else
                {
                    imageView.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                    imageView.setTag("Like");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    //comment count
    private void getCommentsCount(final TextView comments_count,final String postId)
    {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Comments")
                .child(postId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                comments_count.setText(snapshot.getChildrenCount()+"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }




    //save
    private void isSaved(final String postId,final ImageView save)
    {
        FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Favourites")
                .child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(postId).exists())
                {
                    save.setImageResource(R.drawable.ic_baseline_bookmark_24);
                    save.setTag("saved");
                }else
                {
                    save.setImageResource(R.drawable.ic_baseline_bookmark_border_24);
                    save.setTag("save");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
            }
        });

    }


/*
    public void shareItem(final String url) {
        ProgressDialog pd=new ProgressDialog(context);
        pd.setTitle("Downloading..");
        pd.setMessage("Please wait");
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        Picasso.get().load(url).into(new Target() {
            @Override public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("image/*");
                i.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(bitmap));
                context.startActivity(Intent.createChooser(i, "Share Image"));
                pd.dismiss();

            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                pd.dismiss();
                Toast.makeText(context, "Error"+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override public void onPrepareLoad(Drawable placeHolderDrawable) {
                pd.dismiss();
            }
        });
    }
    public Uri getLocalBitmapUri(Bitmap bmp) {
        Uri bmpUri = null;
        try {
            File file =  new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Memer" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }*/
}



