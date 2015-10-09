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
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;


public class ExerciseInfoActivity extends AppCompatActivity {

    private Button bSaveExercise, bPlusWeight, bMinusWeight, bPlusReps, bMinusReps, bClearExerciseInput;
    private EditText etWeight, etReps;
    private ProgressDialog saveExerciseDialog;
    private TextView warningMessage;

    private JSONParser jsonParser;

    private double weight, reps;
    private String currDate, exerciseName, isExerciseComplete;
    private static String insertExerciseInfoUrl = "http://192.168.1.9:80/android_connect/insertExerciseInfo.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_info);

        isExerciseComplete = "false";


        Intent intent = getIntent();
        exerciseName = intent.getStringExtra("exercise");

        SharedPreferences datePref = getSharedPreferences("date-pref", Context.MODE_PRIVATE);
        currDate = datePref.getString("currDate", "Date not found.");
        setTitle(exerciseName + " on " + currDate);

        jsonParser = new JSONParser();

        weight = 0;
        reps = 0;

        warningMessage = (TextView) findViewById(R.id.tvWarningMessage);
        warningMessage.setTextColor(Color.RED);

        bSaveExercise = (Button) findViewById(R.id.bSaveExercise);
        bSaveExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SaveExercise().execute();
            }
        });

        bPlusWeight = (Button) findViewById(R.id.bPlusWeight);
        bPlusWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weight += 5;
                etWeight.setText("" + weight);
            }
        });

        bMinusWeight= (Button) findViewById(R.id.bMinusWeight);
        bMinusWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weight -= 5;
                if(weight < 0)
                    weight = 0;
                etWeight.setText(""+weight);
            }
        });

        bPlusReps = (Button) findViewById(R.id.bPlusReps);
        bPlusReps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reps += 5;
                etReps.setText(""+reps);
            }
        });

        bMinusReps = (Button) findViewById(R.id.bMinusReps);
        bMinusReps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reps -= 5;
                if(reps < 0)
                    reps = 0;
                etReps.setText(""+reps);
            }
        });

        bClearExerciseInput = (Button) findViewById(R.id.bClearExerciseInput);
        bClearExerciseInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weight = 0;
                reps = 0;
                etWeight.setText("");
                etReps.setText("");
            }
        });

        etWeight = (EditText) findViewById(R.id.etWeight);
        etWeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                if(text.length() > 0)
                    weight = Double.parseDouble(text);
                else
                    weight = 0;
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        etReps = (EditText) findViewById(R.id.etReps);
        etReps.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                if(text.length() > 0)
                    reps = Double.parseDouble(text);
                else
                    reps = 0;
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

    }

    class SaveExercise extends AsyncTask<String, String, String> {

        boolean emptyFieldError;

        protected void onPreExecute(){
            super.onPreExecute();
            saveExerciseDialog = new ProgressDialog(ExerciseInfoActivity.this);
            saveExerciseDialog.setMessage("Saving exercise...");
            saveExerciseDialog.setIndeterminate(false);
            saveExerciseDialog.setCancelable(true);
            saveExerciseDialog.show();
            warningMessage.setText("");
        }

        protected String doInBackground(String... args) {

            emptyFieldError = false;

            if(etWeight.equals("") || etReps.equals("") || weight == 0 || reps == 0)
                emptyFieldError = true;
            else {
                LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
                params.put("exercise_name", exerciseName);
                params.put("weight", "" + weight);
                params.put("reps", "" + reps);
                params.put("exercise_complete", isExerciseComplete);
                params.put("date", currDate);
                System.out.println(params.toString());

                JSONObject json = jsonParser.makePostRequest(insertExerciseInfoUrl, params);

                try {
                    int success = json.getInt("success");
                    if (success == 1) {

                        // successfully added data
                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
