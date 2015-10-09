package com.wahnaton.testapp.testappli;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import hirondelle.date4j.DateTime;

public class ScreenSlidePageFragment extends Fragment implements View.OnClickListener{

    private ImageView ivAddExercise;
    private DateTime currDate;

    static ScreenSlidePageFragment newInstance(DateTime date)
    {
        ScreenSlidePageFragment f = new ScreenSlidePageFragment();

        //Give item date as an argument
        Bundle args = new Bundle();
        args.putInt("currDateYear", date.getYear());
        args.putInt("currDateMonth", date.getMonth());
        args.putInt("currDateDay", date.getDay());
        f.setArguments(args);

        return f;
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_screen_slide_page, container, false);

        ivAddExercise = (ImageView) rootView.findViewById(R.id.ivAddExercise);
        ivAddExercise.setOnClickListener(this);

        int year = getArguments().getInt("currDateYear");
        int month = getArguments().getInt("currDateMonth");
        int day = getArguments().getInt("currDateDay");

        currDate = DateTime.forDateOnly(year, month, day);

        return rootView;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.ivAddExercise:
                Intent i = new Intent(v.getContext(), AddExerciseActivity.class);
                i.putExtra("currDate", currDate);
                startActivity(i);
                break;

        }

    }
}
