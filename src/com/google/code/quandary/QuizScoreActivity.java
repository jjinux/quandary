/*
 * Copyright 2012 Bin Lin and Shannon Behrens.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *   http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
                intent = new Intent(QuizScoreActivity.this, QuizEntryActivity.class);
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
                   question.getUserAnswer().equals(question.getCorrectAnswer())){
                    correctNum++;
                }
            }
            return    correctNum*100/quiz.getQuestions().size();
        }

    }

}
