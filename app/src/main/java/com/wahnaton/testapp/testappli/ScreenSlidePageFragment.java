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
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

        exerciseSets = new ArrayList<>();

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
                                dialog.dismiss();
                                final int updateId = exerciseSets.get(position).getExerciseId();
                                final String oldWeight = exerciseSets.get(position).getWeight();
                                final String oldReps = exerciseSets.get(position).getReps();

                                LinearLayout layout = new LinearLayout(getActivity());
                                layout.setOrientation(LinearLayout.VERTICAL);

                                final EditText etWeight = new EditText(getActivity());
                                etWeight.setHint("Weight");
                                etWeight.setInputType(InputType.TYPE_CLASS_NUMBER);
                                etWeight.addTextChangedListener(new TextWatcher() {
                                    @Override
                                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                    }

                                    @Override
                                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                                        String text = s.toString();
                                        if (text.length() > 0) {
                                            exerciseSets.get(position).setWeight(text);
                                            //System.out.println(exerciseSets.get(position).getWeight());
                                        } else {
                                            exerciseSets.get(position).setWeight(oldWeight);
                                            //System.out.println(exerciseSets.get(position).getWeight());
                                        }
                                    }

                                    @Override
                                    public void afterTextChanged(Editable s) {
                                    }
                                });
                                layout.addView(etWeight);

                                final EditText etReps = new EditText(getActivity());
                                etReps.setHint("Reps");
                                etReps.setInputType(InputType.TYPE_CLASS_NUMBER);
                                etReps.addTextChangedListener(new TextWatcher() {
                                    @Override
                                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                    }

                                    @Override
                                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                                        String text = s.toString();
                                        if (text.length() > 0) {
                                            exerciseSets.get(position).setReps(text);
                                            //System.out.println(exerciseSets.get(position).getReps());
                                        } else {
                                            exerciseSets.get(position).setReps(oldReps);
                                            //System.out.println(exerciseSets.get(position).getReps());
                                        }
                                    }

                                    @Override
                                    public void afterTextChanged(Editable s) {
                                    }
                                });
                                layout.addView(etReps);

                                final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

                                alert.setTitle("Update your exercise");
                                alert.setView(layout);

                                alert.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        final String newWeight = exerciseSets.get(position).getWeight();
                                        final String newReps = exerciseSets.get(position).getReps();
                                        exerciseSets.get(position).setExerciseDetails(newWeight, newReps);
                                        new UpdateExercise(updateId, position, newWeight, newReps).execute();
                                        adapter.notifyDataSetChanged();
                                    }
                                });

                                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });

                                alert.show();

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

            String removeExerciseUrl = "http://192.168.1.12:80/android_connect/removeExercise.php";

            LinkedHashMap<String, String> params = new LinkedHashMap<>();
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
        private String weight;
        private String reps;

        public UpdateExercise(int updateId, int updatePosition, String weight, String reps){
            this.updateId = updateId;
            this.weight = weight;
            this.reps = reps;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {

            String updateExerciseUrl = "http://192.168.1.12:80/android_connect/updateExercise.php";

            LinkedHashMap<String, String> params = new LinkedHashMap<>();
            params.put("update_id", updateId + "");
            params.put("weight", weight);
            params.put("reps", reps);
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

            String copyExerciseUrl = "http://192.168.1.12:80/android_connect/copyExercise.php";

            LinkedHashMap<String, String> params = new LinkedHashMap<>();
            params.put("copy_id", copyId + "");
            JSONObject json = jParser.makePostRequest(copyExerciseUrl, params);

            String nameToCopy = exerciseSets.get(copyPosition).getExerciseName();
            String weight = exerciseSets.get(copyPosition).getWeight();
            String reps = exerciseSets.get(copyPosition).getReps();
            int isCompleteCopy = exerciseSets.get(copyPosition).getIsComplete();

            try {
                //the new exercise set has it's own unique id in the database separate from the one it copied.
                int newId = json.getInt("new_id");
                ExerciseSetModel copy = new ExerciseSetModel(newId, nameToCopy, weight, reps, isCompleteCopy);
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

            exerciseDataUrl = "http://192.168.1.12:80/android_connect/getUserData.php";

            loginPrefs = new SecurePreferences(getActivity().getApplicationContext(), "user-info", "randomTestingPurposesKey", true);
            username = loginPrefs.getString("username");

            datePref = getActivity().getSharedPreferences("date-pref", Context.MODE_PRIVATE);
            currDate = datePref.getString("currDate", "Date not found.");

            LinkedHashMap<String, String> params = new LinkedHashMap<>();
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

                    ExerciseSetModel set = new ExerciseSetModel(exerciseId, exerciseName, weight, reps, exerciseComplete);
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
