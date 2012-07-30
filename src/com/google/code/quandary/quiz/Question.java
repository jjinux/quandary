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

package com.google.code.quandary.quiz;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Question implements Serializable {
    Long timeToPause;
    String questionDescription;
    List <String> answers = new ArrayList<String>();
    Integer correctAnswer;
    Integer userAnswer;

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

    public Integer getUserAnswer() {
        return userAnswer;
    }

    public void setUserAnswer(Integer userAnswer) {
        this.userAnswer = userAnswer;
    }


}
