package com.example.interns.hackathonservice;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by admin on 6/23/15.
 */

public class FloodLocationAPI {
    //Corrisponds to Server Call

    private static ArrayList<InclementWeatherObject> locations;

    static {
        locations = new ArrayList<InclementWeatherObject>();
        InclementWeatherObject inclementWeatherObject = new InclementWeatherObject(4, 10, "Exit5i20GA", "flood", new LatLng(33.7132287, -85.2376739));
        int value = RoadManager.getSpeedLimit(inclementWeatherObject);
        if(value != -1) inclementWeatherObject.setSpeedLimit(value);
        locations.add(inclementWeatherObject);
        inclementWeatherObject = new InclementWeatherObject(4, 10, "i285&i75", "flood", new LatLng(32.6328811, -84.4008112));
        RoadManager.getSpeedLimit(inclementWeatherObject);
        locations.add(inclementWeatherObject);
        inclementWeatherObject = new InclementWeatherObject(4, 10, "BBQ", "flood", new LatLng(33.898588, -84.447183));
        RoadManager.getSpeedLimit(inclementWeatherObject);
        locations.add(inclementWeatherObject);
        inclementWeatherObject = new InclementWeatherObject(0, 10, "WeatherChannelHQ", "flood", new LatLng(33.89371, -84.460316));
        RoadManager.getSpeedLimit(inclementWeatherObject);
        locations.add(inclementWeatherObject);
    }

    public static ArrayList<InclementWeatherObject> getPossibleFloodLocations(LatLng location){

        ArrayList<InclementWeatherObject> tempList = new ArrayList<InclementWeatherObject>();

        for (InclementWeatherObject entry : locations){

            Location loc1 = new Location("");
            loc1.setLatitude(entry.getLatLong().latitude);
            loc1.setLongitude(entry.getLatLong().longitude);

            Location loc2 = new Location("");
            loc2.setLatitude(location.latitude);
            loc2.setLongitude(location.longitude);

            float distance = loc1.distanceTo(loc2);

            if(distance < 9656.06)
                tempList.add(entry);
        }

        return tempList;

    }

    public static InclementWeatherObject getObjectByKey(String key) {
        for (InclementWeatherObject entry : locations) {
            if (entry.getID().equals(key))
                return entry;
        }
        return null;
    }

}
