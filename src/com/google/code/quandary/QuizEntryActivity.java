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
        et.setHint(getResources().getString(R.string.QuizDefaultUrl));
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

                new RunQuizTask(QuizEntryActivity.this).execute(url);

            }


        });

    }

}
