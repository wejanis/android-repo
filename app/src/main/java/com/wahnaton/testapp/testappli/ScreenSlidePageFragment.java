package com.wahnaton.testapp.testappli;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class ScreenSlidePageFragment extends Fragment implements View.OnClickListener{

    private TextView tvEmpty;
    private ImageView ivAddExercise;
    private TextView tvAddExercise;
    private ListView lvExerciseSets;
    private ListAdapter exerciseSetAdapter;
    private ProgressDialog pDialog;

    private String username;
    private String currDate;
    private String exercise_complete;
    private boolean areExercisesLoaded = false;

    JSONParser jParser = new JSONParser();
    ArrayList<HashMap<String, String>> exerciseNames;
    LoadUserExerciseSets sets;

    // JSON Node names
    private static final String TAG_EXERCISE_NAME = "exercise_name";
    private static final String TAG_SUBTEXT = "subtext";

    public static ScreenSlidePageFragment newInstance(){
        return new ScreenSlidePageFragment();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_screen_slide_page, container, false);

        exerciseNames = new ArrayList<HashMap<String, String>>();
        //areExercisesLoaded = false;

        tvEmpty = (TextView) rootView.findViewById(android.R.id.empty);
        ivAddExercise = (ImageView) rootView.findViewById(R.id.ivAddExercise);
        ivAddExercise.setOnClickListener(this);
        tvAddExercise = (TextView) rootView.findViewById(R.id.tvAddExercise);

        lvExerciseSets = (ListView) rootView.findViewById(R.id.lvExerciseSets);

        lvExerciseSets.setEmptyView(tvEmpty);

        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {

        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser && !areExercisesLoaded) {
            new LoadUserExerciseSets().execute();
            areExercisesLoaded = true;
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.ivAddExercise:
                startActivity(new Intent(v.getContext(), AddExerciseActivity.class));
                break;
        }
    }

    class LoadUserExerciseSets extends AsyncTask<String, String, String> {

        private String exerciseDataUrl;
        private SecurePreferences loginPrefs;
        private SharedPreferences datePref;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Loading products. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... args) {

            exerciseDataUrl = "http://192.168.1.9:80/android_connect/getUserData.php";

            loginPrefs = new SecurePreferences(getActivity().getApplicationContext(), "user-info", "randomTestingPurposesKey", true);
            username = loginPrefs.getString("username");

            datePref = getActivity().getSharedPreferences("date-pref", Context.MODE_PRIVATE);
            currDate = datePref.getString("currDate", "Date not found.");

            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put("username", username);
            params.put("exercise_date", currDate);
            System.out.println("date: " + currDate);
            System.out.println("passed in url" + exerciseDataUrl);

            JSONArray exerciseSets = jParser.makeGetRequest(exerciseDataUrl, params);

            try {
                // looping through exercise sets
                for (int i = 0; i < exerciseSets.length(); i++) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    JSONObject c = exerciseSets.getJSONObject(i);
                    System.out.println(c.toString());

                    int sets = Integer.parseInt(c.getString("sets"));

                    for(int j = 0; j < sets; j++) {
                        // Storing each json item in variable
                        String exercise_name = c.getString("exercise_name");
                        String weight = c.getString("weight");
                        String reps = c.getString("reps");

                        exercise_complete = c.getString("exercise_complete");

                        String subText = weight + "lbs, " + reps + " rep(s)";
                        System.out.println("subtext: " + subText);

                        map.put(TAG_EXERCISE_NAME, exercise_name);
                        map.put(TAG_SUBTEXT, subText);
                        exerciseNames.add(map);
                    }

                }
                System.out.println("hashmap: " + exerciseNames.toString());


            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String file_url) {
            pDialog.dismiss();

            if(exerciseNames.size() > 3){
                ivAddExercise.setVisibility(View.GONE);
                tvAddExercise.setVisibility(View.GONE);
            }

            exerciseSetAdapter = new SimpleAdapter(getActivity().getApplicationContext(), exerciseNames,
                    R.layout.exercise_set_item, new String[] { TAG_EXERCISE_NAME, TAG_SUBTEXT},
                    new int[] { R.id.exerciseSetName, R.id.exerciseSetSubtext});

            lvExerciseSets.setAdapter(exerciseSetAdapter);
        }

    }
}
