package com.varsitycollege.mapsapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //Navigates to Register Form
    public void registerClick(View view) {
        Intent i = new Intent(MainActivity.this,Register.class);
        startActivity(i);

    }

    //Navigates to Login Form
    public void loginClick(View view) {
        Intent i = new Intent(MainActivity.this,Login.class);
        startActivity(i);
    }
}