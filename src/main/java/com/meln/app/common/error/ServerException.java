package com.meln.app.common.error;

import lombok.Getter;

public class ServerException extends RuntimeException {

  @Getter
  private final String code;

  public ServerException(String code, String message) {
    super(message);
    this.code = code;
  }
}
