package com.example.exactogps;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;

public class MainActivity extends AppCompatActivity {
    //How often the application updates the location (default or fast)
    public static final int DEFAULT_UPDATE_INTERVAL = 30;
    public static final int FAST_UPDATE_INTERVAL = 5;

    //References to the UI elements

    TextView tv_lat;
    TextView tv_lon;
    TextView tv_altitude;
    TextView tv_accuracy;
    TextView tv_speed;
    TextView tv_sensor;
    TextView tv_updates;
    TextView tv_address;

    Switch sw_locationupdates;
    Switch sw_gps;

    //variable to remember if we are tracking location or not.
    boolean updateOn = false;

    //Location request is a config file for all settings related to FusedLocationProviderClient
    LocationRequest locationRequest;

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
    }
}