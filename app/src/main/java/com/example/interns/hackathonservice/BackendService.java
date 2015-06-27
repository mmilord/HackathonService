package com.example.interns.hackathonservice;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BackendService extends Service {
    LocationManager locationManager;
    private static final String TAG = "BackendService";

    private boolean isRunning  = false;

    ScheduledExecutorService scheduler;
    APIProcessor apiProcessor;

    @Override
    public void onCreate() {
        Log.i(TAG, "Service onCreate");

        isRunning = true;
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {

        Log.i(TAG, "Service onStartCommand");
        locationManager  = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the location provider
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);	//default
        criteria.setCostAllowed(false);
        // get the best provider depending on the criteria
        String provider = locationManager.getBestProvider(criteria, false);

        // the last known location of this provider
        Location location = locationManager.getLastKnownLocation(provider);
        LocationListener locationListener = new MyLocationListener();
        locationManager.requestLocationUpdates(provider, 200, 1, locationListener);

        apiProcessor = new APIProcessor(getApplicationContext(), intent, locationManager);
        apiProcessor.initAPIChain(getApplicationContext());

        //Creating new thread for my service
        //Always write your long running tasks in a separate thread, to avoid ANR
        new Thread(new Runnable() {
            @Override
            public void run() {

                scheduler = Executors.newSingleThreadScheduledExecutor();

                scheduler.scheduleAtFixedRate (new Runnable() {
                    public void run() {
                        //startService(intent);
                        Log.d(TAG, "backround");

                        apiProcessor.updateLocation();

                    }
                }, 0, 2, TimeUnit.MINUTES);

                System.out.println("hi" + SystemClock.currentThreadTimeMillis());

                //Stop service once it finishes its task
               // stopSelf();
            }
        }).start();

        return Service.START_STICKY;
    }

    public void NotificationSender(){
        System.out.println("asdf" + SystemClock.currentThreadTimeMillis());
        NotificationBuilder n = new NotificationBuilder(this, new Intent(BackendService.this, NotificationBuilder.class));
        n.NewNotification("Weather Alert Nearby!", "14.1 Earthquake reported in your area!");
    }


    @Override
    public IBinder onBind(Intent arg0) {
        Log.i(TAG, "Service onBind");
        return null;
    }

    @Override
    public void onDestroy() {

        isRunning = false;

        scheduler.shutdown();

        Log.i(TAG, "Service onDestroy");
    }

    /*---------- Listener class to get coordinates ------------- */
    public class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {
            Toast.makeText(
                    getApplicationContext(),
                    "Location changed: Lat: " + loc.getLatitude() + " Lng: "
                            + loc.getLongitude(), Toast.LENGTH_SHORT).show();
            String longitude = "Longitude: " + loc.getLongitude();
            Log.v(TAG, longitude);
            String latitude = "Latitude: " + loc.getLatitude();
            Log.v(TAG, latitude);

            //WeatherManager.GetWeatherData(new LatLng(loc.getLatitude(), loc.getLongitude()));

        /*------- To get city name from coordinates -------- */
            String cityName = null;
            Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
            List<Address> addresses;
            try {
                addresses = gcd.getFromLocation(loc.getLatitude(),
                        loc.getLongitude(), 1);
                if (addresses.size() > 0)
                    System.out.println(addresses.get(0).getLocality());
                cityName = addresses.get(0).getLocality();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            String s = longitude + "\n" + latitude + "\n\nMy Current City is: "
                    + cityName;

        }

        @Override
        public void onProviderDisabled(String provider) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    }
}
