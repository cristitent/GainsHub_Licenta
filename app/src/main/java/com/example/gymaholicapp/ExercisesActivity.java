package com.example.gymaholicapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ExercisesActivity extends AppCompatActivity {

    ScrollView sv_exercises;
    EditText et_exercises;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercises);

        et_exercises = findViewById(R.id.et_exercises);
        sv_exercises = findViewById(R.id.sv_exercises);

        et_exercises.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            URL url = new URL("https://api.api-ninjas.com/v1/exercises?muscle=biceps");
                            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                            connection.setRequestProperty("accept", "application/json");
                            connection.setRequestProperty("X-API-Key", "xf0aeGx7IEmtEwf4eZ0hng==tSpzadwz07ocJL3C");
                            int responseCode = connection.getResponseCode();

                            if (responseCode == HttpURLConnection.HTTP_OK) {
                                InputStream responseStream = connection.getInputStream();
                                ObjectMapper mapper = new ObjectMapper();
                                JsonNode root = mapper.readTree(responseStream);

                                // Process the response here
                                final String response = root.toString();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(ExercisesActivity.this, response, Toast.LENGTH_SHORT).show();
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