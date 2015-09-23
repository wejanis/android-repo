package com.wahnaton.testapp.testappli;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

// change to test git 2.0
public class MainActivity extends Activity implements View.OnClickListener {

    Button bLogout;
    EditText etName, etUsername;
    private SecurePreferences loginPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginPrefs = new SecurePreferences(this, "user-info", "randomTestingPurposesKey", true);

        bLogout = (Button) findViewById(R.id.bLogout);
        etName = (EditText) findViewById(R.id.etName);
        etUsername = (EditText) findViewById(R.id.etUsername);

        String username = loginPrefs.getString("username");
        etUsername.setText(username);

        bLogout.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case R.id.bLogout:
                startActivity(new Intent(this, LoginActivity.class));
                break;



        }


    }
}
