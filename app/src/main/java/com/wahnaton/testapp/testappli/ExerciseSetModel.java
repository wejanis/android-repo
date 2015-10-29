package com.wahnaton.testapp.testappli;

public class ExerciseSetModel {

    private int exerciseId;
    private String exerciseName;
    private String exerciseDetails;
    private int isComplete;
    private String weight;
    private String reps;

    public ExerciseSetModel(int exerciseId, String exerciseName, String weight, String reps, int isComplete) {

        this.exerciseId = exerciseId;
        this.exerciseName = exerciseName;
        this.weight = weight;
        this.reps = reps;
        this.isComplete = isComplete;

        exerciseDetails = weight + "lbs, " + reps + " rep(s)";
    }

    public int getExerciseId(){

        return exerciseId;
    }

    public String getExerciseName(){
        return this.exerciseName;
    }

    public String getWeight(){
        return this.weight;
    }

    public void setWeight(String weight){
        this.weight = weight;
    }

    public String getReps(){
        return this.reps;
    }

    public void setReps(String reps){
        this.reps = reps;
    }

    public String getExerciseDetails(){
        return this.exerciseDetails;
    }

    public void setExerciseDetails(String weight, String reps){
        this.exerciseDetails = weight + "lbs, " + reps + " rep(s)";
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
