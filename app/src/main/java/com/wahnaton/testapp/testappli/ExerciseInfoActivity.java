package com.wahnaton.testapp.testappli;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class ExerciseInfoActivity extends AppCompatActivity {

    private Button bPlusWeight, bMinusWeight, bPlusReps, bMinusReps, bClearExerciseInput;
    private EditText etWeight, etReps;

    private double weight, reps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_info);

        Intent intent = getIntent();
        String title = intent.getStringExtra("exercise");
        setTitle(title);

        weight = 0;
        reps = 0;

        bPlusWeight = (Button) findViewById(R.id.bPlusWeight);
        bMinusWeight= (Button) findViewById(R.id.bMinusWeight);
        bPlusReps = (Button) findViewById(R.id.bPlusReps);
        bMinusReps = (Button) findViewById(R.id.bMinusReps);
        bClearExerciseInput = (Button) findViewById(R.id.bClearExerciseInput);
        etWeight = (EditText) findViewById(R.id.etWeight);
        etReps = (EditText) findViewById(R.id.etReps);

        bPlusWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weight += 5;
                etWeight.setText("" + weight);
            }
        });

        bMinusWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weight -= 5;
                if(weight < 0)
                    weight = 0;
                etWeight.setText(""+weight);
            }
        });
        bPlusReps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reps += 5;
                etReps.setText(""+reps);
            }
        });
        bMinusReps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reps -= 5;
                if(reps < 0)
                    reps =0;
                etReps.setText(""+reps);
            }
        });

        bClearExerciseInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weight = 0;
                reps = 0;
                etWeight.setText("");
                etReps.setText("");
            }
        });

        etWeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                if(text.length() > 0)
                    weight = Double.parseDouble(text);
                else
                    weight = 0;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

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
            public void afterTextChanged(Editable s) {

            }
        });

    }
}
