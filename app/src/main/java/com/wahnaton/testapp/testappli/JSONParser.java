package com.wahnaton.testapp.testappli;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class JSONParser{

   public JSONParser() {}

    // function get json from url
    // by making HTTP POST  method
    public JSONObject makePostRequest(String url, LinkedHashMap<String, String> postDataParams) {

        HttpURLConnection urlConnection = null;
        String response ="";
        JSONObject jObj = null;

        // make HTTP request using POST method
        try {
            URL _url = new URL(url);

            //setup connection
            urlConnection = (HttpURLConnection) _url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setReadTimeout(5000);
            urlConnection.setConnectTimeout(5000);
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            urlConnection.setRequestMethod("POST");
            urlConnection.setUseCaches(false);

            String postDataString = formatRequestString(postDataParams);
            urlConnection.setFixedLengthStreamingMode(postDataString.getBytes().length);

            OutputStream os = new BufferedOutputStream(urlConnection.getOutputStream());
            BufferedWriter bw = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            bw.write(postDataString);
            bw.flush();
            bw.close();
            os.close();

            int responseCode= urlConnection.getResponseCode();
            System.out.println("response code: " + responseCode);


            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                while ((line= br.readLine()) != null) {
                    response+=line;
                }
            }

            if (responseCode != HttpURLConnection.HTTP_OK) {
                Log.e("No response", urlConnection.getInputStream().toString());
            }

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            urlConnection.disconnect();
        }

        // try parse the string to a JSON object
        try {
            System.out.println("Response: " + response.toString());
            jObj = new JSONObject(response);

        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }

        // return JSON String
        return jObj;

    }

    //Formats and encodes the string for the GET/POST request
    private static String formatRequestString(LinkedHashMap<String, String> params){

        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet())
        {
            if(first)
                first = false;
            else
                result.append("&");

            try {
                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                throw new AssertionError("UTF-8 is unknown");
            }
            result.append("=");
            try {
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                throw new AssertionError("UTF-8 is unknown");
            }
        }
        return result.toString();
    }

    // function get json from url
    // by making HTTP GET  method
    public JSONArray makeGetRequest(String url, LinkedHashMap<String, String> getDataParams) { //data params for the GET request

        JSONArray jArray = null;
        String json = "";
        HttpURLConnection urlConnection = null;

        //make HTTP request using GET method
        try {
            url = url + "?" + formatRequestString(getDataParams);

            URL _url = new URL(url);
            urlConnection = (HttpURLConnection) _url.openConnection();
            urlConnection.setDoInput(true);
            urlConnection.setReadTimeout(5000);
            urlConnection.setConnectTimeout(5000);
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            urlConnection.setRequestMethod("GET");
            urlConnection.setUseCaches(true);

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            InputStreamReader isw = new InputStreamReader(in);

            int data = isw.read();
            StringBuilder sb = new StringBuilder();
            while (data != -1) {
                char current = (char) data;
                data = isw.read();
                sb.append(current);
                System.out.print(current);
            }
            System.out.println();
            json = sb.toString();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            urlConnection.disconnect();
    }

        // try parse the string to a JSON object
        try {
            jArray = new JSONArray(json);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }

        // return JSON String
        return jArray;
    }

}