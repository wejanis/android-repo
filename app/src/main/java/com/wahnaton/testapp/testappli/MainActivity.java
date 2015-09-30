package com.wahnaton.testapp.testappli;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
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

public class MainActivity extends AppCompatActivity {

    private SecurePreferences loginPrefs;
    private DateTime datePicked;
    private ViewPager mPager;
    private int numDays;
    private DatePickerDialog.OnDateSetListener date;
    private static int NUM_PAGES = 5000;
    private PagerTitleStrip pts;

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

        mPager = (ViewPager) findViewById(R.id.mPager);
        pts = (PagerTitleStrip) findViewById(R.id.tsPager);
        mPager.setAdapter(new ScreenSlidePagerAdapter(getResources(), getSupportFragmentManager()));
        mPager.setCurrentItem(NUM_PAGES / 2, false);

        mPager.getAdapter().notifyDataSetChanged();
        mPager.setOffscreenPageLimit(0);
        pts.setNonPrimaryAlpha(0);

        //TODO: Needs to be changed to use server username no preferences.
        //TODO: Currently only works when "Remember Me" checkbox is selected.
        String username = loginPrefs.getString("username");
        setTitle("Welcome, " + username + "!");

        datePicked = DateTime.now(TimeZone.getDefault());
        numDays = mPager.getCurrentItem() - (NUM_PAGES/2);

        date = new DatePickerDialog.OnDateSetListener() {
          public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
          {
              numDays = mPager.getCurrentItem() - (NUM_PAGES/2);
              System.out.println("position has been moved by " + numDays + " days");

              //DatePicker indexes the months from 0 (Jan.) - 11 (Dec.) so need to add one to the month parameter.
              datePicked = DateTime.forDateOnly(year, monthOfYear + 1, dayOfMonth);
              System.out.println("datePicked: " + datePicked.toString() + "------------------------------------------");


              DateTime dt = DateTime.now(TimeZone.getDefault());
              System.out.println("dt before day adjustment (should be today's date): " + dt.toString() + "------------------------------------------");

              dt = dt.plusDays(numDays);


              System.out.println("dt should now be the date that the screen was on before datepicker was clicked " + dt.toString() + "------------------------------------------");
              int num = dt.numDaysFrom(datePicked);
              System.out.println("dt num days from datepicked: " + num + "------------------------------------------");
              mPager.setCurrentItem(mPager.getCurrentItem() + num);
              System.out.println("currPos: " + mPager.getCurrentItem() + "------------------------------------------");
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

                new DatePickerDialog(this, date, datePicked.getYear(), datePicked.getMonth()-1, datePicked.getDay()).show();

                return true;
            default:
                return super.onOptionsItemSelected(item);

        }

    }

    private class ScreenSlidePagerAdapter extends FragmentPagerAdapter {
        public ScreenSlidePagerAdapter(Resources resources, FragmentManager fm) {
            super(fm);
        }

        public int getItemPosition(Object object){
            return FragmentStatePagerAdapter.POSITION_NONE;
        }

        public Fragment getItem(int position){

            ScreenSlidePageFragment sspFragment = new ScreenSlidePageFragment();

            return sspFragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            CharSequence cs;

            DateTime pagerdate = DateTime.now(TimeZone.getDefault());
            DateTime days = pagerdate.plusDays(position - (NUM_PAGES/2));

            if(position - (NUM_PAGES/2) == 0)
                cs = "Today";
            else if (position - (NUM_PAGES/2) == 1)
                cs = "Tomorrow";
            else if (position - (NUM_PAGES/2) == -1)
                cs = "Yesterday";
            else
                cs = days.format("WWW, MM/DD/YYYY", Locale.getDefault()).toString();

            return cs;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }


}
