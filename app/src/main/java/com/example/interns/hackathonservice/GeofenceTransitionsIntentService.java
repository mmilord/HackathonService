package com.example.interns.hackathonservice;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;
import android.util.Log;

import com.example.interns.hackathonservice.FloodLocationAPI;
import com.example.interns.hackathonservice.GeofenceErrorMessages;
import com.example.interns.hackathonservice.InclementWeatherObject;
import com.example.interns.hackathonservice.NotificationBuilder;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Listener for geofence transition changes.
 *
 * Receives geofence transition events from Location Services in the form of an Intent containing
 * the transition type and geofence id(s) that triggered the transition. Creates a notification
 * as the output.
 */
public class GeofenceTransitionsIntentService extends IntentService {

    protected static final String TAG = "geofence-transitions-service";

    /**
     * This constructor is required, and calls the super IntentService(String)
     * constructor with the name for a worker thread.
     */
    public GeofenceTransitionsIntentService() {
        // Use the TAG to name the worker thread.
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    /**
     * Handles incoming intents.
     * @param intent sent by Location Services. This Intent is provided to Location
     *               Services (inside a PendingIntent) when addGeofences() is called.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        System.out.println("onHandleIntent");

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = GeofenceErrorMessages.getErrorString(this,
                    geofencingEvent.getErrorCode());
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            InclementWeatherObject inclementWeatherObject = FloodLocationAPI.getObjectByKey(((Geofence) triggeringGeofences.get(0)).getRequestId());
            NotificationSender(inclementWeatherObject);

            // Get the geofences that were triggered. A single event can trigger multiple geofences.

            System.out.println();

            // Send notification and log the transition details.
        } else {
            // Log the error.
        }
    }

    public void NotificationSender(InclementWeatherObject inclementWeatherObject){
        System.out.println("asdf" + SystemClock.currentThreadTimeMillis());
        NotificationBuilder n = new NotificationBuilder(this, new Intent(GeofenceTransitionsIntentService.this, NotificationBuilder.class));
        if(inclementWeatherObject.getInclementWeatherType().equals("flood")) {
            String text = "Please Reduce Speed to " + speedlimit(inclementWeatherObject.getSpeedLimit()) + "!";
            n.NewNotification("Flood Warning!", text);
        }
        else
            n.NewNotification("Weather Alert Nearby!", inclementWeatherObject.getInclementWeatherType() +" warning reported in your area!");
    }

    public int speedlimit(int speedLimit){
        if(speedLimit <= 50)
            return 45;
        else if(speedLimit <= 35)
            return speedLimit-10;
        else if(speedLimit <= 15)
            return 15;
        else
            return speedLimit;

    }

    /**
     * Gets transition details and returns them as a formatted string.
     *
     * @param context               The app context.
     * @param geofenceTransition    The ID of the geofence transition.
     * @param triggeringGeofences   The geofence(s) triggered.
     * @return                      The transition details formatted as String.
     */
    private String getGeofenceTransitionDetails(
            Context context,
            int geofenceTransition,
            List<Geofence> triggeringGeofences) {

        return null;
    }

    /**
     * Posts a notification in the notification bar when a transition is detected.
     * If the user clicks the notification, control goes to the MainActivity.
     */
    private void sendNotification(String notificationDetails) {

    }

    /**
     * Maps geofence transition types to their human-readable equivalents.
     *
     * @param transitionType    A transition type constant defined in Geofence
     * @return                  A String indicating the type of transition
     */
    private String getTransitionString(int transitionType) {

        return null;
    }
}