package com.learning.java.app.model;

import java.util.ArrayList;

public interface ITestListener {

    void getAllTests(ArrayList<Test> testList);

    void getTest(Test test);
}
