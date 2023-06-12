package com.example.gymaholicapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
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
                    List<DocumentSnapshot> exerciseDocuments = workoutSnapshot.get("exercises", List.class);

                    Workout workout = new Workout();
                    workout.setWorkoutId(workoutId);
                    workout.setWorkoutName(workoutName);

                    if (exerciseDocuments != null) {
                        for (DocumentSnapshot exerciseSnapshot : exerciseDocuments) {
                            int exerciseIndex = exerciseSnapshot.getLong("exerciseIndex").intValue();
                            String exerciseName = exerciseSnapshot.getString("exerciseName");
                            int sets = exerciseSnapshot.getLong("sets").intValue();
                            int reps = exerciseSnapshot.getLong("reps").intValue();
                            double weights = exerciseSnapshot.getDouble("weights");

                            workout.addExerciseEntry(exerciseIndex, exerciseName, sets, reps, weights);
                        }
                    }

                    View workoutView = LayoutInflater.from(HistoryActivity.this).inflate(R.layout.list_history, null);
                    TextView workoutNameTextView = workoutView.findViewById(R.id.textWorkoutNameH);
                    workoutNameTextView.setText(workoutName);

                    layoutHistory.addView(workoutView);
                    svHistory.fullScroll(View.FOCUS_DOWN);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("WorkoutsActivity", "Error loading workouts", e);
            }
        });
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

}