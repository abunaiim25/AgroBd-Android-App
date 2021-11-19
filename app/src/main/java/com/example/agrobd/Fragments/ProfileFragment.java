package com.example.agrobd.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.agrobd.Adapter.PostAdapter;
import com.example.agrobd.CreateProfileActivity;
import com.example.agrobd.Model.Post;
import com.example.agrobd.OthersProfileActivity;
import com.example.agrobd.R;
import com.example.agrobd.SettingsMenuActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {

    TextView username,posts_count,followers_count,following_count,number,address,bio,profession ;
    ImageView menu;
    CircleImageView profile_image;
    Button edit_profile;
    ProgressBar progressBar;

    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference reference;//

    //my post
    DatabaseReference databaseRef;
    RecyclerView recyclerView;
    List<Post> postList;
    PostAdapter adapter;





    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile,container,false);


        username=view.findViewById(R.id.username);
        posts_count=view.findViewById(R.id.posts_count);
        followers_count=view.findViewById(R.id.followers_count);
        following_count=view.findViewById(R.id.following_count);
        number=view.findViewById(R.id.number);
        address=view.findViewById(R.id.address);
        bio=view.findViewById(R.id.bio);
        profile_image=view.findViewById(R.id.profile_image);
        edit_profile=view.findViewById(R.id.edit_profile);
        profession=view.findViewById(R.id.profession);
        recyclerView=view.findViewById(R.id.recyclerView);
        menu=view.findViewById(R.id.menu);

        progressBar=view.findViewById(R.id.progressbar);
        progressBar.setVisibility(View.VISIBLE);


        auth= FirebaseAuth.getInstance();
        user= auth.getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference().child("Users");

        //get data to firebase
        getUserData();


        //follow and following count
        getFollowersCount();
        getFollowCount();


        //my post
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        user = FirebaseAuth.getInstance().getCurrentUser();
        databaseRef = FirebaseDatabase.getInstance().getReference().child("Posts");

        postList = new ArrayList<>();
        adapter = new PostAdapter(getContext(),postList);
        recyclerView.setAdapter(adapter);

        getPosts();

        //post count
        PostCount();





        //settings
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), SettingsMenuActivity.class));
            }
        });


        //edit Profile
        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CreateProfileActivity.class);
                startActivity(intent);
            }
        });




        return view;
    }

    //getUserData firebase--->username,number,bio,profile---add
    private void getUserData()
    {
        reference.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                String n=snapshot.child("username").getValue().toString();
                String cn=snapshot.child("contactNumber").getValue().toString();
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
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });
    }


    //edit profile=get item to profile activity
    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentId = user.getUid();
        DocumentReference reference;
        FirebaseFirestore firebase = FirebaseFirestore.getInstance();

        reference = firebase.collection("user").document(currentId);

        reference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.getResult().exists())
                        {
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
                        }
                        else
                        {
                            Intent intent = new Intent(getActivity(),CreateProfileActivity.class);
                            startActivity(intent);
                        }

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

                Toast.makeText(getActivity(), "Error "+error.getMessage(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void getFollowersCount() {

        DatabaseReference databaseReference1=FirebaseDatabase.getInstance().getReference().child("Follow")
                .child(user.getUid()).child("following");
        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                following_count.setText(""+snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }


    //my post
    private void getPosts() {

        databaseRef = FirebaseDatabase.getInstance().getReference().child("Posts");

        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                postList.clear();
                progressBar.setVisibility(View.GONE);

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);

                    if (post.getPublisher().equals(user.getUid())) { //this code for only my post
                        postList.add(post);
                    }
                   // postList.add(post);
                }
                Collections.reverse(postList);//last post fast see
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}