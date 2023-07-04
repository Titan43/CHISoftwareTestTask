package com.chiSoftware.testTask;

public interface Constants {
    long TOKEN_LIFETIME = 1000*60*60*6;
    String AUTH_PATH = "/auth";
    String REGISTER_PATH = "/register";
    String EMAIL_REGEX = "\\w+@\\w+.\\w+$";
    String PHONE_NUMBER_REGEX = "\\+38\\d{10}$";
    String LINK = "localhost:8080";
}
