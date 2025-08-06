package com.meln.common;

public interface EndPoints {
    String API = "/api";
    String V1 = "/v1";
    String API_V1 = API + V1;

    interface User {
        String USERS = "/users";
        String ME = USERS + "/me";
    }
}
