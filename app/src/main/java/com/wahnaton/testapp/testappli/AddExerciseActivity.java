package com.wahnaton.testapp.testappli;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

/*
    The Add Exercise Activity allows the user to select which exercise
    was completed from a list. Also allows for filtering based on text input.
 */

public class AddExerciseActivity extends Activity {

    private EditText etSearch;
    private ListView exerciseListView;
    private ListAdapter exerciseAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_exercise);
        setTitle("");

        //The list of exercise is hardcoded for test purposes right now. In the future,
        //the list would be read from a file to be easily modifiable.
        String[] exercises = {"Bicep curls", "Bench Press", "Squats", "Deadlifts"};

        //Adapter maps each exercise to an item in the list view.
        exerciseAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, exercises);

        //When a user clicks on an exercise in the list, this activity sends
        //the exercise name to the Exercise Info activity.
        exerciseListView = (ListView) findViewById(android.R.id.list);
        exerciseListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String selectedExercise = (String) exerciseAdapter.getItem(position);
                Intent i = new Intent(getApplicationContext(), ExerciseInfoActivity.class);
                i.putExtra("exercise", selectedExercise);
                startActivity(i);

            }
        });
        exerciseListView.setAdapter(exerciseAdapter);

        //Filters the text in the listview based on user input in the search field.
        etSearch = (EditText) findViewById(R.id.etSearch);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ((ArrayAdapter<String>) AddExerciseActivity.this.exerciseAdapter).getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });



    }

}
