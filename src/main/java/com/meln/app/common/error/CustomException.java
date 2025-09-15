package com.meln.app.common.error;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

  private final Response.Status httpStatus;
  private final String code;

  public CustomException(Response.Status httpStatus, String code, String message) {
    super(message);
    this.httpStatus = httpStatus;
    this.code = code;
  }

  public CustomException(Response.Status httpStatus, String code, String message,
      Throwable throwable) {
    super(message, throwable);
    this.httpStatus = httpStatus;
    this.code = code;
  }

  public static class CustomAuthException extends CustomException {

    public CustomAuthException(String code, String message) {
      super(Status.UNAUTHORIZED, code, message);
    }
  }

  public static class CustomBadRequestException extends CustomException {

    public CustomBadRequestException(String code, String message) {
      super(Status.BAD_REQUEST, code, message);
    }

    public CustomBadRequestException(String code, String message,
        Throwable throwable) {
      super(Status.BAD_REQUEST, code, message, throwable);
    }
  }

  public static class CustomRetryableException extends CustomException {

    public CustomRetryableException(String code, String message) {
      super(Status.TOO_MANY_REQUESTS, code, message);
    }
  }

  public static class CustomUnprocessableEntityException extends CustomException {

    public CustomUnprocessableEntityException(String code, String message) {
      super(Status.fromStatusCode(422), code, message);
    }
  }

}
