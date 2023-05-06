package com.example.gymaholicapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private Button buttonExercises;
    private Button buttonWorkouts;
    private Button buttonProgression;
    private Button buttonHistory;
    FirebaseAuth auth;
    private Button buttonLogout;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        buttonLogout = findViewById(R.id.buttonLogout);
        user = auth.getCurrentUser();
        if(user == null){
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }

        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

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

        buttonProgression = (Button) findViewById(R.id.button8);
        buttonProgression.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProgressionActivity();
            }
        });

        buttonHistory = (Button) findViewById(R.id.button9);
        buttonHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHistoryActivity();
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

    public void openProgressionActivity() {
        Intent intent = new Intent(this, ProgressionActivity.class);
        startActivity(intent);
    }

    public void openHistoryActivity() {
        Intent intent = new Intent(this, HistoryActivity.class);
        startActivity(intent);
    }
}