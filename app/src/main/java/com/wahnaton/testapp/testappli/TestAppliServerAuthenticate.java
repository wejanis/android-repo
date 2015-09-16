package com.wahnaton.testapp.testappli;

import android.util.Log;
import com.google.gson.Gson;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
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
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;


public class TestAppliServerAuthenticate implements ServerAuthenticate{

    @Override
    public String userSignUp(String name, String username, String pass, String authType) throws Exception {

        String url = "http://192.168.1.9:80/android_connect/userLogin.php";
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

            String postDataString = "name=" + name + "&" + "username=" + name + "&" + "password=" + name;
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

        String authtoken = null;
        User createdUser = new Gson().fromJson(jObj.toString(), User.class);
        authtoken = createdUser.sessionToken;

        return authtoken;
    }

    @Override //TODO: fix deprecated stuff
    public String userSignIn(String user, String pass, String authType) throws Exception {

        Log.d("testappli", "userSignIn");

        DefaultHttpClient httpClient = new DefaultHttpClient();
        String url = "http://192.168.1.9:80/android_connect/userLogin.php";


        String query = null;
        try {
            query = String.format("%s=%s&%s=%s", "username", URLEncoder.encode(user, "UTF-8"), "password", pass);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        url += "?" + query;

        HttpGet httpGet = new HttpGet(url);

        httpGet.addHeader("X-Parse-Application-Id", "XUafJTkPikD5XN5HxciweVuSe12gDgk2tzMltOhr");
        httpGet.addHeader("X-Parse-REST-API-Key", "8L9yTQ3M86O4iiucwWb4JS7HkxoSKo7ssJqGChWx");

        HttpParams params = new BasicHttpParams();
        params.setParameter("username", user);
        params.setParameter("password", pass);
        httpGet.setParams(params);
//        httpGet.getParams().setParameter("username", user).setParameter("password", pass);

        String authtoken = null;
        try {
            HttpResponse response = httpClient.execute(httpGet);

            String responseString = EntityUtils.toString(response.getEntity());
            if (response.getStatusLine().getStatusCode() != 200) {
                TestAppliError error = new Gson().fromJson(responseString, TestAppliError.class);
                throw new Exception("Error signing-in ["+error.code+"] - " + error.error);
            }

            User loggedUser = new Gson().fromJson(responseString, User.class);
            authtoken = loggedUser.sessionToken;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return authtoken;
    }


    private class TestAppliError implements Serializable {
        int code;
        String error;
    }
    private class User implements Serializable {

        private String name;
        private String username;
        private String objectId;
        public String sessionToken;
        private String userId;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getObjectId() {
            return objectId;
        }

        public void setObjectId(String objectId) {
            this.objectId = objectId;
        }

        public String getSessionToken() {
            return sessionToken;
        }

        public void setSessionToken(String sessionToken) {
            this.sessionToken = sessionToken;
        }

        public String getGravatarId() {
            return userId;
        }

        public void setGravatarId(String gravatarId) {
            this.userId = gravatarId;
        }
    }
}
