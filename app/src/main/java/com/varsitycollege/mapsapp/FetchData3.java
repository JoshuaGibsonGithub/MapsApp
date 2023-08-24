package com.varsitycollege.mapsapp;

import android.content.ContentProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Iterator;

public class FetchData3 extends AsyncTask<Object,String,String> {

    String googleNearByPlacesData;
    GoogleMap googleMap;
    String url;

    @Override
    protected void onPostExecute(String s) {
        try {
            JSONObject jsonDestination = new JSONObject(s)
                    .getJSONArray("routes")
                    .getJSONObject(0)
                    .getJSONArray("legs")
                    .getJSONObject(0)
                    .getJSONObject("end_location");

            JSONObject jsonOrigin = new JSONObject(s)
                    .getJSONArray("routes")
                    .getJSONObject(0)
                    .getJSONArray("legs")
                    .getJSONObject(0)
                    .getJSONObject("start_location");

            String Destlat = jsonDestination.getString("lat");
            String Destlng = jsonDestination.getString("lng");

            String Originlat = jsonOrigin.getString("lat");
            String Originlng = jsonOrigin.getString("lng");

            LatLng destLatLng = new LatLng(Double.parseDouble(Destlat), Double.parseDouble(Destlng));
            LatLng originLatLng = new LatLng(Double.parseDouble(Originlat), Double.parseDouble(Originlng));

            googleMap.addPolyline((new PolylineOptions()).add(destLatLng, originLatLng).
                    width(5)
                    .color(Color.RED)
                    .geodesic(true));

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(destLatLng).title("Chosen Destination");
            googleMap.addMarker(markerOptions);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destLatLng, 15));



        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
        }
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Object... objects) {

        try {

            googleMap = (GoogleMap) objects[0];
            url = (String) objects[1];
            DownloadUrl downloadUrl = new DownloadUrl();
            googleNearByPlacesData = downloadUrl.retrieveUrl(url);

        }catch (IOException e) {
            e.printStackTrace();

        }

        return googleNearByPlacesData;
    }
}

