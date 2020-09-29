package com.learning.java.app.model;

import java.util.ArrayList;

public class Question {

    public String type = "1"; // type 1 - simple | type 2 - insert | type 3 - multiple
    public ArrayList<String> questions = new ArrayList<>();
    public ArrayList<String> answers = new ArrayList<>();
    public String multipleCorrectAnswerId = "0";

    public Question() {
        this.type = "1";
        this.questions.add("");
        this.answers.add("");
        this.multipleCorrectAnswerId = "0";
    }

    public Question(String type, ArrayList<String> questions, ArrayList<String> answers, String multipleCorrectAnswerId) {
        this.type = type;
        this.questions = questions;
        this.answers = answers;
        this.multipleCorrectAnswerId = multipleCorrectAnswerId;
    }
}
