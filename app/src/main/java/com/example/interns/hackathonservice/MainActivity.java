package com.example.interns.hackathonservice;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class MainActivity extends Activity {
    Intent intent;
    int PLACE_PICKER_REQUEST = 1;
    EditText searchBox;
    Geocoder geocoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //GeofenceManager manager = new GeofenceManager(this);
        //HashMap<String, LatLng> locationList = new HashMap<String, LatLng>();
        //locationList.put("Weather Channel", new LatLng(33.89371, -84.460316));
        //manager.addGeofence("Weather Channel", locationList.get("Weather Channel"));
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        intent = new Intent(MainActivity.this, BackendService.class);
        //Intent intent = new Intent(MainActivity.this, BackendService.class);
        startService(intent);

        searchBox = (EditText) findViewById(R.id.searchBox);

        /*ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate (new Runnable() {
            public void run() {
                //startService(intent);
                NotificationSender();

            }
        }, 0, 20, TimeUnit.SECONDS);*/
    }

    public void NotificationSender(){
        System.out.println("asdf" + SystemClock.currentThreadTimeMillis());
        NotificationBuilder n = new NotificationBuilder(this, new Intent(MainActivity.this, NotificationBuilder.class));
        n.NewNotification("Weather Alert Nearby!", "14.1 Earthquake reported in your area!");
    }

    public void onClick(View view) {
        stopService(intent);
        try {
            PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
            Intent newIntent = intentBuilder.build(this);
            startActivityForResult(newIntent, PLACE_PICKER_REQUEST);

        } catch (GooglePlayServicesRepairableException e) {
            GooglePlayServicesUtil
                    .getErrorDialog(e.getConnectionStatusCode(), this, 0);
        } catch (GooglePlayServicesNotAvailableException e) {
            Toast.makeText(this, "Google Play Services is not available.",
                    Toast.LENGTH_LONG)
                    .show();
        }
    }

    public void search (View view) {
        String query = searchBox.getText().toString();

        Geocoder geo = new Geocoder(getBaseContext());
        List<Address> gotAddresses = null;
        try {
            gotAddresses = geo.getFromLocationName(query, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Address address = (Address) gotAddresses.get(0);

        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

        String properAddress = String.format("%s, %s",
                address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
                address.getCountryName());


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
           // showPickAction(true);
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                String toastMsg = String.format("Place: %s", place.getName());
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
            }
        }
    }
}
