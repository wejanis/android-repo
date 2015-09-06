package com.wahnaton.testapp.testappli;

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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

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
            urlConnection.setReadTimeout(15000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            urlConnection.setRequestMethod("POST");
            urlConnection.setUseCaches(false);

            String postDataString = getPostDataString(postDataParams);
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

    //Formats the input of makePostRequest
    private static String getPostDataString(LinkedHashMap<String, String> params){


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
    public JSONObject makeGetRequest(String url) {

        JSONObject jObj = null;
        String json = "";
        HttpURLConnection urlConnection = null;

        //make HTTP request using GET method
        try {
            URL _url = new URL(url);
            urlConnection = (HttpURLConnection) _url.openConnection();
            urlConnection.setDoInput(true);
            urlConnection.setReadTimeout(15000);
            urlConnection.setConnectTimeout(15000);
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
            json = sb.toString();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }

        // try parse the string to a JSON object
        try {
            jObj = new JSONObject(json);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }

        // return JSON String
        return jObj;
    }

}