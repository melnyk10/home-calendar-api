package com.meln.common;

public interface Endpoints {
    String API = "/api";
    String V1 = "/v1";
    String API_V1 = API + V1;

    interface User {
        String USERS = "/users";
        String ME = USERS + "/me";
    }

    interface Hltv {
        String HLTV = "/hltv";
        String HLTV_SYNC = HLTV + "/sync";
    }
}
