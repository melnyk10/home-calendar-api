package com.meln.common.error;

public interface ErrorMessage {

  interface General {

    interface Code {

      String SOMETHING_WENT_WRONG = "SOMETHING_WENT_WRONG";
    }

    interface Message {

      String SOMETHING_WENT_WRONG = "Something went wrong! Please try again later.";
    }
  }

  interface Auth {

    interface Code {

      String USERNAME_MISSING = "USERNAME_MISSING";
      String USER_NOT_FOUND = "USER_NOT_FOUND";
    }

    interface Message {

      static String USER_NOT_FOUND(String email) {
        return "User with email: %s not found".formatted(email);
      }
    }
  }

  interface User {

    interface Code {

      String USER_NOT_FOUND = "USER_NOT_FOUND";
    }

    interface Message {

      static String USER_NOT_FOUND(String email) {
        return "Cannot find user with email: %s".formatted(email);
      }
    }
  }

}
