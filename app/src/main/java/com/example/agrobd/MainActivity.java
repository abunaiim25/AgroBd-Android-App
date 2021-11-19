package com.example.agrobd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.example.agrobd.Fragments.AddPostFragment;
import com.example.agrobd.Fragments.ContactFragment;
import com.example.agrobd.Fragments.HomeFragment;
import com.example.agrobd.Fragments.ProfileFragment;
import com.example.agrobd.Fragments.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        bottomNavigationView=findViewById(R.id.bottom_nav);




        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.home)
                {
                    HomeFragment homeFragment = new HomeFragment();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.frameLayout, homeFragment);
                    fragmentTransaction.commit();
                }
                else if (id == R.id.search)
                {
                    SearchFragment searchFragment = new SearchFragment();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.frameLayout, searchFragment);
                    fragmentTransaction.commit();
                }
                else if (id == R.id.addPost) {
                    AddPostFragment addPostFragment = new AddPostFragment();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.frameLayout, addPostFragment);
                    fragmentTransaction.commit();
                }
                else if (id == R.id.contact) {
                    ContactFragment contactFragment = new ContactFragment();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.frameLayout, contactFragment);
                    fragmentTransaction.commit();
                }
                else if (id == R.id.profile) {
                    ProfileFragment profileFragment = new ProfileFragment();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.frameLayout, profileFragment);
                    fragmentTransaction.commit();

                    SharedPreferences.Editor editor = getSharedPreferences("USER", Context. MODE_PRIVATE).edit();
                    editor.putString("profileId", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    //editor.putString("profileId", user.getUid());
                    editor.apply();
                }


                return true;
            }
        });

        bottomNavigationView.setSelectedItemId(R.id.home);


    }
}