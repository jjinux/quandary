package com.google.code.quandary;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Toast;
import com.google.code.quandary.quiz.JsonRemoteReader;
import com.google.code.quandary.quiz.Quiz;

public class RunQuizTask extends AsyncTask<String, Void, Quiz> {

    private QuizEntryActivity quizEntryActivity;

    public RunQuizTask(QuizEntryActivity quizEntryActivity) {
        this.quizEntryActivity=quizEntryActivity;

    }

    @Override
    protected Quiz doInBackground(String... urls) {
        try {
            Quiz quiz = getQuiz(urls[0]);
            Intent myIntent;
            myIntent = new Intent(quizEntryActivity, QuizActivity.class);
            myIntent.putExtra("myquiz", quiz);
            quizEntryActivity.startActivityForResult(myIntent, 0);
            return quiz;
        } catch (Exception e) {
            return null;
        }
    }

    private Quiz getQuiz(String url) {
        String jasonString= JsonRemoteReader.downloadJsonString(url);
        Quiz quiz=JsonRemoteReader.convertToQuize(jasonString)                   ;
        return quiz;
    }
}
