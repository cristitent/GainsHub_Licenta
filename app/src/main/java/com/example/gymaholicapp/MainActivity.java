package com.example.gymaholicapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button buttonExercises;
    private Button buttonWorkouts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonExercises = (Button) findViewById(R.id.button6);
        buttonExercises.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openExercisesActivity();
            }
        });

        buttonWorkouts = (Button) findViewById(R.id.button7);
        buttonWorkouts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWorkoutsActivity();
            }
        });
    }
    public void openExercisesActivity() {
        Intent intent = new Intent(this, ExercisesActivity.class);
        startActivity(intent);
    }

    public void openWorkoutsActivity() {
        Intent intent = new Intent(this, WorkoutsActivity.class);
        startActivity(intent);
    }
}