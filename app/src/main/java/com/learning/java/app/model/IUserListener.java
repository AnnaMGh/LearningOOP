package com.learning.java.app.model;

import java.util.ArrayList;

public interface IUserListener {
    void getUser(User user);

    void getAllUsers(ArrayList<User> userList);

    void getUserToken(String token);
}
