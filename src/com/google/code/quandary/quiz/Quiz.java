package com.google.code.quandary.quiz;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Bill
 * Date: 07/07/12
 * Time: 11:07
 * To change this template use File | Settings | File Templates.
 */
public class Quiz implements Serializable {
    String videoId;

   List<Question>   questions =new ArrayList<Question>();

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

}
