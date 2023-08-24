package com.varsitycollege.mapsapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    private EditText edtLoginEmail, edtLoginPassword;
    public static String email;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtLoginEmail = (EditText) findViewById(R.id.edtLoginEmail);
        edtLoginPassword = (EditText) findViewById(R.id.edtLoginPassword);

        mAuth = FirebaseAuth.getInstance();
    }

    public void loginUserClick(View view) {
        LoginUser();
    }

    private void LoginUser() {
        email = edtLoginEmail.getText().toString().trim();
        String password = edtLoginPassword.getText().toString().trim();

        //Checks whether email field is empty
        if (email.isEmpty()){
            edtLoginEmail.setError("Email is required");
            edtLoginEmail.requestFocus();
            return;
        }

        //Checks whether email is valid
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            edtLoginEmail.setError("Please provide a valid email");
            edtLoginEmail.requestFocus();
            return;
        }

        //Checks whether password field is empty
        if (password.isEmpty()){
            edtLoginPassword.setError("Password is required");
            edtLoginPassword.requestFocus();
            return;
        }

        //Checks whether password is 6 characters or longer
        if (password.length() < 6){
            edtLoginPassword.setError("Min password length is 6 characters");
            edtLoginPassword.requestFocus();
            return;
        }

        //Uses FireBase authentication to check if email and password are valid
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    //Navigates to the Map if login successful
                    startActivity(new Intent(Login.this, MainMap.class));
                }else{
                    //Alerts the user if login unsuccessful
                    Toast.makeText(Login.this, "Please check your credentials", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //Navigates to the Main Menu
    public void MainMenuClick(View view) {
        startActivity(new Intent(Login.this, MainActivity.class));
    }
}