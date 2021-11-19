package com.example.agrobd;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class SettingsMenuActivity extends AppCompatActivity {

    Button logout;
    RelativeLayout saveR,editProfileR;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_menu);

        logout=findViewById(R.id.logout);
        saveR=findViewById(R.id.saveR);
        editProfileR=findViewById(R.id.editProfileR);


        auth= FirebaseAuth.getInstance();



        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                startActivity(new Intent(SettingsMenuActivity.this, LoginSignUpActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });


        saveR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent = new Intent(SettingsMenuActivity.this,SavedActivity.class);
               startActivity(intent);
            }
        });



        editProfileR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsMenuActivity.this,CreateProfileActivity.class);
                startActivity(intent);
            }
        });
    }



}
