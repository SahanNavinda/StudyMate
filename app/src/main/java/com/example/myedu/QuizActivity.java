package com.example.myedu;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;

public class QuizActivity extends AppCompatActivity {

    private TextView tvQuestion, tvScore;
    private RadioGroup radioGroup;
    private Button btnNext, btnSubmit;

    private int currentQuestionIndex = 0;
    private int score = 0;

    private String[] questions = {
            "What is the capital of France?",
            "What is 2 + 2?",
            "Who wrote 'Romeo and Juliet'?"
    };

    private String[][] options = {
            {"Paris", "London", "Berlin", "Madrid"},
            {"3", "4", "5", "6"},
            {"Shakespeare", "Dickens", "Hemingway", "Austen"}
    };

    private int[] correctAnswers = {0, 1, 0}; // 0 is for the first option, 1 is for the second, etc.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz1);

        tvQuestion = findViewById(R.id.tvQuestion);
        tvScore = findViewById(R.id.tvScore);
        radioGroup = findViewById(R.id.radioGroup);
        btnNext = findViewById(R.id.btnNext);
        btnSubmit = findViewById(R.id.btnSubmit);

        // Set the first question
        setQuestion();

        // Next Button Click Listener
        btnNext.setOnClickListener(v -> {
            int selectedAnswer = radioGroup.getCheckedRadioButtonId();
            if (selectedAnswer != -1) {
                RadioButton selectedRadioButton = findViewById(selectedAnswer);
                int answerIndex = radioGroup.indexOfChild(selectedRadioButton);
                if (answerIndex == correctAnswers[currentQuestionIndex]) {
                    score++;
                }
            }

            // Update the score TextView
            tvScore.setText("Score: " + score);

            // Move to the next question
            currentQuestionIndex++;

            if (currentQuestionIndex < questions.length) {
                setQuestion();
            } else {
                btnNext.setVisibility(View.GONE);
                btnSubmit.setVisibility(View.VISIBLE);
            }
        });

        // Submit Button Click Listener
        btnSubmit.setOnClickListener(v -> {
            Toast.makeText(QuizActivity.this, "Your Final Score: " + score + "/" + questions.length, Toast.LENGTH_SHORT).show();
            saveBestScore(score);  // Save the best score after quiz completion
        });
    }

    private void setQuestion() {
        tvQuestion.setText(questions[currentQuestionIndex]);

        radioGroup.clearCheck();

        RadioButton rbOption1 = (RadioButton) radioGroup.getChildAt(0);
        RadioButton rbOption2 = (RadioButton) radioGroup.getChildAt(1);
        RadioButton rbOption3 = (RadioButton) radioGroup.getChildAt(2);
        RadioButton rbOption4 = (RadioButton) radioGroup.getChildAt(3);

        rbOption1.setText(options[currentQuestionIndex][0]);
        rbOption2.setText(options[currentQuestionIndex][1]);
        rbOption3.setText(options[currentQuestionIndex][2]);
        rbOption4.setText(options[currentQuestionIndex][3]);
    }

    private void saveBestScore(int score) {
        // Get the current logged-in user's UID
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Create a User object with the new score
        DocumentReference userRef = FirebaseFirestore.getInstance().collection("Users").document(userId);

        // Update only the best score if it is higher
        userRef.update("bestScore", Math.max(score, getCurrentBestScore(userRef)))
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(QuizActivity.this, "Best score updated!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(QuizActivity.this, "Error saving data", Toast.LENGTH_SHORT).show();
                });
    }

    private int getCurrentBestScore(DocumentReference userRef) {
        // Retrieve current best score logic (this can be implemented as required)
        return 0;  // Return 0 or actual current best score from Firestore if needed.
    }
}
