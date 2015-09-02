package com.wahnaton.testapp.testappli;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class RegisterActivity extends Activity implements View.OnClickListener {

    private ProgressDialog pDialog;
    private static String addUserUrl = "http://localhost/android_connection/addUser.php";
    JSONParser jsonParser = new JSONParser();

    Button bRegister;
    EditText etName, etUsername, etPassword, etVerifyPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        bRegister = (Button) findViewById(R.id.bRegister);
        etName = (EditText) findViewById(R.id.etName);
        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etVerifyPassword = (EditText) findViewById(R.id.etVerifyPassword);
        bRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bRegister:
                new AddUser().execute();
                break;
        }
    }

    class AddUser extends AsyncTask<String, String, String> {

        protected void onPreExecute(){
            super.onPreExecute();
            pDialog = new ProgressDialog(RegisterActivity.this);
            pDialog.setMessage("Creating your account...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... args) {
            String name = etName.getText().toString();
            String username = etUsername.getText().toString();
            String password = etPassword.getText().toString();
            String verifypassword = etVerifyPassword.getText().toString();

            HashMap<String, String> params = new HashMap<String, String>();
            params.put("name", name);
            params.put("username", username);
            params.put("password", password);
            params.put("verifypassword", verifypassword);

            JSONObject json = jsonParser.makePostRequest(addUserUrl, params);
            Log.d("Add user response: ", json.toString());

            // check for success tag
            try {
                int success = json.getInt("success");
                if (success == 1) {
                    // successfully created product
                    Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(i);
                    pDialog.setMessage("Successfully created your account!");
                    // closing this screen
                    finish();
                } else {
                    // failed to create product
                    pDialog.setMessage("Failed to create account");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }


        protected void onPostExecute(String file_url){
            pDialog.dismiss();
        }


    }

}
