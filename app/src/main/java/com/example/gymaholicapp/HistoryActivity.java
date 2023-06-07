package com.example.gymaholicapp;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoryActivity extends AppCompatActivity {

    private LinearLayout layoutHistoryList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        layoutHistoryList = findViewById(R.id.llHistory);
        db = FirebaseFirestore.getInstance();

        db.collection("exercises")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Map<Integer, List<String>> exerciseWorkouts = new HashMap<>();

                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            long exerciseIndex = documentSnapshot.getLong("exerciseIndex");
                            String workoutName = documentSnapshot.getString("workoutName");

                            List<String> workoutNames = exerciseWorkouts.get((int) exerciseIndex);
                            if (workoutNames == null) {
                                workoutNames = new ArrayList<>();
                                exerciseWorkouts.put((int) exerciseIndex, workoutNames);
                            }

                            workoutNames.add(workoutName);
                        }

                        displayWorkoutNames(exerciseWorkouts);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.e("Firestore", "Error fetching exercise data: " + e.getMessage());
                    }
                });
    }

    private void displayWorkoutNames(Map<Integer, List<String>> exerciseWorkouts) {
        for (Map.Entry<Integer, List<String>> entry : exerciseWorkouts.entrySet()) {
            int exerciseIndex = entry.getKey();
            List<String> workoutNames = entry.getValue();

            TextView exerciseIndexTextView = new TextView(this);
            exerciseIndexTextView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            exerciseIndexTextView.setText("Exercise Index: " + exerciseIndex);
            exerciseIndexTextView.setTextSize(24);
            exerciseIndexTextView.setTypeface(null, Typeface.BOLD);
            layoutHistoryList.addView(exerciseIndexTextView);

            for (String workoutName : workoutNames) {
                TextView workoutNameTextView = new TextView(this);
                workoutNameTextView.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                ));
                workoutNameTextView.setText(workoutName);
                workoutNameTextView.setTextSize(18);
                layoutHistoryList.addView(workoutNameTextView);
            }
        }
    }
}