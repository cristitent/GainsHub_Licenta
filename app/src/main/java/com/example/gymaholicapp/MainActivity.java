package com.example.gymaholicapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
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

    public boolean onNavigationItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.menuHome) {
            startActivity(new Intent(MainActivity.this, MainActivity.class));
        } else if (itemId == R.id.menuExercises) {
            startActivity(new Intent(MainActivity.this, ExercisesActivity.class));
        } else if (itemId == R.id.menuWorkouts) {
            startActivity(new Intent(MainActivity.this, WorkoutsActivity.class));
        } else if (itemId == R.id.menuProgression) {
            startActivity(new Intent(MainActivity.this, ProgressionActivity.class));
        } else if (itemId == R.id.menuHistory) {
            startActivity(new Intent(MainActivity.this, HistoryActivity.class));
        } else if (itemId == R.id.menuLogOut) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
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