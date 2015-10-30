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

    private JSONParser jsonParser = new JSONParser();
    private SecurePreferences loginPrefs;

    private EditText etUsername;
    private EditText etPassword;
    private TextView tvRegisterLink;
    private TextView tvInvalidLogin;
    private CheckBox cbRememberLogin;
    private Button bLogin;
    private ProgressDialog loginDialog;

    private String rememberLogin;

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

        //SecurePreferences is based off of SharedPreferences, but encrypts the data in the
        // file. This way even if one were to root the user's phone to access the preference data
        // it would be encrypted.
        loginPrefs = new SecurePreferences(this, "user-info", "randomTestingPurposesKey", true);

        //Checks if the user selected remember login previously.
        rememberLogin = loginPrefs.getString("rememberLogin");

        //If the user doesn't want to be remembered, clear all input fields.
        if(rememberLogin == null)
        {
            loginPrefs.put("rememberLogin", "false");
            etUsername.setText("");
            etPassword.setText("");
            cbRememberLogin.setChecked(false);
        }

        //If the user wants to be remembered, grab user data from the preference file
        // stored on the device.
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

            //If the user clicks login, grab input data and execute the validate user
            // background task
            case R.id.bLogin:
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                boolean isChecked = cbRememberLogin.isChecked();

                new ValidateUser(username, password, isChecked).execute();
                break;

            //Clicking the register link sends the user to the Register activity.
            case R.id.tvResgisterLink:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
        }

    }

    /*
        Validate User is a task that checks if the user has entered valid login data.
        It works by using a background task to send a post request to the server where
        the server checks if the data is valid. Valid information sends the user to the
        Main activity, otherwise, an error message is displayed.
    */
    class ValidateUser extends AsyncTask<String, String, String> {

        private boolean passwordError, usernameError, userNameSizeError, passwordSizeError;
        private String username;
        private String password;
        private boolean isChecked;

        public ValidateUser(String username, String password, boolean isChecked){
            this.username = username;
            this.password = password;
            this.isChecked = isChecked;
        }

        protected void onPreExecute(){
            super.onPreExecute();

            loginDialog = new ProgressDialog(LoginActivity.this);
            loginDialog.setMessage("Loading account..");
            loginDialog.setIndeterminate(false);
            loginDialog.setCancelable(true);
            loginDialog.show();

            //Initial error message is blank.
            tvInvalidLogin.setText("");
        }

        protected String doInBackground(String... args) {

            String userLoginUrl = "http://192.168.1.12:80/android_connect/userLogin.php";
            passwordError= false;
            usernameError = false;
            userNameSizeError = false;
            passwordSizeError = false;

            //Input validation. Usernames must be at least 1 character, passwords
            // must be at least 6 characters.
            if(username.length() < 1)
                userNameSizeError = true;
            else if(password.length() < 6)
                passwordSizeError = true;

            else {

                //Store user login info as key/value pairs.
                LinkedHashMap<String, String> params = new LinkedHashMap<>();
                params.put("username", username);
                params.put("password", password);

                //Make a post request to the server using the above parameter list
                // as the post body.
                JSONObject json = jsonParser.makePostRequest(userLoginUrl, params);
                //System.out.println("json string: " + json.toString());

                try {
                    int success = json.getInt("success");
                    if (success == 1) {

                        //Store the user data for at least the life of the app
                        loginPrefs.put("username", username);

                        //Keep track of the fact that the user wants to be remembered.
                        //Password is only stored on the device if the user wants
                        // to be remembered.
                        if(isChecked) {
                            loginPrefs.put("rememberLogin", "true");
                            loginPrefs.put("password", password);
                        }
                        else {
                            //Don't clear preferences here because the username is used throughout the app
                            //Credentials are cleared upon logout
                            //Instead, keep track that the user wants the credentials removed upon logout
                            //and make sure to do loginPrefs.clear() in any logout method.
                            loginPrefs.put("rememberLogin", "false");
                        }

                        //Send the user to the main activity upon successful login
                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(i);
                        finish();
                    }

                    else {

                        //Flag the type of error based on the server response.
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

            //Display error message to the user.
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
