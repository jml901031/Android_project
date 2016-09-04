package com.example.derek.finalproject;

/**
 * Created by bubbl on 4/25/2016.
 */

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class MapsApiUtils {
    private static MapsApiUtils mapsApiUtils = new MapsApiUtils();
    private static final String key = "AIzaSyAvnE5IeKYPVhabfGSSLUtPNdC3KT3A2Nc";

    synchronized public static MapsApiUtils getInstance() {
        return mapsApiUtils;
    }

    // Send json data via HTTP POST Request
    public static String sendHttPostRequest(String urlString){
        HttpURLConnection httpConnection = null;
        String jsonString=null;
        try {
            URL url = new URL(urlString);
            httpConnection = (HttpURLConnection) url.openConnection();

            httpConnection.setDoOutput(true);
            httpConnection.setChunkedStreamingMode(0);

            if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream stream = httpConnection.getInputStream();
                jsonString = getStringfromStream(stream);

                Log.d("MyDebugMsg:PostRequest", jsonString);
            } else Log.d("MyDebugMsg:PostRequest", "POST request returns error");
        } catch (Exception ex) {
            Log.d("MyDebugMsg", "Exception in sendHttpPostRequest");
            ex.printStackTrace();
        }

        if (httpConnection != null) httpConnection.disconnect();
        return jsonString;
    }

    // Get a string from an input stream
    private static String getStringfromStream(InputStream stream){
        String line, jsonString=null;
        if (stream != null) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            StringBuilder out = new StringBuilder();
            try {
                while ((line = reader.readLine()) != null) {
                    out.append(line);
                }
                reader.close();
                jsonString = out.toString();
            } catch (IOException ex) {
                Log.d("MyDebugMsg", "IOException in downloadJSON()");
                ex.printStackTrace();
            }
        }
        return jsonString;
    }

    public Object getLatlng(String str) {
        String url = "https://maps.google.com/maps/api/geocode/json?address="+
                str+"&key="+key;

        LatLng tmp = null;

        String jsonString = sendHttPostRequest(url);

        try{
            JSONObject jso = new JSONObject(jsonString);

            JSONArray jsa = (JSONArray)jso.get("results");

            JSONObject jso1 = (JSONObject)jsa.get(0);
            JSONObject jso2 = (JSONObject)jso1.get("geometry");
            JSONObject jso3 = (JSONObject)jso2.get("location");
            tmp = new LatLng((double)jso3.get("lat"), (double)jso3.get("lng"));
        }
        catch (Exception ex){
            Log.d("Exception From here?",ex.toString());
        }
        return tmp;

    }

    public PolylineOptions getRoute(String origin,String des){
        String url = "https://maps.googleapis.com/maps/api/directions/json?origin="+
                origin+"&destination="+des+"&key="+key;
        String jsonString = sendHttPostRequest(url);
        PolylineOptions poly = new PolylineOptions();

        try{
            JSONObject jso = new JSONObject(jsonString);

            JSONArray routes = (JSONArray)jso.get("routes");
            JSONObject routes0 = (JSONObject)routes.get(0);
            JSONArray legs = (JSONArray) routes0.get("legs");
            JSONObject legs0 = (JSONObject) legs.get(0);
            JSONArray steps = (JSONArray) legs0.get("steps");
            for(int i=0;i < steps.length();i++) {
                LatLng tmp = null;
                JSONObject location = (JSONObject)steps.get(i);
                JSONObject start = (JSONObject) location.get("start_location");
                JSONObject end = (JSONObject) location.get("end_location");
                tmp = new LatLng((double)start.get("lat"),(double) start.get("lng"));
                poly.add(tmp);
                Log.d("start", String.valueOf(tmp.latitude) + " " + String.valueOf(tmp.longitude));
                tmp = new LatLng((double)end.get("lat"),(double) end.get("lng"));
                poly.add(tmp);
                Log.d("end", String.valueOf(tmp.latitude) + " " + String.valueOf(tmp.longitude));
            }
        }
        catch (Exception ex){
            Log.d("Exception From here?",ex.toString());
        }
        return poly;
    }
}