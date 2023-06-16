package com.example.gymaholicapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class ProgressionActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private LinearLayout layoutProgressionList;
    private FirebaseFirestore db;
    private LinearLayout layoutProgression;
    private ScrollView svProgression;
    private CollectionReference progressionCollection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progression);

        layoutProgressionList = findViewById(R.id.layoutProgresion);
        db = FirebaseFirestore.getInstance();
        layoutProgression = findViewById(R.id.layoutProgresion);
        svProgression = findViewById(R.id.sv_porgresion);


        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);

        findViewById(R.id.imageMenu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);

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

                    // Retrieve exercise data from "exercises" collection
                    CollectionReference exerciseCollection = db.collection("exercises");
                    exerciseCollection.whereEqualTo("workoutName", workoutName).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot exerciseSnapshot) {
                            for (DocumentSnapshot exerciseDocument : exerciseSnapshot) {
                                String exerciseName = exerciseDocument.getString("exerciseName");
                                int exerciseIndex = exerciseDocument.getLong("exerciseIndex").intValue();
                                int sets = exerciseDocument.getLong("sets").intValue();
                                int reps = exerciseDocument.getLong("reps").intValue();
                                double weights = exerciseDocument.getDouble("weights");

                                ExerciseData exerciseData = new ExerciseData(exerciseIndex, exerciseName, sets, reps, weights, workoutName);
                                workout.addExerciseData(exerciseData);
                            }

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
                    });
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

        List<ExerciseData> exerciseDataList = workout.getExerciseDataList();

        int maxExerciseIndex = getMaxExerciseIndex(exerciseDataList);

        if(exerciseDataList != null) {
            for (ExerciseData data : exerciseDataList) {
                if (data.getExerciseName().equals(workout.getFirstExercise()) && data.getExerciseIndex() == 1) {
                    String firstExerciseWeight = "Starting Weight: " + data.getWeights();
                    firstExerciseStartingWeightsTextView.setText(firstExerciseWeight);
                    firstExerciseStartingWeightsTextView.setVisibility(View.VISIBLE); // Make the text view visible
                    break; // Exit the loop since we found the matching exercise
                } else {
                    firstExerciseStartingWeightsTextView.setVisibility(View.GONE); // Hide the text view
                }
            }
        }

        if(exerciseDataList != null) {
            for (ExerciseData data : exerciseDataList) {
                if (data.getExerciseName().equals(workout.getFirstExercise()) && data.getExerciseIndex() == maxExerciseIndex) {
                    String firstExerciseWeight = "Starting Weight: " + data.getWeights();
                    firstExerciseCurrentWeightsTextView.setText(firstExerciseWeight);
                    firstExerciseCurrentWeightsTextView.setVisibility(View.VISIBLE); // Make the text view visible
                    break; // Exit the loop since we found the matching exercise
                } else {
                    firstExerciseCurrentWeightsTextView.setVisibility(View.GONE); // Hide the text view
                }
            }
        }

        if(exerciseDataList != null) {
            for (ExerciseData data : exerciseDataList) {
                if (data.getExerciseName().equals(workout.getSecondExercise()) && data.getExerciseIndex() == 1) {
                    String secondExerciseWeight = "Starting Weight: " + data.getWeights();
                    secondExerciseStartingWeightsTextView.setText(secondExerciseWeight);
                    secondExerciseStartingWeightsTextView.setVisibility(View.VISIBLE); // Make the text view visible
                    break; // Exit the loop since we found the matching exercise
                } else {
                    secondExerciseStartingWeightsTextView.setVisibility(View.GONE); // Hide the text view
                }
            }
        }

        if(exerciseDataList != null) {
            for (ExerciseData data : exerciseDataList) {
                if (data.getExerciseName().equals(workout.getSecondExercise()) && data.getExerciseIndex() == maxExerciseIndex) {
                    String secondExerciseWeight = "Starting Weight: " + data.getWeights();
                    secondExerciseCurrentWeightsTextView.setText(secondExerciseWeight);
                    secondExerciseCurrentWeightsTextView.setVisibility(View.VISIBLE); // Make the text view visible
                    break; // Exit the loop since we found the matching exercise
                } else {
                    secondExerciseCurrentWeightsTextView.setVisibility(View.GONE); // Hide the text view
                }
            }
        }


        if(exerciseDataList != null) {
            for (ExerciseData data : exerciseDataList) {
                if (data.getExerciseName().equals(workout.getThirdExercise()) && data.getExerciseIndex() == 1) {
                    String thirdExerciseWeight = "Starting Weight: " + data.getWeights();
                    thirdExerciseStartingWeightsTextView.setText(thirdExerciseWeight);
                    thirdExerciseStartingWeightsTextView.setVisibility(View.VISIBLE); // Make the text view visible
                    break; // Exit the loop since we found the matching exercise
                } else {
                    thirdExerciseStartingWeightsTextView.setVisibility(View.GONE); // Hide the text view
                }
            }
        }

        if(exerciseDataList != null) {
            for (ExerciseData data : exerciseDataList) {
                if (data.getExerciseName().equals(workout.getThirdExercise()) && data.getExerciseIndex() == maxExerciseIndex) {
                    String thirdExerciseWeight = "Starting Weight: " + data.getWeights();
                    thirdExerciseCurrentWeightsTextView.setText(thirdExerciseWeight);
                    thirdExerciseCurrentWeightsTextView.setVisibility(View.VISIBLE); // Make the text view visible
                    break; // Exit the loop since we found the matching exercise
                } else {
                    thirdExerciseCurrentWeightsTextView.setVisibility(View.GONE); // Hide the text view
                }
            }
        }

        if(exerciseDataList != null) {
            for (ExerciseData data : exerciseDataList) {
                if (data.getExerciseName().equals(workout.getFourthExercise()) && data.getExerciseIndex() == 1) {
                    String fourthExerciseWeight = "Starting Weight: " + data.getWeights();
                    fourthExerciseStartingWeightsTextView.setText(fourthExerciseWeight);
                    fourthExerciseStartingWeightsTextView.setVisibility(View.VISIBLE); // Make the text view visible
                    break; // Exit the loop since we found the matching exercise
                } else {
                    fourthExerciseStartingWeightsTextView.setVisibility(View.GONE); // Hide the text view
                }
            }
        }

        if(exerciseDataList != null) {
            for (ExerciseData data : exerciseDataList) {
                if (data.getExerciseName().equals(workout.getFourthExercise()) && data.getExerciseIndex() == maxExerciseIndex) {
                    String fourthExerciseWeight = "Starting Weight: " + data.getWeights();
                    fourthExerciseCurrentWeightsTextView.setText(fourthExerciseWeight);
                    fourthExerciseCurrentWeightsTextView.setVisibility(View.VISIBLE); // Make the text view visible
                    break; // Exit the loop since we found the matching exercise
                } else {
                    fourthExerciseCurrentWeightsTextView.setVisibility(View.GONE); // Hide the text view
                }
            }
        }

        if(exerciseDataList != null) {
            for (ExerciseData data : exerciseDataList) {
                if (data.getExerciseName().equals(workout.getFifthExercise()) && data.getExerciseIndex() == 1) {
                    String fifthExerciseWeight = "Starting Weight: " + data.getWeights();
                    fifthExerciseStartingWeightsTextView.setText(fifthExerciseWeight);
                    fifthExerciseStartingWeightsTextView.setVisibility(View.VISIBLE); // Make the text view visible
                    break; // Exit the loop since we found the matching exercise
                } else {
                    fifthExerciseStartingWeightsTextView.setVisibility(View.GONE); // Hide the text view
                }
            }
        }

        if(exerciseDataList != null) {
            for (ExerciseData data : exerciseDataList) {
                if (data.getExerciseName().equals(workout.getFifthExercise()) && data.getExerciseIndex() == maxExerciseIndex) {
                    String fifthExerciseWeight = "Starting Weight: " + data.getWeights();
                    fifthExerciseCurrentWeightsTextView.setText(fifthExerciseWeight);
                    fifthExerciseCurrentWeightsTextView.setVisibility(View.VISIBLE); // Make the text view visible
                    break; // Exit the loop since we found the matching exercise
                } else {
                    fifthExerciseCurrentWeightsTextView.setVisibility(View.GONE); // Hide the text view
                }
            }
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


    public boolean onNavigationItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.menuHome) {
            startActivity(new Intent(ProgressionActivity.this, MainActivity.class));
        } else if (itemId == R.id.menuExercises) {
            startActivity(new Intent(ProgressionActivity.this, ExercisesActivity.class));
        } else if (itemId == R.id.menuWorkouts) {
            startActivity(new Intent(ProgressionActivity.this, WorkoutsActivity.class));
        } else if (itemId == R.id.menuProgression) {
            startActivity(new Intent(ProgressionActivity.this, ProgressionActivity.class));
        } else if (itemId == R.id.menuHistory) {
            startActivity(new Intent(ProgressionActivity.this, HistoryActivity.class));
        } else if (itemId == R.id.menuLogOut) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(ProgressionActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private int getMaxExerciseIndex(List<ExerciseData> exerciseDataList) {
        int maxIndex = 0;
        if (exerciseDataList != null) {
            for (ExerciseData data : exerciseDataList) {
                int exerciseIndex = data.getExerciseIndex();
                if (exerciseIndex > maxIndex) {
                    maxIndex = exerciseIndex;
                }
            }
        }
        return maxIndex;
    }

}