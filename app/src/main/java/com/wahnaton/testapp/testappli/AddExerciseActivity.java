package com.wahnaton.testapp.testappli;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;


public class AddExerciseActivity extends Activity {

    private EditText etSearch;
    private ListView exerciseListView;
    private ListAdapter exerciseAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_exercise);
        setTitle("");

        String[] exercises = {"blah", "aaaa", "yo", "etc", "asdf", "ttttt", "123", "123", "123", "123", "123", "123", "123", "123", "123", "123"};
        exerciseAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, exercises);

        exerciseListView = (ListView) findViewById(android.R.id.list);
        exerciseListView.setAdapter(exerciseAdapter);

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
