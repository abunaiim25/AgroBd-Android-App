package com.example.agrobd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.agrobd.Adapter.CommentAdapter;
import com.example.agrobd.Model.CommentList;
import com.example.agrobd.Model.Post;
import com.example.agrobd.Model.UserList;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentActivity extends AppCompatActivity {



    //CircleImageView profile;
    EditText input_comment;
    ImageView send;

    RecyclerView recyclerView;
    List<CommentList> lists;
    CommentAdapter adapter;

    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference reference;

    String postId,publisher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);


        //profile=findViewById(R.id.profile_image);
        send=findViewById(R.id.post);
        input_comment=findViewById(R.id.add_comment);

        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference().child("Users");

        postId = getIntent().getStringExtra("postId");
        publisher = getIntent().getStringExtra("publisher");

        recyclerView=findViewById(R.id.recyclerView);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));


        getComment();






        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = input_comment.getText().toString();

                if (comment.isEmpty())
                {
                    Toast.makeText(CommentActivity.this, "Empty comment can't be send", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    sendComment(comment);
                }
            }
        });

    }//first bracket


    private void sendComment(String comment) {

        String timestamp = System.currentTimeMillis()+"";
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Comments").child(postId);

        HashMap<String,Object> map = new HashMap<>();
        map.put("comment",comment);
        map.put("publisher",user.getUid());
        map.put("date",timestamp);

        databaseReference.push().setValue(map);
        input_comment.setText("");

    }


    private void getComment() {
        lists=new ArrayList<>();

        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child("Comments")
                .child(postId);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                lists.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    CommentList commentList=dataSnapshot.getValue(CommentList.class);
                    lists.add(commentList);
                }
                adapter= new CommentAdapter(getApplicationContext(),lists);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(CommentActivity.this, "Error while load comments", Toast.LENGTH_SHORT).show();
            }
        });
    }





}