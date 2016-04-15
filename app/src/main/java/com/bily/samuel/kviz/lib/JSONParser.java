package com.bily.samuel.kviz.lib;

import android.util.Log;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by samuel on 19.11.2015.
 */
public class JSONParser {

    public JSONParser(){

    }

    public JSONObject makePostCall(HashMap<String, String> values) throws JSONException {
        JSONObject jsonObject = null;
        StringBuilder stringBuilder = null;
        try{
            URL url = new URL("http://139.59.136.208/spse/android/index.php");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(6000);
            urlConnection.setConnectTimeout(7000);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter( new OutputStreamWriter(os,"UTF-8"));
            writer.write(getQuery(values));
            writer.flush();
            writer.close();
            os.close();
            urlConnection.connect();
            int responseCode = urlConnection.getResponseCode();
            if(responseCode == HttpsURLConnection.HTTP_OK){
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                stringBuilder = new StringBuilder();
                while((line=br.readLine()) != null){
                    stringBuilder.append(line);
                }
                jsonObject = new JSONObject(stringBuilder.toString());
            }
        } catch (IOException | JSONException e) {
            if (stringBuilder != null) {
                Log.e("JSONParser", "" + stringBuilder.toString());
            }
            //e.printStackTrace();
            return new JSONObject("{\nsuccess: 0,\nmessage: \"Something went wrong.\"\n}");
        }
        return jsonObject;
    }

    private String getQuery(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (Map.Entry<String, String> pair : params.entrySet()){
            if (first) {
                first = false;
            }else {
                result.append("&");
            }
            result.append(URLEncoder.encode(pair.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
        }

        return result.toString();
    }


}