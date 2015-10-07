package com.wahnaton.testapp.testappli;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ScreenSlidePageFragment extends Fragment implements View.OnClickListener{

    private ImageView ivAddExercise;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_screen_slide_page, container, false);

        ivAddExercise = (ImageView) rootView.findViewById(R.id.ivAddExercise);

        ivAddExercise.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.ivAddExercise:
                startActivity(new Intent(v.getContext(), AddExerciseActivity.class));
                break;

        }

    }
}
