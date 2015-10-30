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

/*
    The JSON Parser class is used to make HTTP POST and GET requests to the server
    and parses the json data from the POST and GET response.
 */

public class JSONParser{

   public JSONParser() {}

    public JSONObject makePostRequest(String url, LinkedHashMap<String, String> postDataParams) {

        HttpURLConnection urlConnection = null;
        String response ="";
        JSONObject jObj = null;

        try {
            URL _url = new URL(url);

            //Setup the url connection
            urlConnection = (HttpURLConnection) _url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setReadTimeout(5000);
            urlConnection.setConnectTimeout(5000);
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            urlConnection.setRequestMethod("POST");
            urlConnection.setUseCaches(false);

            //Format the given params into a post string and let the url connection know its length
            String postDataString = formatRequestString(postDataParams);
            urlConnection.setFixedLengthStreamingMode(postDataString.getBytes().length);

            //Write the post string to a buffer to send to the server
            OutputStream os = new BufferedOutputStream(urlConnection.getOutputStream());
            BufferedWriter bw = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            bw.write(postDataString);
            bw.flush();
            bw.close();
            os.close();

            //Print out the response code from the server
            int responseCode= urlConnection.getResponseCode();
            System.out.println("response code: " + responseCode);

            //If there is a response, read the data and store into a string
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                while ((line= br.readLine()) != null) {
                    response+=line;
                }
            }

            //If the response isn't ok, log it
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

        // return JSON Object
        return jObj;

    }

    // function get json from url
    // by making HTTP GET  method
    public JSONArray makeGetRequest(String url, LinkedHashMap<String, String> getDataParams) { //data params for the GET request

        JSONArray jArray = null;
        String response = "";
        HttpURLConnection urlConnection = null;

        try {

            //Format the get request string
            url = url + "?" + formatRequestString(getDataParams);

            //Setup the url connection
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
            response = sb.toString();

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
            jArray = new JSONArray(response);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }

        //Uses a json array instead of an object because the get request
        // could return multiple objects
        return jArray;
    }

    //Given a list of key/value pairs, this method formats and encodes them into string for the GET/POST request
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

}