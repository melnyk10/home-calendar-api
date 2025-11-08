package com.meln.api;

public interface Endpoints {

  String API = "/api";
  String V1 = "/v1";
  String API_V1 = API + V1;

  interface User {

    String USERS = "/users";
    String ME = USERS + "/me";
  }

  interface Provider {

    String PROVIDERS = "/providers";
  }

  interface Calendar {

    String CALENDARS = "/calendars";
    String SYNC = CALENDARS + "/sync";
  }

  interface CalendarProvider {

    String CALENDAR_PROVIDER = "/calendar-provider";
  }

  interface Google {

    String GOOGLE = "/google";
    String CONNECT = GOOGLE + "/connect";
    String CALLBACK = GOOGLE + "/callback";
    String DISCONNECT = GOOGLE + "/disconnect";
  }
}
