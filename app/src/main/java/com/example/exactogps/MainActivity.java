package com.example.exactogps;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

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

        sw_gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sw_gps.isChecked()) {
                    //Changes it to the most accurate option -- GPS
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    tv_sensor.setText("Using GPS sensors");
                } else {
                    //Changes it to the less accurate (but more battery-efficent) option -- Cell Towers
                    locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                    tv_sensor.setText("Using Towers + WIFI");
                }
            }
        });
    } //End onCreate method

    private void updateGPS() {
        //Get permissions from the user to track GPS
        //Get the current location from the fused client
        //Update the UI -- i.e. set all properties in their associated text view items.

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //User provided the permission
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {

                @Override
                public void onSuccess(Location location) {
                    //User manually enabled permissions. Put the values of location. Add various tidbits of information into the UI components.

                }
            });
            
        } else {
            //Permissions not granted yet.

            //Checks to see if user OS build version is compatible
            if(Build.VERSION >= Build.VERSION_CODES.M) {
                Toast.makeText(this, "Please enable location tracking", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Sorry, you need to upgrade to " + Build.VERSION_CODES.M, Toast.LENGTH_LONG).show();
            }
        }
    }
}