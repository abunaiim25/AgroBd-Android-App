package com.example.agrobd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.agrobd.Adapter.PostAdapter;
import com.example.agrobd.Model.Post;
import com.example.agrobd.Model.UserList;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class OthersProfileActivity extends AppCompatActivity {

    TextView username, posts_count, followers_count, following_count, number, address, bio, profession;
    CircleImageView profile_image;
    Button btn_follow, btn_following;
    ProgressBar progressBar;
    String Id;

    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference reference;

    //my post
    DatabaseReference databaseRef;
    RecyclerView recyclerView;
    List<Post> postList;
    PostAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_others_profile);

        username = findViewById(R.id.username);
        posts_count = findViewById(R.id.posts_count);
        followers_count = findViewById(R.id.followers_count);
        following_count = findViewById(R.id.following_count);
        number = findViewById(R.id.number);
        address = findViewById(R.id.address);
        bio = findViewById(R.id.bio);
        btn_follow = findViewById(R.id.follow);
        btn_following = findViewById(R.id.following);
        profile_image = findViewById(R.id.profile_image);
        profession = findViewById(R.id.profession);
        recyclerView = findViewById(R.id.recyclerView);

        progressBar = findViewById(R.id.progressbar);
        progressBar.setVisibility(View.VISIBLE);


        //who will show this profile //for OtherProfileActivity
        Id = getIntent().getStringExtra("uid");


        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference().child("Users");


        //get data to firebase
        getUserData();


        //follow and following count
        getFollowersCount();
        getFollowCount();


        //my post
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(OthersProfileActivity.this));

        user = FirebaseAuth.getInstance().getCurrentUser();
        databaseRef = FirebaseDatabase.getInstance().getReference().child("Posts");

        postList = new ArrayList<>();
        adapter = new PostAdapter(OthersProfileActivity.this, postList);
        recyclerView.setAdapter(adapter);

        getPosts();
        PostCount();


        if (user.getUid().equals(user.getUid())) {
            btn_follow.setVisibility(View.GONE);
            btn_following.setVisibility(View.GONE);
        } else {
            btn_follow.setVisibility(View.VISIBLE);
            btn_following.setVisibility(View.VISIBLE);
        }

        isFollowing(user.getUid(), btn_follow,btn_following);


        btn_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btn_follow.getText().toString().equals("Follow")) {

                    FirebaseDatabase.getInstance().getReference().child("Follow")
                            .child(user.getUid())
                            .child("following")
                            .child(user.getUid())
                            .setValue(true);


                    FirebaseDatabase.getInstance().getReference()
                            .child("Follow")
                            .child(user.getUid())
                            .child("followers")
                            .child(user.getUid())
                            .setValue(true);
                }
            }
        });
        btn_following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btn_following.getText().toString().equals("Following")) {
                    FirebaseDatabase.getInstance().getReference().child("Follow")
                            .child(user.getUid())
                            .child("following")
                            .child(user.getUid())
                            .removeValue();

                    FirebaseDatabase.getInstance().getReference().child("Follow")
                            .child(user.getUid())
                            .child("followers").child(user.getUid())
                            .removeValue();
                }
            }
        });


    }
    // reference.child(user.getUid()).addValueEventListener(new ValueEventListener() {

    //getUserData firebase--->username,number,bio,profile---add
    private void getUserData() {
        reference.child(Id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String n = snapshot.child("username").getValue().toString();
                String cn = snapshot.child("contactNumber").getValue().toString();
                //bio
                /*if (snapshot.child("bio").exists())
                {
                    String b = snapshot.child("bio").getValue().toString();
                    bio.setText(b);
                }else
                {
                    bio.setText("bio");
                }
                //Picture profile
                if (snapshot.child("profile").exists())
                {
                    String p = snapshot.child("profile").getValue().toString();
                    Picasso.get().load(p).placeholder(R.drawable.profile_image).into(profile_image);
                }else
                {
                    profile_image.setImageResource(R.drawable.profile_image);
                }*/
                username.setText(n);
                number.setText(cn);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //edit profile=get item to profile activity
    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentId = Id;
        DocumentReference reference;
        FirebaseFirestore firebase = FirebaseFirestore.getInstance();

        reference = firebase.collection("user").document(currentId);

        reference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.getResult().exists()) {
                            String usernameResult = task.getResult().getString("username");
                            String bioResult = task.getResult().getString("bio");
                            String professionResult = task.getResult().getString("profession");
                            String numberResult = task.getResult().getString("contactNumber");
                            String addressResult = task.getResult().getString("address");
                            String url = task.getResult().getString("url");


                            Picasso.get().load(url).into(profile_image);

                            username.setText(usernameResult);
                            bio.setText(bioResult);
                            profession.setText(professionResult);
                            number.setText(numberResult);
                            address.setText(addressResult);
                        } else {
                            Intent intent = new Intent(OthersProfileActivity.this, CreateProfileActivity.class);
                            startActivity(intent);
                        }

                    }
                });
    }


    private void getFollowCount() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Follow")
                .child(user.getUid()).child("followers");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followers_count.setText("" + snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(OthersProfileActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void getFollowersCount() {

        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child("Follow")
                .child(user.getUid()).child("following");
        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                following_count.setText("" + snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(OthersProfileActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }


    //my post
    private void getPosts() {

        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                postList.clear();
                progressBar.setVisibility(View.GONE);

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);

                    if (post.getPublisher().equals(Id) ) {//others profile
                        postList.add(post);
                    }
                    //postList.add(post); //or all post
                }
                Collections.reverse(postList);//last post fast see
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //post count
    private void PostCount() {
        DatabaseReference postCount=FirebaseDatabase.getInstance().getReference().child("Posts");
        postCount.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i=0;

                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    String p=dataSnapshot.child("publisher").getValue().toString();
                    if (p.equals(user.getUid()))
                    {
                        i++;
                    }
                }
                //posts_count.setText("Posts "+"("+i+")");
                posts_count.setText(i+"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(getApplicationContext(), "Error "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }






    //private void isFollowing(final String userid, final Button follow, final Button following) {

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