package com.wahnaton.testapp.testappli;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;


public class RegisterActivity extends Activity implements View.OnClickListener {

    private JSONParser jsonParser = new JSONParser();

    private ProgressDialog registerDialog;
    private Button bRegister;
    private EditText etName, etUsername, etPassword, etVerifyPassword;
    private TextView tvPasswordError;

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

            // The register button grabs the user input and
            // sends it to the background task AddUser to make a
            // server request.
            case R.id.bRegister:
                String name = etName.getText().toString();
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                String verifypassword = etVerifyPassword.getText().toString();
                new AddUser(name, username, password, verifypassword).execute();
                break;
        }
    }

    /*
        Add User is a task that adds a user and his/her information to the database.
        It uses a background task to send a post request to the server where
        the server checks if the user already exists. If the user doesn't exist,
        the information is stored, otherwise an error message is displayed.
     */
    class AddUser extends AsyncTask<String, String, String> {

        private boolean passwordError, usernameError, userNameSizeError, passwordSizeError;
        private String name, username, password, verifypassword;

        public AddUser(String name, String username, String password, String verifypassword) {

            this.name = name;
            this.username = username;
            this.password = password;
            this.verifypassword = verifypassword;
        }


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
            String addUserUrl = "http://192.168.1.12:80/android_connect/addUser.php";
            passwordError= false;
            usernameError = false;
            userNameSizeError = false;
            passwordSizeError = false;

            // Input validation. Name/username must be at least a character.
            // Passwords must be 6 characters. Password and verify password
            // must be the exact same.
            if(name.length() < 1 || username.length() < 1)
                userNameSizeError = true;
            else if(password.length() < 6)
                passwordSizeError = true;
            else if(!password.equals(verifypassword))
                passwordError = true;
            else {

                // Store registration information as key/value pairs.
                LinkedHashMap<String, String> params = new LinkedHashMap<>();
                params.put("name", name);
                params.put("username", username);
                params.put("password", password);

                //Send a post request to the server using the paramters list from above.
                JSONObject json = jsonParser.makePostRequest(addUserUrl, params);
                //Log.d("Add user response: ", json.toString());

                try {
                    int success = json.getInt("success");

                    //Successful user registration will send the user back to the login activity.
                    if (success == 1) {
                        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(i);
                        finish();
                    }

                    //Unsuccessful registration will display an error message.
                    else {
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

            //Display the type of error mesage to the user.
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
