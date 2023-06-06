package com.example.gymaholicapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class WorkoutsActivity extends AppCompatActivity {
    private Button btnAddWorkout;
    private FirebaseDatabase db;
    private ScrollView svWorkouts;
    private LinearLayout layoutWorkouts;
    private DatabaseReference workoutsRef;
    private ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workouts);

        db = FirebaseDatabase.getInstance();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        btnAddWorkout = findViewById(R.id.btnAddWorkout);
        svWorkouts = findViewById(R.id.sv_workouts);
        layoutWorkouts = findViewById(R.id.layoutWorkouts);

        workoutsRef = db.getReference("workouts");

        btnAddWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWorkoutDialog();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadWorkoutsFromDatabase();
    }

    private void loadWorkoutsFromDatabase() {
        // Clear the layout before adding workouts
        layoutWorkouts.removeAllViews();

        // Remove the valueEventListener if it exists
        if (valueEventListener != null) {
            workoutsRef.removeEventListener(valueEventListener);
        }

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Clear the layout before adding workouts
                layoutWorkouts.removeAllViews();

                for (DataSnapshot workoutSnapshot : snapshot.getChildren()) {
                    String workoutId = workoutSnapshot.getKey();
                    String workoutName = workoutSnapshot.child("workoutName").getValue(String.class);

                    View workoutView = LayoutInflater.from(WorkoutsActivity.this).inflate(R.layout.list_workout, null);
                    TextView workoutNameTextView = workoutView.findViewById(R.id.textWorkoutName);
                    workoutNameTextView.setText(workoutName);

                    ImageView deleteImageView = workoutView.findViewById(R.id.deleteImageView);
                    deleteImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            workoutsRef.child(workoutId).removeValue();
                            layoutWorkouts.removeView(workoutView);
                        }
                    });

                    layoutWorkouts.addView(workoutView);
                    svWorkouts.fullScroll(View.FOCUS_DOWN);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled event
            }
        };

        workoutsRef.addValueEventListener(valueEventListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (valueEventListener != null) {
            workoutsRef.removeEventListener(valueEventListener);
        }
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

                Query query = workoutsRef.orderByChild("workoutName").equalTo(workout.getWorkoutName());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Workout already exists, display a message or handle the duplication case
                            Toast.makeText(WorkoutsActivity.this, "Workout already exists", Toast.LENGTH_SHORT).show();
                        } else {
                            // Workout doesn't exist, add a new entry
                            DatabaseReference newWorkoutRef = workoutsRef.push();
                            newWorkoutRef.setValue(workout)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(WorkoutsActivity.this, "Workout created successfully", Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(WorkoutsActivity.this, "Failed to create workout", Toast.LENGTH_SHORT).show();
                                            Log.e("Realtime Database", "Error creating workout", e);
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle the error
                    }
                });
            }
        });

        dialog.show();
    }

}