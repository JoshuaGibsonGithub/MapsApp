package com.varsitycollege.mapsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class Favourites {
    String favourite;


    public Favourites(String favourite) {
        this.favourite = favourite;
    }

    public String getFavourite() {
        return favourite;
    }

    public void setFavourite(String favourite) {
        this.favourite = favourite;
    }


}