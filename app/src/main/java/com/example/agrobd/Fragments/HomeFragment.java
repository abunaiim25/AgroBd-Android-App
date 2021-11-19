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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.agrobd.Adapter.PostAdapter;
import com.example.agrobd.Model.Post;
import com.example.agrobd.R;
import com.example.agrobd.SettingsMenuActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class HomeFragment extends Fragment {

    FrameLayout frameLayout;
    ImageView menu;

    RecyclerView recyclerView;
    List<Post> postList;
    PostAdapter adapter;
    ProgressBar progressBar;

    FirebaseUser user;
    DatabaseReference reference;
    FirebaseAuth auth;

    List<String> followingList;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home,container,false);


        frameLayout=view.findViewById(R.id.frameLayout);
        menu=view.findViewById(R.id.menu);


        progressBar=view.findViewById(R.id.progressbar);
        progressBar.setVisibility(View.VISIBLE);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));//


        auth= FirebaseAuth.getInstance();
        user=auth.getCurrentUser();

        reference = FirebaseDatabase.getInstance().getReference().child("Users");//




        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), SettingsMenuActivity.class));
            }
        });


        checkFollow();

        return view;
    }//first

    private void checkFollow() {
        followingList = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow")
                .child(user.getUid())
                .child("following");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    followingList.add(dataSnapshot.getKey());
                }

                getPosts();

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

}//second
    private void getPosts() {
        postList =new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Posts");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                progressBar.setVisibility(View.GONE);

                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    Post post = dataSnapshot.getValue(Post.class);

                    for (String id : followingList)
                    {
                        if (post.getPublisher().equals(id))
                        {
                            postList.add(post);
                        }
                    }
                }
                Collections.reverse(postList);//last post fast see
                adapter = new PostAdapter(getContext(),postList);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}