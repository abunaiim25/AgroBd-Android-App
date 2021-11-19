package com.example.agrobd;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.agrobd.Fragments.ProfileFragment;
import com.example.agrobd.Model.UserList;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class CreateProfileActivity extends AppCompatActivity {

    ImageView  profile_image;
    EditText username,profession,number,address,bio;
    ProgressBar progressBar;
    Button edit_profile;

    FirebaseAuth auth;//
    FirebaseUser user;
    StorageReference storageReference;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference documentReference;

    Uri imageUri;
    UploadTask uploadTask;
    private static final int PICK_IMAGE = 1;
    String currentUserId;

    UserList member;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        member = new UserList();

        profile_image=findViewById(R.id.profile_image);
        username=findViewById(R.id.username);
        profession=findViewById(R.id.profession);
        number=findViewById(R.id.number);
        address=findViewById(R.id.address);
        bio=findViewById(R.id.bio);
        progressBar=findViewById(R.id.progressBar);
        edit_profile=findViewById(R.id.edit_profile);
        //backButton=findViewById(R.id.backButton);

/*
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.createProfile,new ProfileFragment()).commit();
            }
        });
*/


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        currentUserId = user.getUid();


        documentReference = db.collection("user").document(currentUserId);
        storageReference = FirebaseStorage.getInstance().getReference("Profile images");
        databaseReference = database.getReference("Users");



        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    uploadData();//
                }catch (Exception e)
                {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(CreateProfileActivity.this, "Please Insert Image and All Profile Bio", Toast.LENGTH_SHORT).show();
                }
            }
        });



        //select image
        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,PICK_IMAGE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (requestCode==PICK_IMAGE || resultCode==RESULT_OK || data!=null || data.getData() != null)
            {
                imageUri = data.getData();
                Picasso.get().load(imageUri).into(profile_image);
            }
        }catch (Exception e)
        {
            Toast.makeText(this, "Error"+e, Toast.LENGTH_SHORT).show();
        }

    }

    public String getFileExt(Uri uri)
    {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap map = MimeTypeMap.getSingleton();
        return map.getExtensionFromMimeType(contentResolver.getType(uri));///
    }

    private void uploadData() {

        String u = username.getText().toString();
        String p = profession.getText().toString();
        String cn = number.getText().toString();
        String a = address.getText().toString();
        String b = bio.getText().toString();

        if (!TextUtils.isEmpty(u) || !TextUtils.isEmpty(p) || !TextUtils.isEmpty(cn) ||
                !TextUtils.isEmpty(a) || !TextUtils.isEmpty(b) || imageUri != null)
        {
            progressBar.setVisibility(View.VISIBLE);

            final StorageReference reference = storageReference.child(System.currentTimeMillis()+"."+getFileExt(imageUri));//
            uploadTask = reference.putFile(imageUri);

            Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful())
                    {
                        throw task.getException();
                    }
                    return reference.getDownloadUrl();
                }
            })
                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {

                            if (task.isSuccessful())
                            {
                                Uri downloadUri = task.getResult();

                                Map<String,String> profile = new HashMap<>();

                                profile.put("username",u);
                                profile.put("profession",p);
                                profile.put("url",downloadUri.toString());
                                profile.put("contactNumber",cn);
                                profile.put("address",a);
                                profile.put("bio",b);
                                profile.put("user_id",currentUserId);
                                profile.put("privacy","public");

                                member.setUsername(u);
                                member.setContactNumber(cn);
                                member.setUser_id(currentUserId);
                                member.setUrl(downloadUri.toString());

                                databaseReference.child(currentUserId).setValue(member);

                                documentReference.set(profile)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                progressBar.setVisibility(View.INVISIBLE);
                                                Toast.makeText(CreateProfileActivity.this, "Profile Created", Toast.LENGTH_SHORT).show();

                                                Handler handler = new Handler();
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Intent intent = new Intent(CreateProfileActivity.this, MainActivity.class);
                                                        startActivity(intent);
                                                        /*FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                                                        fragmentTransaction.replace(R.id.createProfile,new ProfileFragment()).commit();*/
                                                    }

                                                },2000);
                                            }
                                        });
                            }
                        }
                    });

        }
        Toast.makeText(this, "Please fill all Fields", Toast.LENGTH_SHORT).show();
    }



}