package com.example.gymaholicapp;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.List;

public class Workout {
    private String workoutId;
    private String workoutName;
    private String firstExercise;
    private String secondExercise;
    private String thirdExercise;
    private String fourthExercise;
    private String fifthExercise;

    private List<WorkoutsHistory> workoutEntries;


    public Workout() {
        workoutEntries = new ArrayList<>();
    }

    public Workout(String workoutName, String firstExercise, String secondExercise, String thirdExercise, String fourthExercise, String fifthExercise) {
        this.workoutName = workoutName;
        this.firstExercise = firstExercise;
        this.secondExercise = secondExercise;
        this.thirdExercise = thirdExercise;
        this.fourthExercise = fourthExercise;
        this.fifthExercise = fifthExercise;
    }

    public void addExercise(int exerciseIndex,String exerciseName, int sets, int reps, double weights) {
        ExerciseData exerciseData = new ExerciseData(exerciseIndex,exerciseName, sets, reps, weights);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String exerciseId = db.collection("workouts").document().getId();

        DocumentReference exerciseRef = db.collection("workouts").document(getWorkoutId())
                .collection("exercises").document(exerciseId);

        exerciseRef.set(exerciseData, SetOptions.merge());

        db.collection("workouts").document(getWorkoutId()).update("exerciseCount", FieldValue.increment(1));
    }

    public String getWorkoutId() {
        return workoutId;
    }

    public void setWorkoutId(String workoutId) {
        this.workoutId = workoutId;
    }

    public String getWorkoutName() {
        return workoutName;
    }

    public void setWorkoutName(String workoutName) {
        this.workoutName = workoutName;
    }

    public String getFirstExercise() {
        return firstExercise;
    }

    public void setFirstExercise(String firstExercise) {
        this.firstExercise = firstExercise;
    }

    public String getSecondExercise() {
        return secondExercise;
    }

    public void setSecondExercise(String secondExercise) {
        this.secondExercise = secondExercise;
    }

    public String getThirdExercise() {
        return thirdExercise;
    }

    public void setThirdExercise(String thirdExercise) {
        this.thirdExercise = thirdExercise;
    }

    public String getFourthExercise() {
        return fourthExercise;
    }

    public void setFourthExercise(String fourthExercise) {
        this.fourthExercise = fourthExercise;
    }

    public String getFifthExercise() {
        return fifthExercise;
    }

    public void setFifthExercise(String fifthExercise) {
        this.fifthExercise = fifthExercise;
    }

    public void addExerciseEntry(int exerciseIndex, String exerciseName, int sets, int reps, double weights) {
        WorkoutsHistory entry = new WorkoutsHistory();
        entry.setWorkoutName(workoutName);
        entry.setExerciseIndex(exerciseIndex);
        entry.setExerciseName(exerciseName);
        entry.setSets(sets);
        entry.setReps(reps);
        entry.setWeights(weights);

        workoutEntries.add(entry);
    }

    public List<WorkoutsHistory> getWorkoutEntries() {
        return workoutEntries;
    }
}
