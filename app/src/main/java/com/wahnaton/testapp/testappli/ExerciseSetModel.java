package com.wahnaton.testapp.testappli;

public class ExerciseSetModel {

    private int exerciseId;
    private String exerciseName;
    private String exerciseDetails;
    private int isComplete;

    public ExerciseSetModel(int exerciseId, String exerciseName, String exerciseDetails, int isComplete) {

        this.exerciseId = exerciseId;
        this.exerciseName = exerciseName;
        this.exerciseDetails = exerciseDetails;
        this.isComplete = isComplete;
    }

    public int getExerciseId(){

        return exerciseId;
    }

    public String getExerciseName(){
        return this.exerciseName;
    }

    public String getExerciseDetails(){
        return this.exerciseDetails;
    }

    public int getIsComplete(){
        return this.isComplete;
    }

    public void setIsComplete(int isComplete){
        this.isComplete = isComplete;
    }


    public String toString()
    {
        return "Exercise id: " + exerciseId + ", Exercise name: " + exerciseName + ", Details: " + exerciseDetails + ", complete?: " + getIsComplete();
    }

}
