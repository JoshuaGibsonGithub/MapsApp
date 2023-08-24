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
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText edtRegisterEmail, edtRegisterPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        edtRegisterEmail = (EditText) findViewById(R.id.edtRegisterEmail);
        edtRegisterPassword = (EditText) findViewById(R.id.edtRegisterPassword);
    }

    private void RegisterUser() {
        String email = edtRegisterEmail.getText().toString().trim();
        String password = edtRegisterPassword.getText().toString().trim();

        //Checks whether email field is empty
        if(email.isEmpty()){
            edtRegisterEmail.setError("Email is required");
            edtRegisterEmail.requestFocus();
            return;
        }

        //Check whether email is valid
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            edtRegisterEmail.setError("Please provide a valid email");
            edtRegisterEmail.requestFocus();
            return;
        }

        //Checks whether password field is empty
        if(password.isEmpty()){
            edtRegisterPassword.setError("Password is required");
            edtRegisterPassword.requestFocus();
            return;
        }

        //Checks whether password is 6 characters or longer
        if(password.length() < 6){
            edtRegisterPassword.setError("Min password length should be 6 characters");
            edtRegisterPassword.requestFocus();
            return;
        }

        //User authentication using FireBase (Email/Password)
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {


                        //Sends email to firebase database
                        if (task.isSuccessful()) {
                            User user = new User(email, null, null);
                            Toast.makeText(Register.this, "User has been registered", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(Register.this, MainActivity.class));



                        } else {
                            //Alerts user if registration is unsuccessful
                            Toast.makeText(Register.this, "User has not been registered. Please try again", Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

    public void registerNewUserClick(View view) {
        RegisterUser();
    }

    public void MainMenuClick(View view) {
        startActivity(new Intent(Register.this, MainActivity.class));
    }
}