package com.example.gymaholicapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkoutsActivity extends AppCompatActivity {
    private Button btnAddWorkout;
    private FirebaseFirestore db;
    private ScrollView svWorkouts;
    private LinearLayout layoutWorkouts;
    private CollectionReference workoutsCollection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workouts);

        db = FirebaseFirestore.getInstance();

        btnAddWorkout = findViewById(R.id.btnAddWorkout);
        svWorkouts = findViewById(R.id.sv_workouts);
        layoutWorkouts = findViewById(R.id.layoutWorkouts);

        db = FirebaseFirestore.getInstance();
        workoutsCollection = db.collection("workouts");

        btnAddWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWorkoutDialog();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadWorkoutsFromDatabase();
    }

    private void loadWorkoutsFromDatabase() {
        layoutWorkouts.removeAllViews();

        workoutsCollection.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
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

                    View workoutView = LayoutInflater.from(WorkoutsActivity.this).inflate(R.layout.list_workout, null);
                    TextView workoutNameTextView = workoutView.findViewById(R.id.textWorkoutName);
                    workoutNameTextView.setText(workoutName);

                    workoutNameTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openWorkoutDetailsDialog(workout);
                        }
                    });

                    ImageView deleteImageView = workoutView.findViewById(R.id.deleteImageView);
                    deleteImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            deleteWorkout(workoutId);
                            layoutWorkouts.removeView(workoutView);
                        }
                    });

                    ImageView addImageView = workoutView.findViewById(R.id.addImageView);
                    addImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openWorkoutInstanceDialog(workout);
                        }
                    });

                    layoutWorkouts.addView(workoutView);
                    svWorkouts.fullScroll(View.FOCUS_DOWN);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("WorkoutsActivity", "Error loading workouts", e);
            }
        });
    }

    private void deleteWorkout(String workoutId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        WriteBatch batch = db.batch();

        DocumentReference workoutRef = db.collection("workouts").document(workoutId);
        batch.delete(workoutRef);

        CollectionReference exerciseCollection = workoutRef.collection("exercises");
        exerciseCollection.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                List<DocumentSnapshot> exerciseDocuments = querySnapshot.getDocuments();
                for (DocumentSnapshot exerciseDocument : exerciseDocuments) {
                    batch.delete(exerciseDocument.getReference());
                }

                batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(WorkoutsActivity.this, "Workout and associated exercises deleted successfully", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(WorkoutsActivity.this, "Failed to delete workout and associated exercises", Toast.LENGTH_SHORT).show();
                        Log.e("Firestore", "Error deleting workout and associated exercises", e);
                    }
                });
            }
        });
    }

    private void openWorkoutDialog() {
        final Dialog dialog = new Dialog(WorkoutsActivity.this);
        dialog.setContentView(R.layout.workout_dialog);

        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        Button btnCreate = dialog.findViewById(R.id.btnCreate);
        EditText etWorkoutName = dialog.findViewById(R.id.workout_name);
        EditText etFirstExercise = dialog.findViewById(R.id.first_exercise);
        EditText etSecondExercise = dialog.findViewById(R.id.second_exercise);
        EditText etThirdExercise = dialog.findViewById(R.id.third_exercise);
        EditText etFourthExercise = dialog.findViewById(R.id.fourth_exercise);
        EditText etFifthExercise = dialog.findViewById(R.id.fifth_exercise);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String workoutName = etWorkoutName.getText().toString().trim();
                String firstExercise = etFirstExercise.getText().toString().trim();
                String secondExercise = etSecondExercise.getText().toString().trim();
                String thirdExercise = etThirdExercise.getText().toString().trim();
                String fourthExercise = etFourthExercise.getText().toString().trim();
                String fifthExercise = etFifthExercise.getText().toString().trim();

                Workout workout = new Workout(workoutName, firstExercise, secondExercise, thirdExercise, fourthExercise, fifthExercise);

                CollectionReference workoutsCollection = FirebaseFirestore.getInstance().collection("workouts");

                Query query = workoutsCollection.whereEqualTo("workoutName", workout.getWorkoutName());
                query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            Toast.makeText(WorkoutsActivity.this, "Workout already exists", Toast.LENGTH_SHORT).show();
                        } else {
                            workoutsCollection.add(workout)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Toast.makeText(WorkoutsActivity.this, "Workout created successfully", Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                            loadWorkoutsFromDatabase();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(WorkoutsActivity.this, "Failed to create workout", Toast.LENGTH_SHORT).show();
                                            Log.e("Firestore", "Error creating workout", e);
                                        }
                                    });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
            }
        });

        dialog.show();
    }

    private void openWorkoutDetailsDialog(Workout workout) {
        final Dialog dialog = new Dialog(WorkoutsActivity.this);
        dialog.setContentView(R.layout.workout_details_dialog);

        TextView workoutNameTextView = dialog.findViewById(R.id.dialog_workout_name);
        TextView firstExerciseTextView = dialog.findViewById(R.id.dialog_first_exercise);
        TextView secondExerciseTextView = dialog.findViewById(R.id.dialog_second_exercise);
        TextView thirdExerciseTextView = dialog.findViewById(R.id.dialog_third_exercise);
        TextView fourthExerciseTextView = dialog.findViewById(R.id.dialog_fourth_exercise);
        TextView fifthExerciseTextView = dialog.findViewById(R.id.dialog_fifth_exercise);
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

        Button btnOk = dialog.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private int entryCount = 0;
    private void openWorkoutInstanceDialog(Workout workout) {
        final Dialog dialog = new Dialog(WorkoutsActivity.this);
        dialog.setContentView(R.layout.workout_instance_dialog);

        TextView workoutNameTextView = dialog.findViewById(R.id.dialog_workout_name);
        TextView firstExerciseTextView = dialog.findViewById(R.id.dialog_first_exercise);
        TextView secondExerciseTextView = dialog.findViewById(R.id.dialog_second_exercise);
        TextView thirdExerciseTextView = dialog.findViewById(R.id.dialog_third_exercise);
        TextView fourthExerciseTextView = dialog.findViewById(R.id.dialog_fourth_exercise);
        TextView fifthExerciseTextView = dialog.findViewById(R.id.dialog_fifth_exercise);

        workoutNameTextView.setText(workout.getWorkoutName());
        firstExerciseTextView.setText(workout.getFirstExercise());
        secondExerciseTextView.setText(workout.getSecondExercise());
        thirdExerciseTextView.setText(workout.getThirdExercise());
        fourthExerciseTextView.setText(workout.getFourthExercise());
        fifthExerciseTextView.setText(workout.getFifthExercise());

        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Button btnOk = dialog.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                db.collection("workouts")
                        .whereEqualTo("workoutName", workout.getWorkoutName())
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                if (!queryDocumentSnapshots.isEmpty()) {
                                    DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                                    String workoutId = documentSnapshot.getId();

                                    workout.setWorkoutId(workoutId);

                                    String[] exerciseNames = {
                                            documentSnapshot.getString("firstExercise"),
                                            documentSnapshot.getString("secondExercise"),
                                            documentSnapshot.getString("thirdExercise"),
                                            documentSnapshot.getString("fourthExercise"),
                                            documentSnapshot.getString("fifthExercise")
                                    };
                                    EditText[] setsEditTexts = {
                                            dialog.findViewById(R.id.dialog_sets_value1),
                                            dialog.findViewById(R.id.dialog_sets_value2),
                                            dialog.findViewById(R.id.dialog_sets_value3),
                                            dialog.findViewById(R.id.dialog_sets_value4),
                                            dialog.findViewById(R.id.dialog_sets_value5)
                                    };
                                    EditText[] repsEditTexts = {
                                            dialog.findViewById(R.id.dialog_reps_value1),
                                            dialog.findViewById(R.id.dialog_reps_value2),
                                            dialog.findViewById(R.id.dialog_reps_value3),
                                            dialog.findViewById(R.id.dialog_reps_value4),
                                            dialog.findViewById(R.id.dialog_reps_value5)
                                    };
                                    EditText[] weightsEditTexts = {
                                            dialog.findViewById(R.id.dialog_weights_value1),
                                            dialog.findViewById(R.id.dialog_weights_value2),
                                            dialog.findViewById(R.id.dialog_weights_value3),
                                            dialog.findViewById(R.id.dialog_weights_value4),
                                            dialog.findViewById(R.id.dialog_weights_value5)
                                    };

                                    entryCount++;

                                    for (int i = 0; i < exerciseNames.length; i++) {
                                        addExerciseCollection(db, workout, entryCount, exerciseNames[i], setsEditTexts[i], repsEditTexts[i], weightsEditTexts[i]);
                                    }

                                    Date currentDate = new Date();

                                    Map<String, Object> workoutInstanceData = new HashMap<>();
                                    workoutInstanceData.put("workoutId", workoutId);
                                    workoutInstanceData.put("date", currentDate);

                                } else {
                                    Log.e("WorkoutsActivity", "Workout document does not exist in Firestore.");
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("WorkoutsActivity", "Error retrieving workout document: " + e.getMessage());
                            }
                        });

                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void addExerciseCollection(FirebaseFirestore db, Workout workout, int exerciseIndex, String exerciseName,
                                       EditText setsEditText, EditText repsEditText, EditText weightsEditText) {
        int sets = Integer.parseInt(setsEditText.getText().toString());
        int reps = Integer.parseInt(repsEditText.getText().toString());
        double weights = Double.parseDouble(weightsEditText.getText().toString());

        ExerciseData exerciseData = new ExerciseData(exerciseIndex, exerciseName, sets, reps, weights);

        CollectionReference exerciseCollection = db.collection("exercises");

        exerciseCollection.add(exerciseData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }
}