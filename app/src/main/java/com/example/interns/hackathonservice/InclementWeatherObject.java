package com.example.interns.hackathonservice;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by admin on 6/24/15.
 */
public class InclementWeatherObject {
    private float hourRainFall;
    private float multiHourRainFall;
    private String id;
    private LatLng latLong;
    private String inclementWeatherType;
    private int speedLimit;

    public InclementWeatherObject(float hourRainFall, float multiHourRainFall, String id, String inclementWeatherType, LatLng latLong){
        this.hourRainFall = hourRainFall;
        this.multiHourRainFall = multiHourRainFall;
        this.id = id;
        this.latLong = latLong;
        this.inclementWeatherType = inclementWeatherType;
    }


    public int getSpeedLimit(){return speedLimit;}
    public void setSpeedLimit(int speedLimit){this.speedLimit = speedLimit;}
    public float getHourRainFall() { return hourRainFall; }
    public float getMultiHourRainFall() { return multiHourRainFall; }
    public String getID(){return id;}
    public String getInclementWeatherType(){return inclementWeatherType;}
    public LatLng getLatLong(){return latLong; }
}
