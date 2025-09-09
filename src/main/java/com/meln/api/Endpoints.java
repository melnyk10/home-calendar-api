package com.meln.api;

public interface Endpoints {

  String API = "/api";
  String V1 = "/v1";
  String API_V1 = API + V1;

  interface User {

    String USERS = "/users";
    String ME = USERS + "/me";
  }

  interface Subscription {

    String SUBSCRIPTIONS = "/subscriptions";
  }

  interface GoogleCalendar {

    String GOOGLE = "/google";
    String CONNECT = GOOGLE + "/connect";
    String CALLBACK = GOOGLE + "/callback";
    String DISCONNECT = GOOGLE + "/disconnect";
  }
}
