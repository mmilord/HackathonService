package com.example.interns.hackathonservice;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 6/23/15.
 */
public class GeofenceManager implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status> {

    private HashMap<String, Geofence> geoFences;
    private GoogleApiClient googleApiClient;
    private PendingIntent geofencePendingIntent;
    private Intent intent;
    private Context context;
    private List<String> listOfIdsToRemove;

    public GeofenceManager(Context context) {
        geoFences = new HashMap<String, Geofence>();
        this.context = context;
        listOfIdsToRemove = new ArrayList<>();
        setUpGoogleAPI();
    }

    public void addGeofenceList(ArrayList<InclementWeatherObject> inclementWeatherObjects) {
        listOfIdsToRemove = getListOfRemovedIDs(inclementWeatherObjects);

        int count = 0;
        for (InclementWeatherObject inclementWeatherObject : inclementWeatherObjects) {

            if (geoFences.get(inclementWeatherObject.getID()) == null) {
                count++;
                Geofence fence = new Geofence.Builder()
                        .setRequestId(inclementWeatherObject.getID())
                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT | Geofence.GEOFENCE_TRANSITION_DWELL)
                        .setCircularRegion(
                                inclementWeatherObject.getLatLong().latitude,
                                inclementWeatherObject.getLatLong().longitude,
                                GeofenceConstants.GEOFENCERADIUS)
                        .setExpirationDuration(GeofenceConstants.GEOFENCEEXPIRATIONMILLISECONDS)
                        .setLoiteringDelay(1000)
                        .build();

                geoFences.put(inclementWeatherObject.getID(), fence);
            }
        }

        if (googleApiClient.isConnected()) {
            removeGeofenceEvents();
            if(count > 0)
                addGeofencesEvents();
        }

    }

    private void removeGeofenceEvents() {
        if (listOfIdsToRemove.size() > 0){
            for (int i = 0; i < listOfIdsToRemove.size(); i++) {
                if (geoFences.get(listOfIdsToRemove.get(i)) != null) {
                    geoFences.remove(listOfIdsToRemove.get(i));
                }
            }
            LocationServices.GeofencingApi.removeGeofences(googleApiClient, listOfIdsToRemove);
            listOfIdsToRemove.clear();
        }
    }



    public ArrayList<String> getListOfRemovedIDs(ArrayList<InclementWeatherObject> inclementWeatherObjects){
        ArrayList<String> list = new ArrayList<String>();

        if(inclementWeatherObjects.size() == 0){
            Iterator myVeryOwnIterator = geoFences.keySet().iterator();
            while (myVeryOwnIterator.hasNext()) {
                boolean hasKey = false;
                list.add((String) myVeryOwnIterator.next());
            }
        }else {

            Iterator myVeryOwnIterator = geoFences.keySet().iterator();
            while (myVeryOwnIterator.hasNext()) {
                boolean hasKey = false;
                String key = (String) myVeryOwnIterator.next();
                for (InclementWeatherObject inclementWeatherObject : inclementWeatherObjects) {
                    if (key == inclementWeatherObject.getID()) {
                        hasKey = true;
                    }
                }
                if (!hasKey)
                    list.add(key);
            }
        }

        return list;

    }

    public void addGeofence(String name, LatLng latLong){
        Geofence fence = new Geofence.Builder()
                .setRequestId(name)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT | Geofence.GEOFENCE_TRANSITION_DWELL)
                .setCircularRegion(
                        latLong.latitude,
                        latLong.longitude,
                        GeofenceConstants.GEOFENCERADIUS)
                .setExpirationDuration(GeofenceConstants.GEOFENCEEXPIRATIONMILLISECONDS)
                .setLoiteringDelay(1000)
                .build();

        geoFences.put(name, fence);

        if(googleApiClient.isConnected()){
            addGeofencesEvents();
        }
    }


    private void setUpGoogleAPI() {

        googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        googleApiClient.connect();
    }
    public void addGeofencesEvents() {
        if (!googleApiClient.isConnected()) {
            Toast.makeText(context, "Not Connected", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            if(geoFences.size() > 0) {
                LocationServices.GeofencingApi.addGeofences(
                        googleApiClient, // The GeofenceRequest object.
                        getGeofencingRequest(),
                        getGeofencePendingIntent()
                ).setResultCallback(this); // Result processed in onResult().
            }
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
        }
    }
    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofenceSetToArrayList(geoFences));
        return builder.build();
    }

    private ArrayList<Geofence> geofenceSetToArrayList(HashMap<String,Geofence> geofenceSet){
        ArrayList<Geofence> arraylist = new ArrayList<Geofence>();
        for (Map.Entry<String, Geofence> entry : geofenceSet.entrySet()) {
            arraylist.add(entry.getValue());
        }
        return arraylist;
    }
    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (geofencePendingIntent != null) {
            return geofencePendingIntent;
        }
        intent = new Intent(context, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        geofencePendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return geofencePendingIntent;
    }

    @Override
    public void onConnected(Bundle bundle) {
        System.out.println("Is Connected " + googleApiClient.isConnected());

        removeGeofenceEvents();
        addGeofencesEvents();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onResult(Status status) {

    }
}
