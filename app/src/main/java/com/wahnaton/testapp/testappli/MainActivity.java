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
    private DateTime currentDate;
    private ViewPager mPager;
    private DatePickerDialog.OnDateSetListener date;
    private PagerTitleStrip pts;

    // Picked an arbitrary, high value to represent the number of dates that can be swiped between
    // since the pager can't be swiped infinitely.
    private static int NUM_PAGES = 5000;

    //The initial position of the pager is in the middle so the user can swipe back and forth equally.
    private static int INITIAL_POSITION = NUM_PAGES / 2;

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

        mPager = (ViewPager) findViewById(R.id.mPager);
        pts = (PagerTitleStrip) findViewById(R.id.tsPager);
        mPager.setAdapter(new ScreenSlidePagerAdapter(getResources(), getSupportFragmentManager()));
        mPager.setCurrentItem(INITIAL_POSITION, false);
        mPager.getAdapter().notifyDataSetChanged();

        //Keep some offscreen pages even when offscreen so they don't need to reload.
        mPager.setOffscreenPageLimit(13);

        //Pagertitle strip by default shows the title of the previous fragment, the current fragment, and the next fragment
        //This setting causes only the primary fragment's title to be shown (shows only "    Today    " instead of "Yesterday     Today    Tomorrow"
        pts.setNonPrimaryAlpha(0);

        loginPrefs = new SecurePreferences(this, "user-info", "randomTestingPurposesKey", true);

        // Current date is a class variable used to keep the date in sync between multiple
        // user interfaces on the activity.
        currentDate = DateTime.now(TimeZone.getDefault());

        //Handles returning back to the correct date from the ExerciseInfo Activity
        if(getIntent().hasExtra("currDate")){

            /*
             By default the current day is set to today. But it is possible that the user can
             switch the current date by either swiping the screen or using the date picker.
             When the user picks a date they would like to add an exercise to, the date is sent to
             the Exercise Info activity. When the exercise is saved, the main acitivty needs to
             launch using the original date the user left from.
             */

            String intentDate = getIntent().getStringExtra("currDate");
            DateTime referenceDate = currentDate;
            DateTime storedDate = new DateTime(intentDate);

            // Also need to calculate the difference in position between the default date and date the
            // user wanted to add an exercise to.
            int numDaysFromNewDate = referenceDate.numDaysFrom(storedDate);

            currentDate = storedDate;
            storeCurrentDate(currentDate);

            // Based on the calculation above, the position is set to the date the user originally '
            // left the main activity from.
            mPager.setCurrentItem(mPager.getCurrentItem() + numDaysFromNewDate);
            mPager.setSelected(true);

        }
        //default date on app power up is today.
        else {
            storeCurrentDate(currentDate);
        }

        String username = loginPrefs.getString("username");
        setTitle("Welcome, " + username + "!");

        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

             /*
                When the page is scrolled or selected, the current date needs to be adjusted based
                on it's position in the view pager. Syncing allows the UIs that use the current date
                (the view pager and date picker) to have the same date information. The preference
                file that contains the current date information for use in other activities then
                needs to be udpated.
             */
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

        /*
            When the user selects a date using the date picker, the position of the view pager needs
            to be updated. The correct position is calculated by taking the numerical difference
            between the date the user left from when entering the date picker and the new date picked.
         */
        date = new DatePickerDialog.OnDateSetListener() {
          public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
          {
              DateTime referenceDate = currentDate;
              DateTime datePicked = DateTime.forDateOnly(year, monthOfYear + 1, dayOfMonth);
              int numDaysFromNewDate = referenceDate.numDaysFrom(datePicked);
              currentDate = datePicked;

              storeCurrentDate(currentDate);
              mPager.setCurrentItem(mPager.getCurrentItem() + numDaysFromNewDate);
              mPager.setSelected(true);

          }
        };

    }

    public static DateTime syncDate(int position, DateTime dateToSync){

        // Calculate the positional difference between the newly selected selected position and the
        // default position (which is Today).
        int positionDifference = position - (INITIAL_POSITION);

        DateTime pagerdate = DateTime.now(TimeZone.getDefault());
        dateToSync = pagerdate.plusDays(positionDifference);

        return dateToSync;
    }

    public void storeCurrentDate(DateTime date){

        //format to match the 'DATE' field in the SQL database (which stores dates as YYYY-MM-DD)
        String currDate = date.format("YYYY-MM-DD", Locale.getDefault()).toString();

        //Store the date in a preference file so that other activities can use it.
        SharedPreferences datePref = getSharedPreferences("date-pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = datePref.edit();
        editor.putString("currDate", currDate);
        editor.commit();
    }

    //Used upon logout so there is no unnecessary persisting data.
    public void clearAllPreferences(){
        String rememberLogin = loginPrefs.getString("rememberLogin");

        if(rememberLogin.equals("false")) {
            loginPrefs.clear();
        }

        SharedPreferences datePref = getSharedPreferences("date-pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = datePref.edit();
        editor.clear();
        editor.commit();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //Handles items on thne action bar
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId()){

            //Included for future functionality
            case R.id.action_settings:
                return true;

            //Logout returns to the login screen and clears any user data
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

            //Sends the user to the UI to add an exercise
            case R.id.action_addexercise:
                startActivity(new Intent(MainActivity.this, AddExerciseActivity.class));
                return true;

            //Opens a date picker
            case R.id.action_pickdate:
                new DatePickerDialog(this, date, currentDate.getYear(), currentDate.getMonth()-1, currentDate.getDay()).show();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }

    }

    /*
        Adapts fragment data to the screen based on the position of the view pager.
        Each Fragment represents a date which contains a set of exercises.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        DateTime titleDate;

        public ScreenSlidePagerAdapter(Resources resources, FragmentManager fm) {
            super(fm);
        }

        public int getItemPosition(Object object){
            return FragmentStatePagerAdapter.POSITION_NONE;
        }

        // Creates a new fragment when the pager loads a new screen
        // getItem is called twice to create the effect of pagination.
        // It loads the current fragment and the next fragment.
        public Fragment getItem(int position){

            return new ScreenSlidePageFragment().newInstance();
        }

        @Override

        public CharSequence getPageTitle(int position) {

            CharSequence cs;

            //Updates teh date based on the fragment position in the view pager.
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
        //Controls how many fragments are in the view pager
        public int getCount() {
            return NUM_PAGES;
        }
    }

    public void onDestroy() {
        super.onDestroy();
        clearAllPreferences();
    }


}
