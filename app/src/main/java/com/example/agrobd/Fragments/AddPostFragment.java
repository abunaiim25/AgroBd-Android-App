package com.example.agrobd.Fragments;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.agrobd.MainActivity;
import com.example.agrobd.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import static android.app.Activity.RESULT_OK;


public class AddPostFragment extends Fragment {

    ImageView cancel,selected_image;
    TextView post;
    EditText description;
    //Button pickImageBt;

    Uri imageUri;
    String url;
    public static final int PICK_IMAGE=100;

    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference reference;
    StorageReference storageReference;

    Handler handler = new Handler();
    int status = 0;
    Button button;
    ProgressDialog progressDialog;



    public AddPostFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_post,container,false);



        cancel=view.findViewById(R.id.cancel_post);
        post=view.findViewById(R.id.post_upload);
        selected_image=view.findViewById(R.id.selected_image);
        description=view.findViewById(R.id.post_description);
        //pickImageBt=view.findViewById(R.id.pickImageBt);

        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        storageReference= FirebaseStorage.getInstance().getReference().child("Post image/");

        //progressDialog
        ShowProgressDialog();

        //selected image from gallery
        selected_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent (Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,PICK_IMAGE);
            }
        });



        //cancel
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(),MainActivity.class));
            }
        });


//post
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (imageUri == null)
                {
                    Toast.makeText(getContext(), "No image selected", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    uploadPost();
                }

            }
        });


        return view;
    }//first bracket

    //selected image from gallery
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==PICK_IMAGE && resultCode == RESULT_OK && data.getData() != null)
        {
            imageUri = data.getData();
            selected_image.setImageURI(imageUri);
        }else
        {
            Toast.makeText(getContext(), "No Image Selected", Toast.LENGTH_SHORT).show();
            selected_image.setImageResource(R.drawable.ic_baseline_add_photo_alternate_24);
        }
    }

    public String getExtensionFile(Uri uri)
    {
        ContentResolver contentResolver = getActivity().getContentResolver();
        MimeTypeMap map = MimeTypeMap.getSingleton();
        return map.getExtensionFromMimeType(contentResolver.getType(uri));///
    }
/*
private String getExtensionFile(Uri uri)
{
    String extension;
    ContentResolver contentResolver = getActivity().getContentResolver();
    MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
    extension= mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    return extension;
}
*/

    //upload post
private void uploadPost()
{
    /*
    progressDialog=new ProgressDialog(getContext());
    progressDialog.setTitle("New Post");
    progressDialog.setMessage("Please wait..");
    progressDialog.setCanceledOnTouchOutside(false);
    progressDialog.show();*/
    progressDialog = new ProgressDialog(getContext());
    progressDialog.setIndeterminate(false);
    progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
    progressDialog.setTitle("New Post");
    progressDialog.setCancelable(true);
    progressDialog.setMax(100);
    progressDialog.show();


    if (imageUri != null)
    {

        StorageReference sRef = storageReference.child(System.currentTimeMillis()+"."+getExtensionFile(imageUri));

        sRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                sRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        url =uri.toString();

                        reference= FirebaseDatabase.getInstance().getReference().child("Posts");

                        //String timestamp = String.valueOf(System.currentTimeMillis());//for time
                        String timestamp = System.currentTimeMillis()+"";//

                        String postId = reference.push().getKey();

                        HashMap<String,Object> map = new HashMap<>();

                        map.put("postId",postId);
                        map.put("postImage",url);
                        map.put("description",description.getText().toString());
                        map.put("publisher",user.getUid());
                        map.put("date",timestamp);

                        progressDialog.dismiss();
                        reference.child(postId).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful())
                                {
                                    Toast.makeText(getContext(), "Post Uploaded", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getContext(),MainActivity.class));
                                    //finish();
                                }else
                                {
                                    Toast.makeText(getContext(), "Failed"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Failed"+e.getMessage(), Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        });
            }
        });
    }
}


    //progressDialog
    public void ShowProgressDialog()
    {
        status = 0;

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(status < 100){

                    status +=1;

                    try{
                        Thread.sleep(200);
                    }catch(InterruptedException e){
                        e.printStackTrace();
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                progressDialog.setProgress(status);
                            }catch (NullPointerException nullPointerException)
                            {

                            }
                            if(status == 100){
                                try {
                                    progressDialog.dismiss();
                                }catch (NullPointerException nullPointerException)
                                {

                                }

                            }
                        }
                    });
                }
            }
        }).start();
    }

}