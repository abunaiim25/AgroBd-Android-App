package com.example.agrobd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.agrobd.Adapter.SavedAdapter;
import com.example.agrobd.Model.Post;
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

public class SavedActivity extends AppCompatActivity {

    private List<String> mySaves;

    RecyclerView recyclerView_save;
    List<Post> list;
    SavedAdapter adapter;

    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved);


        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        // reference= FirebaseDatabase.getInstance().getReference().child("Favourites");
        reference=FirebaseDatabase.getInstance().getReference().child("Users");



        recyclerView_save=findViewById ( R.id.recyclerView );
        recyclerView_save.setHasFixedSize (true);
        recyclerView_save.setLayoutManager(new GridLayoutManager(SavedActivity.this,3));
        //recyclerView_save.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        getSaved();
    }

    private void getSaved(){
        mySaves=new ArrayList<> ();
        DatabaseReference reference=FirebaseDatabase.getInstance ().getReference ().child ( "Favourites" )
                .child ( user.getUid () );

        reference.addValueEventListener ( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren ()){
                    mySaves.add ( snapshot.getKey () );
                }
                readSaves();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );
    }


    private void readSaves() {
        list=new ArrayList<>();

        DatabaseReference reference= FirebaseDatabase.getInstance ().getReference ().child ( "Posts" );

        reference.addValueEventListener ( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear ();
                for (DataSnapshot snapshot : dataSnapshot.getChildren ()){
                    Post post=snapshot.getValue (Post.class);

                    for (String id : mySaves){
                        if (post.getPostId ().equals ( id )){
                            list.add ( post );
                        }
                    }
                }
                Collections.reverse(list);//last post fast see
                adapter= new SavedAdapter(SavedActivity.this,list);
                recyclerView_save.setAdapter(adapter);
                adapter.notifyDataSetChanged ();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );
    }


}