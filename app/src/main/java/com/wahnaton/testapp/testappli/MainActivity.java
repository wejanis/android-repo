package com.wahnaton.testapp.testappli;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;

import java.util.Locale;
import java.util.TimeZone;

import hirondelle.date4j.DateTime;

public class MainActivity extends AppCompatActivity{

    private SecurePreferences loginPrefs;
    private SharedPreferences datePref;
    private DateTime currentDate;
    private ViewPager mPager;
    private DatePickerDialog.OnDateSetListener date;
    private static int NUM_PAGES = 5000;
    private static int INITIAL_POSITION = NUM_PAGES / 2;
    private PagerTitleStrip pts;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Used when logging out from a page besides the main page. The logout of another page
        //will clear the activity stack until reaching main and set the "finish" extra to true.
        //Then the main activity will send the user to the login screen and finish.
        boolean finish = getIntent().getBooleanExtra("finish", false);
        if (finish) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        loginPrefs = new SecurePreferences(this, "user-info", "randomTestingPurposesKey", true);
        datePref = getSharedPreferences("date-pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = datePref.edit();

       // if(datePref.getString("currDate", null).equals(null))
       // {
            currentDate = DateTime.now(TimeZone.getDefault());
            editor.putString("currDate", currentDate.format("YYYY-MM-DD", Locale.getDefault()).toString());
            editor.commit();
            System.out.println("current day: " + currentDate.format("YYYY-MM-DD", Locale.getDefault()).toString());
      //  }
     //   else{

       // }

        mPager = (ViewPager) findViewById(R.id.mPager);
        pts = (PagerTitleStrip) findViewById(R.id.tsPager);
        mPager.setAdapter(new ScreenSlidePagerAdapter(getResources(), getSupportFragmentManager()));
        mPager.setCurrentItem(INITIAL_POSITION, false);
        mPager.getAdapter().notifyDataSetChanged();

        //Keep some offscreen pages even when offscreen so they don't need to reload.
        mPager.setOffscreenPageLimit(13);

        //Pagertitle strip by default shows the title of the previous fragment, the current fragment, and the next fragment
        //This setting causes only the primary fragment's title to be shown (aka shows only "Today" instead of "Yesterday     Today    Tomorrow"
        pts.setNonPrimaryAlpha(0);

        String username = loginPrefs.getString("username");
        setTitle("Welcome, " + username + "!");

        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                currentDate = syncDate(position, currentDate);
                storeCurrentDate(currentDate);
            }

            @Override
            public void onPageSelected(int position) {
                currentDate = syncDate(position, currentDate);
                storeCurrentDate(currentDate);
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        date = new DatePickerDialog.OnDateSetListener() {
          public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
          {
              DateTime oldDate = currentDate;
              DateTime datePicked = DateTime.forDateOnly(year, monthOfYear + 1, dayOfMonth);
              int numDaysFromNewDate = oldDate.numDaysFrom(datePicked);
              currentDate = datePicked;

              storeCurrentDate(currentDate);
              mPager.setCurrentItem(mPager.getCurrentItem() + numDaysFromNewDate);
              mPager.setSelected(true);

          }
        };

    }

    public static DateTime syncDate(int position, DateTime dateToSync){

        //Calculate the positional difference between the newly selected selected position and the default position (which is Today).
        int positionDifference = position - (INITIAL_POSITION);

        DateTime pagerdate = DateTime.now(TimeZone.getDefault());
        dateToSync = pagerdate.plusDays(positionDifference);

        return dateToSync;
    }

    public void storeCurrentDate(DateTime date){

        //format to match the 'DATE' field in the SQL database (which stores dates as YYYY-MM-DD)
        String currDate = date.format("YYYY-MM-DD", Locale.getDefault()).toString();

        //Store the
        SharedPreferences.Editor editor = datePref.edit();
        editor.putString("currDate", currDate);
        editor.commit();
    }

    public void clearAllPreferences(){
        String rememberLogin = loginPrefs.getString("rememberLogin");

        if(rememberLogin.equals("false")) {
            loginPrefs.clear();
        }

        SharedPreferences.Editor editor = datePref.edit();
        editor.clear();
        editor.commit();
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
                clearAllPreferences();
                finish();
                //Activities aside from MainActivity need:
                //Intent intent = new Intent(this, HomeActivity.class);
                //intent.putExtra("finish", true);
                //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // To clean up all activities
                //startActivity(intent);
                //finish();
                return true;
            case R.id.action_addexercise:
                startActivity(new Intent(MainActivity.this, AddExerciseActivity.class));
                return true;
            case R.id.action_pickdate:
                new DatePickerDialog(this, date, currentDate.getYear(), currentDate.getMonth()-1, currentDate.getDay()).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }

    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        DateTime titleDate;

        public ScreenSlidePagerAdapter(Resources resources, FragmentManager fm) {
            super(fm);
        }

        public int getItemPosition(Object object){
            return FragmentStatePagerAdapter.POSITION_NONE;
        }

        public Fragment getItem(int position){

            return new ScreenSlidePageFragment().newInstance();
        }

        @Override
        public CharSequence getPageTitle(int position) {

            CharSequence cs;

            titleDate =  DateTime.now(TimeZone.getDefault());
            titleDate = syncDate(position, titleDate);

            if(position - (INITIAL_POSITION) == 0)
                cs = "Today";
            else if (position - (INITIAL_POSITION) == 1)
                cs = "Tomorrow";
            else if (position - (INITIAL_POSITION) == -1)
                cs = "Yesterday";
            else
                cs = titleDate.format("WWW, MM/DD/YYYY", Locale.getDefault()).toString();

            return cs;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

    public void onDestroy() {
        super.onDestroy();
        clearAllPreferences();
    }


}
