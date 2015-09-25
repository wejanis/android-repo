package com.wahnaton.testapp.testappli;

import android.app.ActionBar;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import static com.wahnaton.testapp.testappli.R.menu.menu_main;

// change to test git 2.0
public class MainActivity extends AppCompatActivity /*implements View.OnClickListener*/ {

   // Button bLogout;
    EditText etName, etUsername;
    private SecurePreferences loginPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        boolean finish = getIntent().getBooleanExtra("finish", false);
        if (finish) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        loginPrefs = new SecurePreferences(this, "user-info", "randomTestingPurposesKey", true);

        etName = (EditText) findViewById(R.id.etName);
        etUsername = (EditText) findViewById(R.id.etUsername);

        String username = loginPrefs.getString("username");
        etUsername.setText(username);

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId()){

            case R.id.action_settings:
                return true;
            case R.id.action_logout:
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                //Activities aside from MainActivity need:
                //Intent intent = new Intent(this, HomeActivity.class);
                //intent.putExtra("finish", true);
                //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // To clean up all activities
                //startActivity(intent);
                //finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }

    }

}
