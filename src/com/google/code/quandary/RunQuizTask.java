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
