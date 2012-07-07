package com.google.code.quandary;

import android.app.Activity;
import android.content.Intent;
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
                String url = et.getText().toString();
                if(url == null || url.trim().isEmpty()){
                    url=getResources().getString(R.string.QuizDefaultUrl);
                }
                Toast msg = Toast.makeText(getBaseContext(),
                        "Loading ..." , Toast.LENGTH_LONG);
                msg.show();

                new RunQuizTask(v.getContext(),QuizEntryActivity.this).execute(url);

            }


        });

    }

}
