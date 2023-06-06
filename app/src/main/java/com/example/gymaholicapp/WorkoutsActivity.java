package com.example.gymaholicapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class WorkoutsActivity extends AppCompatActivity {
    private Button btnAddWorkout;
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workouts);

        db = FirebaseFirestore.getInstance();

        btnAddWorkout = (Button) findViewById(R.id.btnAddWorkout);
        btnAddWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWorkoutDialog();
            }
        });
    }

    private void openWorkoutDialog() {
        final Dialog dialog = new Dialog(WorkoutsActivity.this);
        dialog.setContentView(R.layout.workout_dialog);

        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        Button btnCreate = dialog.findViewById(R.id.btnCreate);
        EditText etWorkoutName = dialog.findViewById(R.id.workout_name);
        EditText etFirstExercise = dialog.findViewById(R.id.first_exercise);
        EditText etSecondExercise = dialog.findViewById(R.id.second_exercise);
        EditText etThirdExercise = dialog.findViewById(R.id.third_exercise);
        EditText etFourthExercise = dialog.findViewById(R.id.fourth_exercise);
        EditText etFifthExercise = dialog.findViewById(R.id.fifth_exercise);


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String workoutName = etWorkoutName.getText().toString().trim();
                String firstExercise = etFirstExercise.getText().toString().trim();
                String secondExercise = etSecondExercise.getText().toString().trim();
                String thirdExercise = etThirdExercise.getText().toString().trim();
                String fourthExercise = etFourthExercise.getText().toString().trim();
                String fifthExercise = etFifthExercise.getText().toString().trim();

                Workout workout = new Workout(workoutName, firstExercise, secondExercise, thirdExercise, fourthExercise, fifthExercise);

                CollectionReference workoutsCollection = db.collection("workouts");
                workoutsCollection.add(workout)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(WorkoutsActivity.this, "Workout created successfully", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(WorkoutsActivity.this, "Failed to create workout", Toast.LENGTH_SHORT).show();
                                Log.e("Firestore", "Error creating workout", e);
                            }
                        });
            }
        });

        dialog.show();
    }

}