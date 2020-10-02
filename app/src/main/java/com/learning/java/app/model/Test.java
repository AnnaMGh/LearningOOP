package com.learning.java.app.model;

import java.util.ArrayList;

public class Test {

    private int id;
    private String token;
    private String title;
    private boolean checked;
    private boolean live;
    private ArrayList<Question> questionsList = new ArrayList<>();

    public Test() {
    }

    public Test(String title, boolean checked, boolean live, ArrayList<Question> questions) {
        this.title = title;
        this.checked = checked;
        this.live = live;
        this.questionsList = questions;
    }

//    public Test(String token, String title, boolean checked, ArrayList<Question> questions) {
//        this.token = token;
//        this.title = title;
//        this.checked = checked;
//        this.questionsList = questions;
//    }

    public Test(int id, String token, String title, boolean checked, boolean live, ArrayList<Question> questions) {
        this.id = id;
        this.token = token;
        this.title = title;
        this.checked = checked;
        this.live = live;
        this.questionsList = questions;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean getChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public boolean isLive() {
        return live;
    }

    public void setLive(boolean live) {
        this.live = live;
    }

    public ArrayList<Question> getQuestionsList() {
        return this.questionsList;
    }

    public void setQuestionsList(ArrayList<Question> questionsList) {
        this.questionsList = questionsList;
    }
}
