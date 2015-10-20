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
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class ScreenSlidePageFragment extends Fragment implements View.OnClickListener{

    private TextView tvEmpty;
    private ImageView ivAddExercise;
    private TextView tvAddExercise;
    private ListView lvExerciseSets;
    private WorkoutAdapter adapter;
    private ProgressDialog pDialog;

    private String username;
    private String currDate;
    private boolean areExercisesLoaded = false;

    JSONParser jParser = new JSONParser();
    ArrayList<ExerciseSetModel> exerciseSets;
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

        exerciseSets = new ArrayList<ExerciseSetModel>();

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

            JSONArray jsonData = jParser.makeGetRequest(exerciseDataUrl, params);
            System.out.println("jsondata " + jsonData.toString());

            try {
                // looping through exercise sets
                for (int i = 0; i < jsonData.length(); i++) {

                    JSONObject c = jsonData.getJSONObject(i);
                    System.out.println("JSON OBJECT: " + c.toString() + "!!!!");

                    int exerciseId = Integer.parseInt(c.getString("id"));
                    String exerciseName = c.getString("exercise_name");
                    String weight = c.getString("weight");
                    String reps = c.getString("reps");
                    int exerciseComplete = Integer.parseInt(c.getString("exercise_complete"));

                    String exerciseDetails = weight + "lbs, " + reps + " rep(s)";

                    ExerciseSetModel set = new ExerciseSetModel(exerciseId, exerciseName, exerciseDetails, exerciseComplete);
                    //System.out.println(set.toString());

                    exerciseSets.add(set);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String file_url) {
            pDialog.dismiss();

            if(exerciseSets.size() > 3){
                ivAddExercise.setVisibility(View.GONE);
                tvAddExercise.setVisibility(View.GONE);
            }

            adapter = new WorkoutAdapter(getActivity().getApplicationContext(), exerciseSets);

            lvExerciseSets.setAdapter(adapter);
        }

    }
}
