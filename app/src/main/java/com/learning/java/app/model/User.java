package com.learning.java.app.model;

/**
 * Created by Anna on 3/13/2018.
 */

public class User {

    //attributes
    private String token;
    private int id;
    private String name;
    private String email;
    private String password;
    private String function;
    private int notifications;
    private int learnProgress;
    private int testProgress;
    private int daysInARaw;
    private int totalPoints;
    private String photo;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }


    //set
    public void setToken(String token) {
        this.token = token;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public void setNotifications(int notifications) {
        this.notifications = notifications;
    }

    public void setLearnProgress(int learnProgress) {
        this.learnProgress = learnProgress;
    }

    public void setTestProgress(int testProgress) {
        this.testProgress = testProgress;
    }

    public void setDaysInARaw(int daysInARaw) {
        this.daysInARaw = daysInARaw;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }


    //get
    public String getToken() {
        return token;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPassword() {
        return this.password;
    }

    public String getFunction() {
        return this.function;
    }

    public int getNotifications() {
        return this.notifications;
    }

    public int getLearnProgress() {
        return this.learnProgress;
    }

    public int getTestProgress() {
        return this.testProgress;
    }

    public int getDaysInARaw() {
        return this.daysInARaw;
    }

    public int getTotalPoints() {
        return this.totalPoints;
    }

    public String getPhoto() {
        return photo;
    }

    //constructor
    public User() {
    }

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public User(String name, String email, String password, String function, int notifications, int learnProgress, int testProgress, int daysInARaw, int totalPoints) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.function = function;
        this.notifications = notifications;
        this.learnProgress = learnProgress;
        this.testProgress = testProgress;
        this.daysInARaw = daysInARaw;
        this.totalPoints = totalPoints;
    }


    public User(String name, String email, String password, String function, int notifications, int learnProgress, int testProgress, int daysInARaw, int totalPoints, String photoByte) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.function = function;
        this.notifications = notifications;
        this.learnProgress = learnProgress;
        this.testProgress = testProgress;
        this.daysInARaw = daysInARaw;
        this.totalPoints = totalPoints;
        this.photo = photoByte;
    }


    public User(int id, String name, String email, String password, String function, int notifications, int learnProgress, int testProgress, int daysInARaw, int totalPoints, String photoByte) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.function = function;
        this.notifications = notifications;
        this.learnProgress = learnProgress;
        this.testProgress = testProgress;
        this.daysInARaw = daysInARaw;
        this.totalPoints = totalPoints;
        this.photo = photoByte;
    }

    public User(int id, String name, String email, String password, String function, int notifications, int learnProgress, int testProgress, int daysInARaw, int totalPoints) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.function = function;
        this.notifications = notifications;
        this.learnProgress = learnProgress;
        this.testProgress = testProgress;
        this.daysInARaw = daysInARaw;
        this.totalPoints = totalPoints;
    }
}
