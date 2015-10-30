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

/*
    The WorkoutAdapter class is a custom adapter that is based off of array adapter.
    This custom adapter maps data from an array of ExerciseSetModel objects.
 */
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

    //Use the viewholder pattern to help speed up rendering of ListView.
    // Helps with handling the recyling of views when scrolling a list.
    private class ViewHolder{
        TextView tvExerciseSetName;
        TextView tvExerciseSetDetails;
        CheckBox cbIsComplete;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        RelativeLayout view = (RelativeLayout) convertView;
        ViewHolder holder;

        if (view == null) {

            //Setup the view if it is null and tag it.
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = (RelativeLayout) inflater.inflate(R.layout.exercise_set_item, parent, false);

            holder = new ViewHolder();
            holder.tvExerciseSetName = (TextView) view.findViewById(R.id.exerciseSetName);
            holder.tvExerciseSetDetails = (TextView) view.findViewById(R.id.exerciseSetDetails);
            holder.cbIsComplete = (CheckBox) view.findViewById(R.id.cbIsComplete);
            view.setTag(holder);
        }
        else{
            holder = (ViewHolder) view.getTag();
        }

        //When loading each item, see if the checkbox should have a checkmark or not.
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

                //When checked, update the exercise set list model and grab the id
                // of the list item that was clicked.  Flag the isComplete as 1.
                // (isComplete can only be stored inthe db as 0 or 1.)
                if (isChecked) {
                    exerciseSetList.get(position).setIsComplete(1);
                    exerciseId = exerciseSetList.get(position).getExerciseId() + "";
                    isComplete = "1";
                }

                // When unchecked, udpate the exercise set list model, grab the id of the list
                // item that was clicked and flag the isComplete as 0.
                else {
                    exerciseSetList.get(position).setIsComplete(0);
                    exerciseId = exerciseSetList.get(position).getExerciseId() + "";
                    isComplete = "0";
                }
                new UpdateExerciseComplete().execute();
            }
        });

        //Load and display the information from the model to the user.
        holder.tvExerciseSetName.setText(exerciseSetList.get(position).getExerciseName());
        holder.tvExerciseSetDetails.setText(exerciseSetList.get(position).getExerciseDetails() + " id: " + exerciseSetList.get(position).getExerciseId());


        return view;
    }

    /*
        When the user clicks the 'Complete' checkbox, this background task is executed
        and sends a post request to the server letting it know to update the completion
        information in the database.
     */
    class UpdateExerciseComplete extends AsyncTask<String, String, String> {

        private String updateIsCompleteUrl;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {

            updateIsCompleteUrl = "http://192.168.1.12:80/android_connect/updateIsComplete.php";

            LinkedHashMap<String, String> params = new LinkedHashMap<>();

            //Let the server know the id and completion status of the exercise to udpate.
            params.put("is_complete", isComplete);
            params.put("exercise_id", exerciseId);
            jParser.makePostRequest(updateIsCompleteUrl, params);

            return null;
        }

        protected void onPostExecute(String file_url) {
        }
    }
}
