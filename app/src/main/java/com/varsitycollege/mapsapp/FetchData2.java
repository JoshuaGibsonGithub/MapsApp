package com.varsitycollege.mapsapp;

import android.os.AsyncTask;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class FetchData2 extends AsyncTask<Object,String,String> {

    String googleNearByPlacesData;
    GoogleMap googleMap;
    String url;

    @Override
    protected void onPostExecute(String s) {
        JSONObject obj = null;
        try {
            obj = new JSONObject(s);
        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
        }
        JSONArray routes = obj.optJSONArray("routes");
        if (routes != null) {
            for (int i = 0; i < routes.length(); i++) {
                JSONObject objAtIndex = routes.optJSONObject(i);
                if (objAtIndex != null) {
                    JSONArray legs = objAtIndex.optJSONArray("legs");

                    for (int k = 0; k < legs.length(); k++) {

                        JSONObject jsonObject1 = null;
                        try {
                            jsonObject1 = legs.getJSONObject(i);
                        } catch (JSONException jsonException) {
                            jsonException.printStackTrace();
                        }
                        JSONObject getLocation = null;
                        try {
                            getLocation = jsonObject1.getJSONObject("distance");
                            String lol = getLocation.getString("text");
                            System.out.println(lol);
                        } catch (JSONException jsonException) {
                            jsonException.printStackTrace();
                        }

                    }

                }
            }
        }


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

