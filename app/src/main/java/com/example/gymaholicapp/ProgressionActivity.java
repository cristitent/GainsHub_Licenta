package com.example.gymaholicapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class ProgressionActivity extends AppCompatActivity {
    private LinearLayout layoutProgressionList;
    private FirebaseFirestore db;
    private LinearLayout layoutProgression;
    private ScrollView svProgression;
    private CollectionReference progressionCollection;
    private CollectionReference progressionExercisesCollection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progression);

        layoutProgressionList = findViewById(R.id.layoutProgresion);
        db = FirebaseFirestore.getInstance();
        layoutProgression = findViewById(R.id.layoutProgresion);
        svProgression = findViewById(R.id.sv_porgresion);

        loadWorkoutProgressionFromDatabase();
    }

    private void loadWorkoutProgressionFromDatabase() {
        layoutProgression.removeAllViews();

        progressionCollection = db.collection("workouts");

        progressionCollection.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot workoutSnapshot : queryDocumentSnapshots) {
                    String workoutId = workoutSnapshot.getId();
                    String workoutName = workoutSnapshot.getString("workoutName");
                    String firstExercise = workoutSnapshot.getString("firstExercise");
                    String secondExercise = workoutSnapshot.getString("secondExercise");
                    String thirdExercise = workoutSnapshot.getString("thirdExercise");
                    String fourthExercise = workoutSnapshot.getString("fourthExercise");
                    String fifthExercise = workoutSnapshot.getString("fifthExercise");

                    Workout workout = new Workout(workoutName, firstExercise, secondExercise, thirdExercise, fourthExercise, fifthExercise);

                    View workoutView = LayoutInflater.from(ProgressionActivity.this).inflate(R.layout.list_progression, null);
                    TextView workoutNameTextView = workoutView.findViewById(R.id.textWorkoutNameP);
                    workoutNameTextView.setText(workoutName);

                    workoutNameTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openProgressionDetailsDialog(workout);
                        }
                    });

                    layoutProgression.addView(workoutView);
                    svProgression.fullScroll(View.FOCUS_DOWN);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("WorkoutsActivity", "Error loading workouts", e);
            }
        });
    }

    private void openProgressionDetailsDialog(Workout workout) {
        final Dialog dialog = new Dialog(ProgressionActivity.this);
        dialog.setContentView(R.layout.progression_dialog);

        TextView workoutNameTextView = dialog.findViewById(R.id.dialog_workout_name);
        TextView firstExerciseTextView = dialog.findViewById(R.id.dialog_first_exercise);
        TextView firstExerciseStartingWeightsTextView = dialog.findViewById(R.id.dialog_weights_label1);
        TextView firstExerciseCurrentWeightsTextView = dialog.findViewById(R.id.dialog_weights_label1_1);

        TextView secondExerciseTextView = dialog.findViewById(R.id.dialog_second_exercise);
        TextView secondExerciseStartingWeightsTextView = dialog.findViewById(R.id.dialog_weights_label2);
        TextView secondExerciseCurrentWeightsTextView = dialog.findViewById(R.id.dialog_weights_label2_2);

        TextView thirdExerciseTextView = dialog.findViewById(R.id.dialog_third_exercise);
        TextView thirdExerciseStartingWeightsTextView = dialog.findViewById(R.id.dialog_weights_label3);
        TextView thirdExerciseCurrentWeightsTextView = dialog.findViewById(R.id.dialog_weights_label3_3);

        TextView fourthExerciseTextView = dialog.findViewById(R.id.dialog_fourth_exercise);
        TextView fourthExerciseStartingWeightsTextView = dialog.findViewById(R.id.dialog_weights_label4);
        TextView fourthExerciseCurrentWeightsTextView = dialog.findViewById(R.id.dialog_weights_label4_4);

        TextView fifthExerciseTextView = dialog.findViewById(R.id.dialog_fifth_exercise);
        TextView fifthExerciseStartingWeightsTextView = dialog.findViewById(R.id.dialog_weights_label5);
        TextView fifthExerciseCurrentWeightsTextView = dialog.findViewById(R.id.dialog_weights_label5_5);

        String firstExercise = "First Exercise: " + workout.getFirstExercise();
        String secondExercise = "Second Exercise: " + workout.getSecondExercise();
        String thirdExercise = "Third Exercise: " + workout.getThirdExercise();
        String fourthExercise = "Fourth Exercise: " + workout.getFourthExercise();
        String fifthExercise = "Fifth Exercise: " + workout.getFifthExercise();

        workoutNameTextView.setText(workout.getWorkoutName());
        firstExerciseTextView.setText(firstExercise);
        secondExerciseTextView.setText(secondExercise);
        thirdExerciseTextView.setText(thirdExercise);
        fourthExerciseTextView.setText(fourthExercise);
        fifthExerciseTextView.setText(fifthExercise);

        String exerciseName = workout.getFirstExercise();

        if (exerciseName != null) {
            progressionExercisesCollection = db.collection("exercises");
            progressionExercisesCollection.whereEqualTo("name", exerciseName)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            List<DocumentSnapshot> exerciseSnapshots = queryDocumentSnapshots.getDocuments();

                            if (!exerciseSnapshots.isEmpty()) {
                                DocumentSnapshot firstExerciseSnapshot = exerciseSnapshots.get(0);
                                String firstExerciseWeights = firstExerciseSnapshot.getString("weights");

                                if (firstExerciseWeights != null) {
                                    firstExerciseStartingWeightsTextView.setText("Starting Weights: " + firstExerciseWeights);
                                } else {
                                    firstExerciseStartingWeightsTextView.setText("Starting Weights: N/A");
                                }
                            } else {
                                firstExerciseStartingWeightsTextView.setText("Starting Weights: N/A");
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("ProgressionActivity", "Error retrieving exercise weights", e);
                        }
                    });
        } else {
            Log.e("ProgressionActivity", "Invalid workout name");
        }

        Button btnOk = dialog.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}