package com.google.code.quandary.quiz;


import java.util.ArrayList;
import java.util.List;

public class Question {
    Long timeToPause;
    String questionDescription;
    List <String> answers = new ArrayList<String>();
    Integer correctAnswer;

    public Long getTimeToPause() {
        return timeToPause;
    }

    public void setTimeToPause(Long timeToPause) {
        this.timeToPause = timeToPause;
    }

    public String getQuestionDescription() {
        return questionDescription;
    }

    public void setQuestionDescription(String questionDescription) {
        this.questionDescription = questionDescription;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public void setAnswers(List<String> answers) {
        this.answers = answers;
    }

    public Integer getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(Integer correctAnswer) {
        this.correctAnswer = correctAnswer;
    }


}
