package com.wahnaton.testapp.testappli;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedHashMap;


public class WorkoutAdapter extends ArrayAdapter<ExerciseSetModel> {

    private Context context;
    private ArrayList<ExerciseSetModel> exerciseSetList;
    private JSONParser jParser = new JSONParser();
    private String isComplete;
    private String exerciseId;

    public WorkoutAdapter(Context context, ArrayList<ExerciseSetModel> objects) {

        super(context, R.layout.exercise_set_item, objects);

        this.context = context;
        this.exerciseSetList = objects;
        isComplete = "";
    }

    private class ViewHolder{
        TextView tvExerciseSetName;
        TextView tvExerciseSetDetails;
        CheckBox cbIsComplete;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        RelativeLayout view = (RelativeLayout) convertView;
        ViewHolder holder = null;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = (RelativeLayout) inflater.inflate(R.layout.exercise_set_item, parent, false);

            holder = new ViewHolder();
            holder.tvExerciseSetName = (TextView) view.findViewById(R.id.exerciseSetName);
            holder.tvExerciseSetDetails = (TextView) view.findViewById(R.id.exerciseSetDetails);
            holder.cbIsComplete = (CheckBox) view.findViewById(R.id.cbIsComplete);
        }
        else{
            holder = (ViewHolder) view.getTag();
        }

        if (exerciseSetList.get(position).getIsComplete() == 1) {
            holder.cbIsComplete.setChecked(true);
            isComplete = "1";
        }
        else {
            holder.cbIsComplete.setChecked(false);
            isComplete = "0";
        }

        holder.cbIsComplete.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    exerciseSetList.get(position).setIsComplete(1);
                    exerciseId = exerciseSetList.get(position).getExerciseId() + "";
                    isComplete = "1";
                } else {
                    exerciseSetList.get(position).setIsComplete(0);
                    exerciseId = exerciseSetList.get(position).getExerciseId() + "";
                    isComplete = "0";
                }
                new UpdateExerciseComplete().execute();
            }
        });

        holder.tvExerciseSetName.setText(exerciseSetList.get(position).getExerciseName());
        holder.tvExerciseSetDetails.setText(exerciseSetList.get(position).getExerciseDetails() + " id: " + exerciseSetList.get(position).getExerciseId());


        return view;
    }

    class UpdateExerciseComplete extends AsyncTask<String, String, String> {

        private String updateIsCompleteUrl;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {

            updateIsCompleteUrl = "http://192.168.1.9:80/android_connect/updateIsComplete.php";

            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put("is_complete", isComplete);
            params.put("exercise_id", exerciseId);
            jParser.makePostRequest(updateIsCompleteUrl, params);

            return null;
        }

        protected void onPostExecute(String file_url) {
        }
    }
}
