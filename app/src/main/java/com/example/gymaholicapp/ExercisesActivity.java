package com.example.gymaholicapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ExercisesActivity extends AppCompatActivity {

    ScrollView sv_exercises;
    LinearLayout ll_exercises;
    EditText et_exercises;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercises);

        et_exercises = findViewById(R.id.et_exercises);
        sv_exercises = findViewById(R.id.sv_exercises);
        ll_exercises = findViewById(R.id.ll_exercises);

        et_exercises.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            URL url = new URL("https://api.api-ninjas.com/v1/exercises?muscle=" + et_exercises.getText().toString());
                            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                            connection.setRequestProperty("accept", "application/json");
                            connection.setRequestProperty("X-API-Key", "xf0aeGx7IEmtEwf4eZ0hng==tSpzadwz07ocJL3C");
                            int responseCode = connection.getResponseCode();

                            if (responseCode == HttpURLConnection.HTTP_OK) {
                                InputStream responseStream = connection.getInputStream();
                                ObjectMapper mapper = new ObjectMapper();
                                JsonNode root = mapper.readTree(responseStream);

                                final List<Exercise> exerciseList = new ArrayList<>();

                                for (JsonNode node : root) {
                                    String name = node.get("name").asText();
                                    String type = node.get("type").asText();
                                    String muscle = node.get("muscle").asText();
                                    String equipment = node.get("equipment").asText();
                                    String difficulty = node.get("difficulty").asText();
                                    String instructions = node.get("instructions").asText();

                                    Exercise exercise = new Exercise(name, type, muscle, equipment, difficulty, instructions);
                                    exerciseList.add(exercise);
                                }

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Clear previous views
                                        ll_exercises.removeAllViews();

                                        // Create and add views for each exercise item
                                        for (Exercise exercise : exerciseList) {
                                            View itemView = getLayoutInflater().inflate(R.layout.list_exercise, ll_exercises, false);

                                            TextView nameTextView = itemView.findViewById(R.id.textExerciseName);
                                            TextView typeTextView = itemView.findViewById(R.id.textExerciseType);
                                            TextView muscleTextView = itemView.findViewById(R.id.textExerciseMuscle);
                                            TextView equipmentTextView = itemView.findViewById(R.id.textExerciseEquipment);

                                            nameTextView.setText(exercise.getName());
                                            typeTextView.setText(exercise.getType());
                                            muscleTextView.setText(exercise.getMuscle());
                                            equipmentTextView.setText(exercise.getEquipment());

                                            ll_exercises.addView(itemView);

                                            itemView.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Intent intent = new Intent(getApplicationContext(), WorkoutsActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            });
                                        }
                                    }
                                });
                            } else {
                                // Handle error response
                                final String errorResponse = "HTTP Error: " + responseCode;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(ExercisesActivity.this, errorResponse, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            final String errorMessage = "Error: " + e.getMessage();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ExercisesActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
            }
        });
    }
}
