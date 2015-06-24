package com.example.interns.hackathonservice;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by interns on 6/23/15.
 */
public class APIProcessor implements LocationListener {
    Context context;
    Intent intent;
    LocationManager locationManager;
    Location currentBestLocation = null;
    static final int TWO_MINUTES = 1000 * 60 * 2;
    JSONObject weatherData;
    final static String TAG = "asdf";

    public APIProcessor(Context context, Intent intent, LocationManager locationManager) {
        this.context = context;
        this.intent = intent;
        this.locationManager = locationManager;
    }

    public void initAPIChain(Context context) {
        this.context = context;

        getLastBestLocation();
    }

    public void updateLocation(){
        getLastBestLocation();
        if(currentBestLocation != null)
            WeatherManager.GetWeatherData(new LatLng(currentBestLocation.getLatitude(), currentBestLocation.getLongitude()));
    }

    public LatLng getLatLng () {
        return new LatLng(currentBestLocation.getLatitude(), currentBestLocation.getLongitude());
    }

    private Location getLastBestLocation() {
        Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Location locationNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);


        long GPSLocationTime = 0;
        if (null != locationGPS) { GPSLocationTime = locationGPS.getTime(); }

        long NetLocationTime = 0;

        if (null != locationNet) {
            NetLocationTime = locationNet.getTime();
        }

        if ( 0 < GPSLocationTime - NetLocationTime ) {
            currentBestLocation = locationGPS;
            return locationGPS;
        }
        else {
            currentBestLocation = locationNet;
            return locationNet;
        }
    }

    public void onLocationChanged(Location location) {

        //makeUseOfNewLocation(location);
        //call weather api

        WeatherManager.GetWeatherData(new LatLng(location.getLatitude(), location.getLongitude()));

        /*void makeUseOfNewLocation(Location location) {
            if ( isBetterLocation(location, currentBestLocation) ) {
                currentBestLocation = location;
            }
        }*/

        if(currentBestLocation == null){
            currentBestLocation = location;
        }
    }

    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location,
        // because the user has likely moved.
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse.
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(context, "Gps is turned off!! ",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(context, "Gps is turned on!! ",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }

}
