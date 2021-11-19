package com.example.agrobd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    EditText passwordET;
    CheckBox passwordCB;
    TextView registerToLogin;
    EditText username,contactNumber,email;
    Button registration;
    ProgressDialog progressDialog;

    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        //find id
        passwordET=findViewById(R.id.createPassword);
        passwordCB=findViewById(R.id.checkbox);
        registerToLogin=findViewById(R.id.registerToLogin);
        username=findViewById(R.id.username);
        contactNumber=findViewById(R.id.contactNumber);
        email=findViewById(R.id.email);
        passwordET=findViewById(R.id.createPassword);
        registration=findViewById(R.id.registration);


        //firebase
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference().child("Users");


        //show password
       passwordCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
           @Override
           public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

               if (isChecked)
               {
                   passwordET.setTransformationMethod(null);
               }
               else
               {
                   passwordET.setTransformationMethod(new PasswordTransformationMethod());
               }
           }
       });


       //register to login page
        registerToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });


        //registration
        registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String n=username.getText().toString();
                String cn=contactNumber.getText().toString();
                String e=email.getText().toString();
                String p=passwordET.getText().toString();

                if (n.isEmpty())
                {
                    username.setError("Please enter name");
                }else if (cn.isEmpty())
                {
                    contactNumber.setError("Please enter contact number");
                }else if (e.isEmpty())
                {
                    email.setError("Please enter email address");
                }else if (p.isEmpty())
                {
                    passwordET.setError("Password cannot be empty..!");
                }else
                {
                    progressDialog=new ProgressDialog(RegisterActivity.this);
                    progressDialog.setTitle("Registering");
                    progressDialog.setMessage("Please wait...");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    auth.createUserWithEmailAndPassword(e,p).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful())
                            {
                                auth=FirebaseAuth.getInstance();
                                user=auth.getCurrentUser();

                                HashMap<String,Object> map=new HashMap<>();
                                map.put("username",n);
                                map.put("user_id",user.getUid());
                                map.put("contactNumber",cn);
                                map.put("email",e);
                                map.put("password",p);


                                reference.child(user.getUid()).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful())
                                        {
                                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                            Toast.makeText(RegisterActivity.this, "Account created!!!", Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                        }else
                                        {
                                            progressDialog.dismiss();
                                            Toast.makeText(RegisterActivity.this, "Something went wrong "+task.getException(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            }else
                            {
                                progressDialog.dismiss();
                                Toast.makeText(RegisterActivity.this, "Unable to register.."+task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        });

    }//end first bracket

}
