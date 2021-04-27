package com.example.exactogps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    //How often the application updates the location (default or fast)
    public static final int DEFAULT_UPDATE_INTERVAL = 30;
    public static final int FAST_UPDATE_INTERVAL = 5;
    private static final int PERMISSIONS_FINE_LOCATION = 99;

    //References to the UI elements

    TextView tv_lat;
    TextView tv_lon;
    TextView tv_altitude;
    TextView tv_accuracy;
    TextView tv_speed;
    TextView tv_sensor;
    TextView tv_updates;
    TextView tv_address;
    TextView tv_wayPointCounts;

    Button btn_newWaypoint;
    Button btn_showWayPointList;

    Switch sw_locationupdates;
    Switch sw_gps;

    //variable to remember if we are tracking location or not.
    boolean updateOn = false;

    //Current location
    Location currentLocation;

    //List of saved locations
    List<Location> savedLocations;


    //Location request is a config file for all settings related to FusedLocationProviderClient

    LocationRequest locationRequest;


    LocationCallback locationCallBack;

    //Google's API for location services. The majority of the app functions using this class.

    FusedLocationProviderClient fusedLocationProviderClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Give each UI variable a value

        tv_lat = findViewById(R.id.tv_lat);
        tv_lon = findViewById(R.id.tv_lon);
        tv_altitude = findViewById(R.id.tv_altitude);
        tv_accuracy = findViewById(R.id.tv_accuracy);
        tv_speed = findViewById(R.id.tv_speed);
        tv_sensor = findViewById(R.id.tv_sensor);
        tv_updates = findViewById(R.id.tv_updates);
        tv_address = findViewById(R.id.tv_address);
        tv_wayPointCounts = findViewById(R.id.tv_countOfCrumbs);

        btn_newWaypoint = findViewById(R.id.btn_newWayPoint);
        btn_showWayPointList = findViewById(R.id.btn_showWayPointList);

        sw_gps = findViewById(R.id.sw_gps);
        sw_locationupdates = findViewById(R.id.sw_locationsupdates);

        //Set all properties of LocationRequest

        locationRequest = new LocationRequest();

        //How often does the default location check occur?
        locationRequest.setInterval(1000 * DEFAULT_UPDATE_INTERVAL);

        //How often does the location check occur when set to the most frequent update?
        locationRequest.setFastestInterval(1000 * FAST_UPDATE_INTERVAL);

        //Sets the priority of the app to maintaining an optimal balance between location accuracy and device power.
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        //Event that is triggered whenever the update interval is met
        locationCallBack = new LocationCallback() {

            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);

                //Save the location
                updateUIValues(locationResult.getLastLocation());
            }
        };

        btn_newWaypoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get the GPS location

                //Add the new location to the global list
                MyApplication myApplication = (MyApplication) getApplicationContext();
                savedLocations = myApplication.getMyLocations();
                savedLocations.add(currentLocation);
            }
        });

        //Makes this button transition to that page
        btn_showWayPointList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, ShowSavedLocationsList.class);
                startActivity(i);
            }
        });


        sw_gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sw_gps.isChecked()) {
                    //Changes it to the most accurate option -- GPS
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    tv_sensor.setText("Using GPS sensors");
                } else {
                    //Changes it to the less accurate (but more battery-efficient) option -- Cell Towers
                    locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                    tv_sensor.setText("Using Towers + WIFI");
                }
            }
        });

        sw_locationupdates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sw_locationupdates.isChecked()) {
                    //Turn on location tracking
                    startLocationUpdates();
                } else {
                    //Turn off location tracking
                    stopLocationUpdates();
                }
            }
        });


        updateGPS();

    } //End onCreate method

    private void stopLocationUpdates() {

        tv_updates.setText("Location is NOT being tracked");
        tv_lat.setText("Not tracking location");
        tv_lon.setText("Not tracking location");
        tv_speed.setText("Not tracking location");
        tv_address.setText("Not tracking location");
        tv_accuracy.setText("Not tracking location");
        tv_altitude.setText("Not tracking location");
        tv_sensor.setText("Not tracking location");

        fusedLocationProviderClient.removeLocationUpdates(locationCallBack);
    }

    //Had to suppress unnecessary warning
    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        tv_updates.setText("Location is being tracked");
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallBack, null);
        updateGPS();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch(requestCode) {
            case PERMISSIONS_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    updateGPS();
                } else {
                    Toast.makeText(this, "This app requires permission to be granted in order to work properly", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void updateGPS() {
        //Get permissions from the user to track GPS
        //Get the current location from the fused client
        //Update the UI -- i.e. set all properties in their associated text view items.

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //User provided the permission
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {

                @Override
                public void onSuccess(Location location) {
                    //Permissions enabled. Put the values of location. Add various tidbits of information into the UI components.
                    updateUIValues(location);
                    currentLocation = location;
                }
            });

        } else {
            //Permissions not granted yet.

            //Checks to see if user OS build version is compatible
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);
            }
        }
    }

    private void updateUIValues(Location location) {
        //Update all of the TextView objects with a new location
        tv_lat.setText(String.valueOf(location.getLatitude()));
        tv_lon.setText(String.valueOf(location.getLongitude()));
        tv_accuracy.setText(String.valueOf(location.getAccuracy()));

        if (location.hasAltitude()) {
            tv_altitude.setText(String.valueOf(location.getAltitude()));
        } else {
            tv_altitude.setText("Not available");
        }

        if (location.hasSpeed()) {
            tv_speed.setText(String.valueOf(location.getAltitude()));
        } else {
            tv_speed.setText("Not available");
        }

        Geocoder geocoder = new Geocoder(MainActivity.this);

        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            tv_address.setText(addresses.get(0).getAddressLine(0));
        } catch(Exception e) {
            tv_address.setText("Unable to get street address");
        }

        MyApplication myApplication = (MyApplication) getApplicationContext();
        savedLocations = myApplication.getMyLocations();

        //Show the number of WayPoints saved.
        tv_wayPointCounts.setText(Integer.toString(savedLocations.size()));

    }
}