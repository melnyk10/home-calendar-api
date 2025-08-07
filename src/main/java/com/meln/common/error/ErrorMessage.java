package com.meln.common.error;

public interface ErrorMessage {

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

}
