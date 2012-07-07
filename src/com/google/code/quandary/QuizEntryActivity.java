package com.google.code.quandary;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.code.quandary.quiz.JsonRemoteReader;
import com.google.code.quandary.quiz.Quiz;


public class QuizEntryActivity extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        final EditText et;
        final Button b;

        et = (EditText) findViewById(R.id.edittext);
        b = (Button) findViewById(R.id.button);

        b.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                String text = et.getText().toString();
                Quiz quiz = getQuiz(text);
                Toast msg = Toast.makeText(getBaseContext(),
                        "Starting quiz with Youtube ID : \n" + quiz.getVideoId(), Toast.LENGTH_LONG);
                msg.show();

            }


        });

    }

    private Quiz getQuiz(String text) {
       String jasonString="{\"video_id\":\"PYiA-q8enk8\",\"questions\":\n" +
               "[{\n" +
               "\"time\" : 9000,\n" +
               "\"question\" : \"which year is this show?\",\n" +
               "\"answers\"  : [{\n" +
               " \"text\" : \"2010\",\n" +
               " \"correct\" : false\n" +
               "},\n" +
               "{\n" +
               " \"text\" : \"2011\",\n" +
               " \"correct\" : true\n" +
               "},\n" +
               "{\n" +
               " \"text\" : \"2012\",\n" +
               " \"correct\" : false\n" +
               "}\n" +
               "]\n" +
               "}]\n" +
               "}";
        Quiz quiz=JsonRemoteReader.convertToQuize(jasonString)                   ;
        return quiz;
    }
}
