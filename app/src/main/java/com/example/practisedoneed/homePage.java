package com.example.practisedoneed;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.practisedoneed.fragment.HomeFragment;
import com.example.practisedoneed.fragment.profileFragment;
import com.example.practisedoneed.fragment.donateFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

public class homePage extends AppCompatActivity {

    Button logout;
    BottomNavigationView bottomNavigationView ;
    Fragment selectedFagrament = null;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    String fragmentName = "home";
    int check;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home_page);
        bottomNavigationView  = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListner);
        Bundle intent = getIntent().getExtras();
        check=0;


        // Default Fragment
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.fragment_container, new HomeFragment())
                .addToBackStack("home")
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
                            fragmentName="home";
                            break;
                        case R.id.navigation_donate:
                            selectedFagrament = new donateFragment();
                            fragmentName="donate";
                            break;
                        case R.id.navigation_profile:
                            selectedFagrament = new profileFragment();
                            fragmentName="profile";
                            break;

                    }
                        fragmentManager.beginTransaction().replace(R.id.fragment_container,
                                selectedFagrament)
                                .addToBackStack(fragmentName)
                                .commit();
                    return true;
                }
            };

    //ni utk sync top toolbar dgn bottom
    @Override
    public void onBackPressed() {

        if(fragmentManager.getBackStackEntryCount() > 0){

            fragmentManager.popBackStack();
            int count = fragmentManager.getBackStackEntryCount();
            String currentFragment=null;
            if(count-2>=0){
                currentFragment=fragmentManager.getBackStackEntryAt(count-2).getName();

                Log.i(TAG,String.valueOf(count));
                Log.i(TAG,fragmentManager.getBackStackEntryAt(0).getName());
                //check your position based on selected fragment and set it accordingly.
                assert currentFragment != null;
                if(currentFragment.equals("home")){

                    bottomNavigationView.getMenu().getItem(0).setChecked(true);
                }else if(currentFragment.equals("donate")){

                    bottomNavigationView.getMenu().getItem(1).setChecked(true);
                }else if(currentFragment.equals("profile")){

                    bottomNavigationView.getMenu().getItem(2).setChecked(true);
                }
            }else{
                super.onBackPressed();
            }
        }
        else {
            super.onBackPressed();
        }
    }

    //    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        return super.onCreateOptionsMenu(menu);
//    }

}