package com.wahnaton.testapp.testappli;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class ScreenSlidePageFragment extends Fragment{

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
        ivAddExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.ivAddExercise:
                        startActivity(new Intent(v.getContext(), AddExerciseActivity.class));
                        break;
                }
            }
        });
        tvAddExercise = (TextView) rootView.findViewById(R.id.tvAddExercise);

        lvExerciseSets = (ListView) rootView.findViewById(R.id.lvExerciseSets);
        lvExerciseSets.setEmptyView(tvEmpty);
        lvExerciseSets.setLongClickable(true);

        lvExerciseSets.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
                adb.setTitle("What would you like to do?");

                CharSequence options[] = new CharSequence[] {"Update", "Copy", "Delete", "Cancel"};

                adb.setItems(options, new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        switch(which){

                            //Update
                            case 0:
                                //int updateId = exerciseSets.get(position).getExerciseId();

                                break;

                            //Copy
                            case 1:
                                int copyId = exerciseSets.get(position).getExerciseId();
                                new CopyExercise(copyId, position).execute();
                                break;

                            //Delete
                            case 2:
                                int deleteId = exerciseSets.get(position).getExerciseId();
                                new RemoveExercise(deleteId, position).execute();
                                break;
                            default:
                                break;
                        }
                    }
                });
                adb.show();

                return true;
            }
        });

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

    private class RemoveExercise extends AsyncTask<String, String, String> {

        private int deleteId;
        private int deletePosition;

        public RemoveExercise(int deleteId, int deletePosition) {
            this.deleteId = deleteId;
            this.deletePosition = deletePosition;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {

            String removeExerciseUrl = "http://192.168.1.9:80/android_connect/removeExercise.php";

            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put("delete_id", deleteId + "");
            jParser.makePostRequest(removeExerciseUrl, params);
            exerciseSets.remove(deletePosition);


            return null;
        }

        protected void onPostExecute(String file_url) {
            if (exerciseSets.size() < 4) {
                ivAddExercise.setVisibility(View.VISIBLE);
                tvAddExercise.setVisibility(View.VISIBLE);
            }
            adapter.notifyDataSetChanged();
        }
    }

    private class UpdateExercise extends AsyncTask<String, String, String> {

        private int updateId;
        private int updatePosition;

        public UpdateExercise(int updateId, int updatePosition){
            this.updateId = updateId;
            this.updatePosition = updatePosition;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {

            String updateExerciseUrl = "http://192.168.1.9:80/android_connect/updateExercise.php";

            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put("update_id", updateId + "");
            jParser.makePostRequest(updateExerciseUrl, params);

            return null;
        }

        protected void onPostExecute(String file_url) {
        }
    }

    private class CopyExercise extends AsyncTask<String, String, String> {

        private int copyId;
        private int copyPosition;

        public CopyExercise(int copyId, int copyPosition){
            this.copyId = copyId;
            this.copyPosition = copyPosition;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {

            String copyExerciseUrl = "http://192.168.1.9:80/android_connect/copyExercise.php";

            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put("copy_id", copyId + "");
            JSONObject json = jParser.makePostRequest(copyExerciseUrl, params);

            String nameToCopy = exerciseSets.get(copyPosition).getExerciseName();
            String detailsToCopy = exerciseSets.get(copyPosition).getExerciseDetails();
            int isCompleteCopy = exerciseSets.get(copyPosition).getIsComplete();

            try {
                //the new exercise set has it's own unique id in the database separate from the one it copied.
                int newId = json.getInt("new_id");
                ExerciseSetModel copy = new ExerciseSetModel(newId, nameToCopy, detailsToCopy, isCompleteCopy);
                exerciseSets.add(copy);

            }
            catch (JSONException e){
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            if (exerciseSets.size() > 3) {
                ivAddExercise.setVisibility(View.GONE);
                tvAddExercise.setVisibility(View.GONE);
            }
            adapter.notifyDataSetChanged();

        }
    }

    private class LoadUserExerciseSets extends AsyncTask<String, String, String> {

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
            //System.out.println("jsondata " + jsonData.toString());

            try {
                // looping through exercise sets
                for (int i = 0; i < jsonData.length(); i++) {

                    JSONObject c = jsonData.getJSONObject(i);

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
