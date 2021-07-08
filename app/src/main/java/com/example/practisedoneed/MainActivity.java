package com.example.practisedoneed;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;

import maes.tech.intentanim.CustomIntent;

//Main class
public class MainActivity extends AppCompatActivity {

    Button login, register;
    FirebaseUser firebaseUser;
    int NightMode;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    //check user already login or not
    protected void onStart() {
        super.onStart();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        //check nigh mode or not and set the mode
        sharedPreferences = getSharedPreferences("PREFS",MODE_PRIVATE);
        NightMode = sharedPreferences.getInt("NightModeInt", 1);
        AppCompatDelegate.setDefaultNightMode(NightMode);

        //check whether user already log in or not
        //if yes, go straight to feed page, no need to login again
        if (firebaseUser != null) {
            startActivity(new Intent(MainActivity.this, homePage.class));
            finish();
        }
    }

    //function on startup
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        login = findViewById(R.id.login_btn);
        register = findViewById(R.id.signUp_btn);

        //click login button
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
                CustomIntent.customType(MainActivity.this,"left-to-right");
                finishAfterTransition();
            }
        });

        //click register button
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,SignUpActivity.class);
                startActivity(intent);
                CustomIntent.customType(MainActivity.this,"left-to-right");
            }
        });
    }

    //                overridePendingTransition(R.anim.test,R.anim.slide_out);

    public void openActivity2(View view){
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out);
    }


    //Save apps state to use after reopen apps
    @Override
    protected void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        super.onSaveInstanceState(outState);

        //save mode whether night or light mode
        NightMode = AppCompatDelegate.getDefaultNightMode();
        sharedPreferences = getSharedPreferences("PREFS", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putInt("NightModeInt", NightMode);
        editor.apply();
    }
}