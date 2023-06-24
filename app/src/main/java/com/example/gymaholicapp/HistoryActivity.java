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
import android.widget.ImageView;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class HistoryActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private LinearLayout layoutHistoryList;
    private FirebaseFirestore db;
    private LinearLayout layoutHistory;
    private ScrollView svHistory;
    private CollectionReference historyCollection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        layoutHistoryList = findViewById(R.id.layoutHistory);
        db = FirebaseFirestore.getInstance();
        layoutHistory = findViewById(R.id.layoutHistory);
        svHistory = findViewById(R.id.sv_history);


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

        loadWorkoutHistoryFromDatabase();
    }

    private void loadWorkoutHistoryFromDatabase() {
        layoutHistory.removeAllViews();

        historyCollection = db.collection("workouts");
        historyCollection.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
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

                            View workoutView = LayoutInflater.from(HistoryActivity.this).inflate(R.layout.list_history, null);
                            TextView workoutNameTextView = workoutView.findViewById(R.id.textWorkoutNameH);
                            workoutNameTextView.setText(workoutName);

                            workoutNameTextView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    openHistoryDetailsDialog(workout);
                                }
                            });

                            layoutHistory.addView(workoutView);
                            svHistory.fullScroll(View.FOCUS_DOWN);
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("History Activity", "Error loading workouts", e);
            }
        });
    }

    private void openHistoryDetailsDialog(Workout workout) {
        final Dialog dialog = new Dialog(HistoryActivity.this);
        dialog.setContentView(R.layout.history_dialog);

        TextView workoutNameTextView = dialog.findViewById(R.id.dialog_workout_name);

        TextView historyDialogTextView = dialog.findViewById(R.id.dialog_history_text);

        workoutNameTextView.setText(workout.getWorkoutName());

        List<ExerciseData> exerciseDataList = workout.getExerciseDataList();

        int maxExerciseIndex = getMaxExerciseIndex(exerciseDataList);

        if(exerciseDataList != null) {
            for (ExerciseData data : exerciseDataList) {
                if (data.getExerciseIndex() == maxExerciseIndex) {
                    String historyDialog = "Congratulations!!! You did this workout " + data.getExerciseIndex() + " times.";
                    historyDialogTextView.setText(historyDialog);
                    break;
                } else {
                    String historyDialog = "Congratulations!!! You did this workout 0 times.";
                    historyDialogTextView.setText(historyDialog);
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
            startActivity(new Intent(HistoryActivity.this, MainActivity.class));
        } else if (itemId == R.id.menuExercises) {
            startActivity(new Intent(HistoryActivity.this, ExercisesActivity.class));
        } else if (itemId == R.id.menuWorkouts) {
            startActivity(new Intent(HistoryActivity.this, WorkoutsActivity.class));
        } else if (itemId == R.id.menuProgression) {
            startActivity(new Intent(HistoryActivity.this, ProgressionActivity.class));
        } else if (itemId == R.id.menuHistory) {
            startActivity(new Intent(HistoryActivity.this, HistoryActivity.class));
        } else if (itemId == R.id.menuLogOut) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(HistoryActivity.this, LoginActivity.class);
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