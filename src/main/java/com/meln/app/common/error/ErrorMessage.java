package com.meln.app.common.error;

public interface ErrorMessage {

  interface General {

    interface Code {

      String SOMETHING_WENT_WRONG = "SOMETHING_WENT_WRONG";
    }

    interface Message {

      String SOMETHING_WENT_WRONG = "Something went wrong! Please try again later.";
    }
  }

}
