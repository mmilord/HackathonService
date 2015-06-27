package com.example.interns.hackathonservice;

import android.nfc.Tag;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by interns on 6/23/15.
 */
public class WeatherManager {

    public JSONObject weatherData;

    public static JSONObject GetWeatherData (LatLng geoData) {
        JSONObject jsonObject = null;
        try{
            jsonObject = new JSONObject(getJSON(parseURL(geoData)));
            //Log.i(MainActivity.class.getName(), jsonObject.getString("sunrise"));
        } catch(Exception e){e.printStackTrace();}

        return jsonObject;
    }

    public static String parseWeatherJSON (JSONObject weatherData, String key) {
        String data = "";

        try {
            data = weatherData.get(key).toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
      //  Log.d("asdf", "parse weather json");
        return data;
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

        parsedURL = "http://api.weather.com/v1/geocode/34.063/-84.217/observations/current.json?apiKey=34aae6773a01ce1756979f510dff96b9&language=en-US&units=e";
        //parsedURL = "http://api.weather.com/v1/geocode/" + geoData.latitude + "/" +
            //    geoData.longitude + "/observations/current.json?apiKey=" +
              //  "34aae6773a01ce1756979f510dff96b9" + "&language=en-US&units=e";
        // http://api.weather.com/v1/geocode/34.063/-84.217/observations/current.json?apiKey={apikey}&language=en-US&units=m
        // http://api.weather.com/v1/geocode/34.063/-84.217/observations/current.xml?apiKey={apikey}&language=en-US&units=e

        return parsedURL;
    }


}
