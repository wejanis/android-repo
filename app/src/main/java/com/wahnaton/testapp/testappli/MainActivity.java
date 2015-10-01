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
    private DateTime currentDay;
    private ViewPager mPager;
    private int numDays;
    private DatePickerDialog.OnDateSetListener date;
    private static int NUM_PAGES = 5000;
    private PagerTitleStrip pts;
    private DatePickerDialog dpd;

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

        currentDay = DateTime.now(TimeZone.getDefault());

        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                DateTime pagerdate = DateTime.now(TimeZone.getDefault());
                currentDay = pagerdate.plusDays(position - (NUM_PAGES/2));
            }
            @Override
            public void onPageSelected(int position) {

            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        date = new DatePickerDialog.OnDateSetListener() {
          public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
          {
              DateTime oldDate = currentDay;
              DateTime datePicked = DateTime.forDateOnly(year, monthOfYear + 1, dayOfMonth);
              int numDaysFromNewDate = oldDate.numDaysFrom(datePicked);
              currentDay = datePicked;
              mPager.setCurrentItem(mPager.getCurrentItem() + numDaysFromNewDate);
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
                new DatePickerDialog(this, date, currentDay.getYear(), currentDay.getMonth()-1, currentDay.getDay()).show();
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
