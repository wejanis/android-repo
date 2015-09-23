package com.wahnaton.testapp.testappli;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;


public class LoginActivity extends Activity implements View.OnClickListener {

    private ProgressDialog loginDialog;
    private static String userLoginUrl = "http://192.168.1.9:80/android_connect/userLogin.php";
    private String rememberLogin;

    private JSONParser jsonParser = new JSONParser();
    private SecurePreferences loginPrefs;

    private Button bLogin;
    private EditText etUsername;
    private EditText etPassword;
    private TextView tvRegisterLink;
    private TextView tvInvalidLogin;
    private CheckBox cbRememberLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);
        bLogin = (Button) findViewById(R.id.bLogin);
        tvRegisterLink = (TextView) findViewById(R.id.tvResgisterLink);
        tvInvalidLogin = (TextView) findViewById(R.id.tvInvalidLogin);
        cbRememberLogin = (CheckBox) findViewById(R.id.cbRememberLogin);
        loginPrefs = new SecurePreferences(this, "user-info", "randomTestingPurposesKey", true);

        rememberLogin = loginPrefs.getString("rememberLogin");

        //Kind of weird due to how Secured preferences works.
        //Shared preferences has getBoolean which has a parameter for returning a value should the value return null.
        //May need to modify SecuredPreferences to add a similar behavior.
        if(rememberLogin == null)
        {
            loginPrefs.put("rememberLogin", "false");
            etUsername.setText("");
            etPassword.setText("");
            cbRememberLogin.setChecked(false);
        }
        else if(rememberLogin.equals("true")){
            etUsername.setText(loginPrefs.getString("username"));
            etPassword.setText(loginPrefs.getString("password"));
            cbRememberLogin.setChecked(true);
        }


        bLogin.setOnClickListener(this);
        tvRegisterLink.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case R.id.bLogin:
                new ValidateUser().execute();
                break;

            case R.id.tvResgisterLink:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
        }

    }

    class ValidateUser extends AsyncTask<String, String, String> {

        boolean passwordError, usernameError, userNameSizeError, passwordSizeError;

        protected void onPreExecute(){
            super.onPreExecute();
            loginDialog = new ProgressDialog(LoginActivity.this);
            loginDialog.setMessage("Loading account..");
            loginDialog.setIndeterminate(false);
            loginDialog.setCancelable(true);
            loginDialog.show();

            tvInvalidLogin.setText("");
        }

        protected String doInBackground(String... args) {
            passwordError= false;
            usernameError = false;
            userNameSizeError = false;
            passwordSizeError = false;
            String username = etUsername.getText().toString();
            String password = etPassword.getText().toString();

            if(username.length() < 1)
                userNameSizeError = true;
            else if(password.length() < 6)
                passwordSizeError = true;
            else {
                LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
                params.put("username", username);
                params.put("password", password);

                JSONObject json = jsonParser.makePostRequest(userLoginUrl, params);
                //Log.d("JSON Parser", json.toString());

                // check for success tag
                try {
                    int success = json.getInt("success");
                    if (success == 1) {

                        if(cbRememberLogin.isChecked()) {
                            loginPrefs.put("rememberLogin", "true");
                            loginPrefs.put("username", username);
                            loginPrefs.put("password", password);
                        }
                        else{
                            loginPrefs.clear();
                            loginPrefs.put("rememberLogin", "false");
                        }

                        // successfully created user
                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(i);
                        // closing this screen
                        finish();
                    } else {
                        String usernameMessage = json.getString("username_error");
                        String passwordMessage = json.getString("password_error");
                        if (usernameMessage.equals("Username does not exist"))
                            usernameError = true;
                        if (passwordMessage.equals("Incorrect password"))
                            passwordError = true;

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        protected void onPostExecute(String file_url){

            if (userNameSizeError)
            {
                tvInvalidLogin.setText("Please enter a username!");
                tvInvalidLogin.setTextColor(Color.RED);
            }
            else if(passwordSizeError)
            {
                tvInvalidLogin.setText("Passwords must be at least 6 characters.");
                tvInvalidLogin.setTextColor(Color.RED);
            }
            else if(usernameError || passwordError)
            {
                tvInvalidLogin.setText("The username and password you entered did not match our records. Please try again.");
                tvInvalidLogin.setTextColor(Color.RED);
            }
            loginDialog.dismiss();
        }
    }



}
