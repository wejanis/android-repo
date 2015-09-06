package com.wahnaton.testapp.testappli;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;


public class RegisterActivity extends Activity implements View.OnClickListener {

    private ProgressDialog registerDialog;
    private static String addUserUrl = "http://192.168.1.9:80/android_connect/addUser.php";
    JSONParser jsonParser = new JSONParser();


    Button bRegister;
    EditText etName, etUsername, etPassword, etVerifyPassword;
    TextView tvPasswordError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        bRegister = (Button) findViewById(R.id.bRegister);
        etName = (EditText) findViewById(R.id.etName);
        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etVerifyPassword = (EditText) findViewById(R.id.etVerifyPassword);
        tvPasswordError = (TextView) findViewById(R.id.tvInputValidationError);
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

        boolean passwordError, usernameError, userNameSizeError, passwordSizeError;

        protected void onPreExecute(){
            super.onPreExecute();
            registerDialog = new ProgressDialog(RegisterActivity.this);
            registerDialog.setMessage("Creating your account...");
            registerDialog.setIndeterminate(false);
            registerDialog.setCancelable(true);
            registerDialog.show();
            tvPasswordError.setText("");
        }

        protected String doInBackground(String... args) {
            passwordError= false;
            usernameError = false;
            userNameSizeError = false;
            passwordSizeError = false;
            String name = etName.getText().toString();
            String username = etUsername.getText().toString();
            String password = etPassword.getText().toString();
            String verifypassword = etVerifyPassword.getText().toString();

            if(name.length() < 1 || username.length() < 1)
                userNameSizeError = true;
            else if(password.length() < 6)
                passwordSizeError = true;
            else if(!password.equals(verifypassword))
                passwordError = true;
            else {
                LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
                params.put("name", name);
                params.put("username", username);
                params.put("password", password);
                // params.put("verifypassword", verifypassword);

                JSONObject json = jsonParser.makePostRequest(addUserUrl, params);
                Log.d("Add user response: ", json.toString());

                // check for success tag
                try {
                    int success = json.getInt("success");
                    if (success == 1) {
                        // successfully created user
                        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(i);

                        // closing this screen
                        finish();
                    } else {
                        String usernameMessage = json.getString("username_error");
                        if (usernameMessage.equals("Username already exists."))
                            usernameError = true;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        protected void onPostExecute(String file_url){

            if(usernameError)
            {
                tvPasswordError.setText("Username already exists!");
                tvPasswordError.setTextColor(Color.RED);
            }
            else if(passwordError){
                tvPasswordError.setText("Passwords do not match!");
                tvPasswordError.setTextColor(Color.RED);
            }
            else if (userNameSizeError)
            {
                tvPasswordError.setText("Name or Username field is empty.");
                tvPasswordError.setTextColor(Color.RED);
            }
            else if(passwordSizeError)
            {
                tvPasswordError.setText("Passwords must be at least 6 characters.");
                tvPasswordError.setTextColor(Color.RED);
            }
            registerDialog.dismiss();
        }
    }
}
