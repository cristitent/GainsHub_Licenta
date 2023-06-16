package com.example.gymaholicapp;

public class ExerciseData {
    private int exerciseIndex;
    private String exerciseName;
    private int sets;
    private int reps;
    private double weights;
    private String workoutName;

    public ExerciseData() {
    }

    public ExerciseData(int exerciseIndex, String exerciseName, int sets, int reps, double weights, String workoutName) {
        this.exerciseIndex = exerciseIndex;
        this.exerciseName = exerciseName;
        this.sets = sets;
        this.reps = reps;
        this.weights = weights;
        this.workoutName = workoutName;
    }

    public int getExerciseIndex() {
        return exerciseIndex;
    }

    public void setExerciseIndex(int exerciseIndex) {
        this.exerciseIndex = exerciseIndex;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
    }

    public int getSets() {
        return sets;
    }

    public void setSets(int sets) {
        this.sets = sets;
    }

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public double getWeights() {
        return weights;
    }

    public void setWeights(double weights) {
        this.weights = weights;
    }

    public String getWorkoutName() {
        return workoutName;
    }

    public void setWorkoutName(String workoutName) {
        this.workoutName = workoutName;
    }
}
