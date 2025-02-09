package com.example.myedu;



import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity5 extends AppCompatActivity {

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
        setContentView(R.layout.activity_main5); // Your layout XML

        tvQuestion = findViewById(R.id.tvQuestion);
        tvScore = findViewById(R.id.tvScore); // TextView for displaying score
        radioGroup = findViewById(R.id.radioGroup);
        btnNext = findViewById(R.id.btnNext);
        btnSubmit = findViewById(R.id.btnSubmit);

        // Set the first question
        setQuestion();

        // Next Button Click Listener
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if the selected answer is correct
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
            }
        });

        // Submit Button Click Listener
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show the final score as a Toast
                Toast.makeText(MainActivity5.this, "Your Final Score: " + score + "/" + questions.length, Toast.LENGTH_SHORT).show();
                //finish(); // Close the quiz activity after submission
            }
        });
    }

    private void setQuestion() {
        tvQuestion.setText(questions[currentQuestionIndex]);
        // Reset the radio group (deselect any selected option)
        radioGroup.clearCheck();

        // Set the options
        RadioButton rbOption1 = (RadioButton) radioGroup.getChildAt(0);
        RadioButton rbOption2 = (RadioButton) radioGroup.getChildAt(1);
        RadioButton rbOption3 = (RadioButton) radioGroup.getChildAt(2);
        RadioButton rbOption4 = (RadioButton) radioGroup.getChildAt(3);

        rbOption1.setText(options[currentQuestionIndex][0]);
        rbOption2.setText(options[currentQuestionIndex][1]);
        rbOption3.setText(options[currentQuestionIndex][2]);
        rbOption4.setText(options[currentQuestionIndex][3]);
    }
}
