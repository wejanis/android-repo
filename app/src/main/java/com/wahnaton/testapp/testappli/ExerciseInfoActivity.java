package com.wahnaton.testapp.testappli;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;


public class ExerciseInfoActivity extends AppCompatActivity implements View.OnClickListener{

    private Button bSaveExercise, bPlusWeight, bMinusWeight, bPlusReps, bMinusReps, bPlusSets, bMinusSets, bClearExerciseInput;
    private EditText etWeight, etReps, etSets;
    private ProgressDialog saveExerciseDialog;
    private TextView warningMessage;
    private CheckBox cbExerciseComplete;

    private double weight;
    private int reps, sets;
    private String currDate, exerciseName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_info);

        //Grabs the name of the exercise selected in the add exercise activity
        Intent intent = getIntent();
        exerciseName = intent.getStringExtra("exercise");

        //Loads the current date from the main activity.
        SharedPreferences datePref = getSharedPreferences("date-pref", Context.MODE_PRIVATE);
        currDate = datePref.getString("currDate", "Date not found.");
        setTitle(exerciseName + " on " + currDate);

        weight = 0;
        reps = 0;
        sets = 0;

        cbExerciseComplete = (CheckBox) findViewById(R.id.cbExerciseComplete);
        cbExerciseComplete.setTextColor(Color.DKGRAY);
        cbExerciseComplete.setChecked(false);

        warningMessage = (TextView) findViewById(R.id.tvWarningMessage);
        warningMessage.setTextColor(Color.RED);

        bSaveExercise = (Button) findViewById(R.id.bSaveExercise);
        bSaveExercise.setOnClickListener(this);

        bPlusWeight = (Button) findViewById(R.id.bPlusWeight);
        bPlusWeight.setOnClickListener(this);

        bMinusWeight = (Button) findViewById(R.id.bMinusWeight);
        bMinusWeight.setOnClickListener(this);

        bPlusReps = (Button) findViewById(R.id.bPlusReps);
        bPlusReps.setOnClickListener(this);

        bMinusReps= (Button) findViewById(R.id.bMinusReps);
        bMinusReps.setOnClickListener(this);

        bPlusSets= (Button) findViewById(R.id.bPlusSets);
        bPlusSets.setOnClickListener(this);

        bMinusSets = (Button) findViewById(R.id.bMinusSets);
        bMinusSets.setOnClickListener(this);

        bClearExerciseInput = (Button) findViewById(R.id.bClearExerciseInput);
        bClearExerciseInput.setOnClickListener(this);

        // The Text watcher for weight, reps, and sets updates the value of these
        // variables if the user updates using the edit text rather than the +/- buttons
        etWeight = (EditText) findViewById(R.id.etWeight);
        etWeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                if (text.length() > 0)
                    weight = Double.parseDouble(text);
                else
                    weight = 0;
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        etReps = (EditText) findViewById(R.id.etReps);
        etReps.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                if(text.length() > 0)
                    reps = Integer.parseInt(text);
                else
                    reps = 0;
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        etSets = (EditText) findViewById(R.id.etSets);
        etSets.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                if(text.length() > 0)
                    sets = Integer.parseInt(text);
                else
                    sets = 0;
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            // If the user saves the exercise, a background task is run and it creates and
            // it stores the data in the database.
            case R.id.bSaveExercise:
                boolean isChecked = cbExerciseComplete.isChecked();
                new SaveExercise(isChecked).execute();
                break;

            //When the user clicks the +/- buttons next to weight/reps/sets the value  is
            // incremented and the text is updated.
            case R.id.bPlusWeight:
                weight += 5;
                etWeight.setText("" + weight);
                break;
            case R.id.bMinusWeight:
                weight -= 5;
                if(weight < 0)
                    weight = 0;
                etWeight.setText("" + weight);
                break;
            case R.id.bPlusReps:
                reps += 1;
                etReps.setText(""+reps);
                break;
            case R.id.bMinusReps:
                reps -= 1;
                if(reps < 0)
                    reps = 0;
                etReps.setText(""+reps);
                break;
            case R.id.bPlusSets:
                sets += 1;
                etSets.setText(""+sets);
                break;
            case R.id.bMinusSets:
                sets -= 1;
                if(sets < 0)
                    sets = 0;
                etSets.setText(""+sets);
                break;

            //Clears all the text and values of all information when the user clicks Clear
            case R.id.bClearExerciseInput:
                weight = 0;
                reps = 0;
                sets = 0;
                etWeight.setText("");
                etReps.setText("");
                etSets.setText("");
                cbExerciseComplete.setChecked(false);
                break;
        }


    }

    /*
        SaveExercise is a background task that stores the user-entered data into the database
        and associates that data with the user's database id.
     */
    class SaveExercise extends AsyncTask<String, String, String> {

        private boolean emptyFieldError;
        private boolean isChecked;
        private SecurePreferences loginPrefs;
        private String username;
        private JSONParser jsonParser;

        public SaveExercise(boolean isChecked){
            this.isChecked = isChecked;
        }

        protected void onPreExecute(){
            super.onPreExecute();

            //UI notifies the user that data is being saved.
            saveExerciseDialog = new ProgressDialog(ExerciseInfoActivity.this);
            saveExerciseDialog.setMessage("Saving exercise...");
            saveExerciseDialog.setIndeterminate(false);
            saveExerciseDialog.setCancelable(true);
            saveExerciseDialog.show();
            warningMessage.setText("");
        }

        protected String doInBackground(String... args) {

            loginPrefs = new SecurePreferences(getApplicationContext(), "user-info", "randomTestingPurposesKey", true);
            username = loginPrefs.getString("username");
            jsonParser = new JSONParser();

            //Checks to make sure the user entered information in all fields
            emptyFieldError = false;
            if(weight == 0 || reps == 0 || sets == 0)
                emptyFieldError = true;

            //Makes a post request to the server to store the exercise information.
            else {


                String insertExerciseInfoUrl = "http://192.168.1.12:80/android_connect/insertExerciseInfo.php";
                String isExerciseComplete = "false";
                if(isChecked)
                    isExerciseComplete = "true";

                LinkedHashMap<String, String> params = new LinkedHashMap<>();
                params.put("exercise_name", exerciseName);
                params.put("weight", "" + weight);
                params.put("reps", "" + reps);
                params.put("sets", "" + sets);
                params.put("exercise_complete", isExerciseComplete);
                params.put("date", currDate);
                params.put("username", username);
                //System.out.println(params.toString());

                // Makes the post request and recieves a json object with information on the
                // success or failure of the request.
                JSONObject json = jsonParser.makePostRequest(insertExerciseInfoUrl, params);

                try {
                    int success = json.getInt("success");
                    if (success == 1) {

                        // User data was successfully stored, so the user is returned to the main activity
                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        //Lets the main activity know the date the user left wants to return to.
                        i.putExtra("currDate", currDate);
                        startActivity(i);
                        finish();
                    } else {
                        String field_error = json.getString("error_message");
                        if (field_error.equals("A required field is missing"))
                            emptyFieldError = true;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        protected void onPostExecute(String file_url){

            if(emptyFieldError) {
                warningMessage.setText("Please enter information for all fields.");
                warningMessage.setTextColor(Color.RED);
            }

            saveExerciseDialog.dismiss();
        }
    }
}
