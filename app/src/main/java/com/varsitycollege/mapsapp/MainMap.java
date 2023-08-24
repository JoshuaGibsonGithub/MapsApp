package com.varsitycollege.mapsapp;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainMap extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    Spinner spType, spUnit, spFavourites;
    Button btFind, btCalc, btSave;
    EditText edtDestination;
    SupportMapFragment supportMapFragment;
    GoogleMap map;
    FusedLocationProviderClient fusedLocationProviderClient;
    public static String unitType;
    double currentLat = 0, currentLong = 0;
    double endLat, endLong;
    //List<String> Favourites;
    DatabaseReference favouritesDBRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_map);

        spType = findViewById(R.id.sp_type);
        btFind = findViewById(R.id.bt_find);
        spUnit = findViewById(R.id.sp_unit);
        btCalc = findViewById(R.id.bt_calc);
        btSave = findViewById(R.id.bt_save);
        spFavourites = findViewById(R.id.spnFavourites);
        edtDestination = findViewById(R.id.edtDestination);
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.google_map);
        List<String> Favourites = new ArrayList<String>();


        //Arrays of landmark types
        String[] placeTypeList = {"atm", "bank", "hospital", "movie_theater", "restaurant", "parking"};
        String[] placeNameList = {"ATM", "Bank", "Hospital", "Movie Theater", "Restaurant", "Parking"};

        //Arrays of unit types
        String[] unitTypeList = {"metric", "imperial"};
        String[] unitNameList = {"Metric", "Imperial"};

        spUnit.setAdapter(new ArrayAdapter<>(MainMap.this,
                android.R.layout.simple_spinner_item, unitNameList));

        spType.setAdapter(new ArrayAdapter<>(MainMap.this,
                android.R.layout.simple_spinner_item, placeNameList));

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        //Checking for permission to get user location
        if (ActivityCompat.checkSelfPermission(MainMap.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {
            ActivityCompat.requestPermissions(MainMap.this
                    , new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }

        //Calculates route time and distance. Unit of measurement is changed between imperial and metric based on user input
        btCalc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                map.clear();
                getCurrentLocation();
                int i = spUnit.getSelectedItemPosition();
                unitType = unitTypeList[i];
                StringBuilder stringBuilder = new StringBuilder("https://maps.googleapis.com/maps/api/distancematrix/json");
                stringBuilder.append("?destinations=" + edtDestination.getText());
                stringBuilder.append("&origins=" + currentLat + "," + currentLong);
                stringBuilder.append("&units=" + unitType);
                stringBuilder.append("&key=" + getResources().getString(R.string.google_map_key));
                String url = stringBuilder.toString();
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(url)
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Response response) throws IOException {

                        if (response.isSuccessful()){
                            final String myResponse = response.body().string();

                            MainMap.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        JSONObject jsonDistance = new JSONObject(myResponse)
                                                .getJSONArray("rows")
                                                .getJSONObject(0)
                                                .getJSONArray("elements")
                                                .getJSONObject(0)
                                                .getJSONObject("distance");


                                        JSONObject jsonDuration = new JSONObject(myResponse)
                                                .getJSONArray("rows")
                                                .getJSONObject(0)
                                                .getJSONArray("elements")
                                                .getJSONObject(0)
                                                .getJSONObject("duration");

                                        String distance = jsonDistance.get("text").toString();
                                        String duration = jsonDuration.get("text").toString();
                                        String finalResult = distance + " : " + duration;
                                        Toast.makeText(MainMap.this, finalResult, Toast.LENGTH_LONG).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            });

                        }
                    }
                });
                System.out.println(url);



                StringBuilder stringBuilder2 = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json");
                stringBuilder2.append("?origin=" + currentLat + "," + currentLong);
                stringBuilder2.append("&destination=" + edtDestination.getText());
                stringBuilder2.append("&key=" + getResources().getString(R.string.google_map_key));
                String url2 = stringBuilder2.toString();
                System.out.println(url2);


                Object dataFetch3[] = new Object[2];
                dataFetch3[0] = map;
                dataFetch3[1] = url2;

                FetchData3 fetchData3 = new FetchData3();
                fetchData3.execute(dataFetch3);

            }
        });


        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = Login.email;
                favouritesDBRef = FirebaseDatabase.getInstance().getReference().child("Favourites");
                Favourites favourites = new Favourites(edtDestination.getText().toString());
                favouritesDBRef.push().setValue(favourites);
        /*        User user = new User(email, edtDestination.getText().toString(), unitType);
                FirebaseDatabase.getInstance().getReference("Favourites")
                        .child("favourite")
                        .push().setValue(edtDestination.getText().toString());*/

            }
        });

        //Finds 5 closest landmarks based on landmark type that the user has chosen. the landmarks are then added to the user database as favourites.
        btFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                map.clear();
                getCurrentLocation();
                int i = spType.getSelectedItemPosition();
                StringBuilder stringBuilder = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json");
                stringBuilder.append("?location=" + currentLat + "," + currentLong);
                stringBuilder.append("&radius=1000");
                stringBuilder.append("&type=" + placeTypeList[i]);
                stringBuilder.append("&sensor=true");
                stringBuilder.append("&key=" + getResources().getString(R.string.google_map_key));

                String url = stringBuilder.toString();

                System.out.println(url);
                Object dataFetch[] = new Object[2];
                dataFetch[0] = map;
                dataFetch[1] = url;

                FetchData fetchData = new FetchData();
                fetchData.execute(dataFetch);

            }
        });

        favouritesDBRef = FirebaseDatabase.getInstance().getReference();
        favouritesDBRef.child("Favourites").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()) {
                    String spinnerNew = dataSnapshot1.child("favourite").getValue(String.class);
                    Favourites.add(spinnerNew);
                }

                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(MainMap.this, android.R.layout.simple_spinner_item, Favourites);
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spFavourites.setAdapter(spinnerAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public void onMapReady(final GoogleMap googleMap) {
        map = googleMap;
        map.setOnMarkerClickListener(this);

    }


    //Gets the user's current location
    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLat = location.getLatitude();
                    currentLong = location.getLongitude();
                    supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(@NonNull GoogleMap googleMap) {
                            map = googleMap;
                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(currentLat, currentLong), 10
                            ));
                            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                            MarkerOptions options = new MarkerOptions().position(latLng).title("I am there").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                            map.addMarker(options);
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 44) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            }
        }
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        endLat = marker.getPosition().latitude;
        endLong = marker.getPosition().longitude;
        return false;
    }
}

