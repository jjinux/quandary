package com.google.code.quandary;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import com.google.code.quandary.quiz.Question;
import com.google.code.quandary.quiz.Quiz;


public class QuizScoreActivity extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.score);




        Intent intent= getIntent();
        Quiz quiz= (Quiz) intent.getSerializableExtra("quiz");
        System.out.println(quiz.getVideoId());
        int score=calculateScore(quiz);
        TextView textView = (TextView)findViewById(R.id.quiz_score);
        textView.setText(score+" %");
        final Button b;
        b = (Button) findViewById(R.id.again_button);
        b.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(QuizScoreActivity.this, QuizActivity.class);
                startActivity(intent);
            }
        });

    }

    private int calculateScore(Quiz quiz) {
        if(quiz ==null || quiz.getQuestions()==null || quiz.getQuestions().isEmpty())    {
        return 0;
        } else{
            int correctNum=0;
            for(Question question : quiz.getQuestions()){
                if(question.getUserAnswer() != null && question.getCorrectAnswer()!=null &&
                   question.getUserAnswer()==question.getCorrectAnswer()){
                    correctNum++;
                }
            }
            return    correctNum*100/quiz.getQuestions().size();
        }

    }

}
