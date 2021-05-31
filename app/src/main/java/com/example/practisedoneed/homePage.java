package com.example.practisedoneed;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.practisedoneed.fragment.HomeFragment;
import com.example.practisedoneed.fragment.profileFragment;
import com.example.practisedoneed.fragment.donateFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

public class homePage extends AppCompatActivity {

    Button logout;
    BottomNavigationView bottomNavigationView ;
    Fragment selectedFagrament = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home_page);
        bottomNavigationView  = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListner);
        Bundle intent = getIntent().getExtras();


        // Default Fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new HomeFragment())
                .commit();

        logout = findViewById(R.id.logout);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(homePage.this, MainActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });
    }




    private final BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListner =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                    switch (menuItem.getItemId()) {
                        case R.id.navigation_home:
                            selectedFagrament = new HomeFragment();
                            break;
                        case R.id.navigation_donate:
                            selectedFagrament = new donateFragment();
//
                            break;
                        case R.id.navigation_chatting:
                            selectedFagrament = new profileFragment();

                            break;

                    }

                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                selectedFagrament).commit();


                    return true;
                }

            };


}