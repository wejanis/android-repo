package com.wahnaton.testapp.testappli;

import android.app.ActionBar;
import android.app.ActivityOptions;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static com.wahnaton.testapp.testappli.R.menu.menu_main;

// change to test git 2.0
public class MainActivity extends AppCompatActivity /*implements View.OnClickListener*/ {

    private SecurePreferences loginPrefs;
    private TextView tvDate;
    private Calendar myCalendar;

    private DatePickerDialog.OnDateSetListener date;

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

        tvDate = (TextView) findViewById(R.id.tvDate);

        String username = loginPrefs.getString("username");
        setTitle("Welcome, " + username + "!");

        String myFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        tvDate.setText(sdf.format(Calendar.getInstance().getTime()));


        myCalendar = Calendar.getInstance();
        date = new DatePickerDialog.OnDateSetListener() {
          public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
          {
              myCalendar.set(Calendar.YEAR, year);
              myCalendar.set(Calendar.MONTH, monthOfYear);
              myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
              updateLabel();
          }
        };

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
            case R.id.action_pickdate:

                new DatePickerDialog(this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();

                return true;
            default:
                return super.onOptionsItemSelected(item);

        }

    }


    private void updateLabel() {

        String myFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        tvDate.setText(sdf.format(myCalendar.getTime()));
    }



}
