package com.example.practisedoneed;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.r0adkll.slidr.Slidr;

import org.jetbrains.annotations.NotNull;

//Reset password class
public class ResetPassword extends AppCompatActivity {

    private EditText email_text;
    private Button ResetSubmit_btn;
    private ProgressBar progressBar;
    private TextView txt_login;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Slidr.attach(this);
        setContentView(R.layout.activity_reset_password);

        txt_login = findViewById(R.id.txt_login);
        ResetSubmit_btn = findViewById(R.id.ResetSubmit_btn);
        progressBar = findViewById(R.id.progressBar);
        email_text = findViewById(R.id.email_text);
        auth = FirebaseAuth.getInstance();

        ResetSubmit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });

        txt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ResetPassword.this, LoginActivity.class));
            }
        });


    }

    //RESET PASSWORD FUNCTION
    private void resetPassword() {

        String email = email_text.getText().toString().trim();

        //Check if the password filed is empty or not
        if(email.isEmpty()){
            email_text.setError("Email is required!");
            email_text.requestFocus();
            return;
        }

        //check if the email follow an email format
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            email_text.setError("Please provide valid email!");
            email_text.requestFocus();
            return;
        }

        //pop up progress bar
        progressBar.setVisibility(View.VISIBLE);
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(ResetPassword.this, "Check your email to reset your password", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                    startActivity(new Intent(ResetPassword.this, LoginActivity.class));
                }
                else{
                    Toast.makeText(ResetPassword.this, "Try again! Something wrong happend!", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

}