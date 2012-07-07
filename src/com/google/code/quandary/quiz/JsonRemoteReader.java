package com.google.code.quandary.quiz;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class JsonRemoteReader {


    public static Quiz convertToQuize(String jasonString)      {
          Quiz quiz = new Quiz();
        try {
            JSONObject jsonQuize = new JSONObject(jasonString);
            // Get the query value'
            String video_id = jsonQuize.getString("video_id");
            quiz.setVideoId(video_id);
            // Make array of the questions
            JSONArray questions = jsonQuize.getJSONArray("questions");
            for (int i = 0; i < questions.length(); i++) {
                Question question=new Question();
                quiz.getQuestions().add(question);
                JSONObject jsonQuestion = questions.getJSONObject(i);
                Long timeToPause=jsonQuestion.getLong("time");
                 String questionString=  jsonQuestion.getString("question")   ;
                question.setTimeToPause(timeToPause);
                question.setQuestionDescription(questionString);
                JSONArray answers =jsonQuestion.getJSONArray("answers");
                for(int j=0; j<answers.length(); j++){
                    JSONObject jsonAnswer = answers.getJSONObject(j);
                    String text=  jsonAnswer.getString("text");
                    question.getAnswers().add(text);
                    boolean correct = jsonAnswer.getBoolean("correct");
                    if(correct)    {
                        question.setCorrectAnswer(i);
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return quiz;
    }

    public static String downloadJsonString(String url){
        if(!url.startsWith("http://"))    {
            url="http://"+url;
        }

        // Create the httpclient
        HttpClient httpclient = new DefaultHttpClient();

        // Prepare a request object
        HttpGet httpget = new HttpGet(url);

        // Execute the request
        HttpResponse response;

        // return string
        String returnString = null;

        try {

            // Open the webpage.
            response = httpclient.execute(httpget);

            if(response.getStatusLine().getStatusCode() == 200){
                // Connection was established. Get the content.

                HttpEntity entity = response.getEntity();
                // If the response does not enclose an entity, there is no need
                // to worry about connection release

                if (entity != null) {
                    // A Simple JSON Response Read
                    InputStream instream = entity.getContent();

                    // Load the requested page converted to a string into a JSONObject.
                     returnString=  convertStreamToString(instream);
                    System.out.println(returnString);
                    // Cose the stream.
                    instream.close();
               }
            }
            else {
                // code here for a response othet than 200.  A response 200 means the webpage was ok
                // Other codes include 404 - not found, 301 - redirect etc...
                // Display the response line.
                return null;
            }
        }
        catch (IOException ex) {
            // thrown by line 80 - getContent();
            // Connection was not established
            ex.printStackTrace();
            returnString = "Connection failed; " + ex.getMessage();
            return "{\"video_id\":\"PYiA-q8enk8\",\"questions\":\n" +
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
        }
        return returnString;
    }

    private static String convertStreamToString(InputStream is) {
        /*
         * To convert the InputStream to String we use the BufferedReader.readLine()
         * method. We iterate until the BufferedReader return null which means
         * there's no more data to read. Each line will appended to a StringBuilder
         * and returned as String.
         */
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
