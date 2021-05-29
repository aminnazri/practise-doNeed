package com.example.practisedoneed;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.practisedoneed.fragment.HomeFragment;
import com.example.practisedoneed.fragment.chatFragment;
import com.example.practisedoneed.fragment.donateFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.practisedoneed.databinding.ActivityHomePageBinding;
import com.google.firebase.auth.FirebaseAuth;

public class homePage extends AppCompatActivity {

    BottomNavigationView bottomNavigationView ;
    Fragment selectedFagrament = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        bottomNavigationView  = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemReselectedListener(navigationItemSelectedListner);

        Bundle intent = getIntent().getExtras();
    }

    private  BottomNavigationView.OnNavigationItemReselectedListener navigationItemSelectedListner =
            new BottomNavigationView.OnNavigationItemReselectedListener() {
                @Override
                public void onNavigationItemReselected(@NonNull MenuItem menuItem) {

                    switch (menuItem.getItemId()) {
                        case R.id.navigation_home:
                            selectedFagrament = new HomeFragment();
                            break;
                        case R.id.navigation_donate:
                            selectedFagrament = null;
                            startActivity(new Intent(homePage.this, donate.class));
                            break;
                        case R.id.navigation_chatting:
                            selectedFagrament = null;
                            startActivity(new Intent(homePage.this, chat.class));
                            break;

                    }



                    if (selectedFagrament != null) {

                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                selectedFagrament).commit();

                    }
                    return ;
                }

            };

}