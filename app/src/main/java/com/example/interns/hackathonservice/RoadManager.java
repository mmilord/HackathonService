package com.example.interns.hackathonservice;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by admin on 6/24/15.
 */
public class RoadManager {

    public static int getSpeedLimit(InclementWeatherObject inclementWeatherObjects){
        JSONObject jsonObject = null;
        int i = -1;
        try{
            jsonObject = new JSONObject(getJSON(parseURL(inclementWeatherObjects.getLatLong())));
            if(jsonObject.has("speedLimits")){
                JSONArray json = new JSONArray(WeatherManager.parseWeatherJSON(jsonObject, "speedLimits"));
                jsonObject = new JSONObject(json.get(0).toString());
                i = jsonObject.getInt("speedLimit");
            }
        } catch(Exception e){e.printStackTrace();}

        return i;
    }

    public static String getJSON(String address){
        StringBuilder builder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(address);
        try{
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if(statusCode == 200){
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while((line = reader.readLine()) != null){
                    builder.append(line);
                }
            } else {
                Log.e(MainActivity.class.toString(), "Failedet JSON object");
            }
        }catch(ClientProtocolException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
        return builder.toString();
    }

    public static String parseURL (LatLng geoData) {
        String parsedURL = "";

        parsedURL = "https://roads.googleapis.com/v1/speedLimits?path=";
        parsedURL += geoData.latitude + "," + geoData.longitude;
        parsedURL += "&units=MPH&key=AIzaSyAYffyi7046YSVHnaFhNg1SFiLuDOR35vs";
        return parsedURL;
    }

    private static String removeLastChar(String str) {
        return str.substring(0,str.length()-1);
    }
}
