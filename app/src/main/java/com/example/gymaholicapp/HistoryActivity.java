package com.example.gymaholicapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class HistoryActivity extends AppCompatActivity {
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
}