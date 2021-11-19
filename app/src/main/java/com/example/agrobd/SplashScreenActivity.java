package com.example.agrobd;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreenActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);


        auth=FirebaseAuth.getInstance ();
        user=auth.getCurrentUser ();

        new Handler(  ).postDelayed (new Runnable () {
            @Override
            public void run() {
                if (user !=null)
                {
                    startActivity ( new Intent( SplashScreenActivity.this,MainActivity.class ) );
                    finish();
                }
                else
                {
                    startActivity ( new Intent ( SplashScreenActivity.this, LoginSignUpActivity.class ) );
                    finish();
                }
            }
        },2500 );
    }

}