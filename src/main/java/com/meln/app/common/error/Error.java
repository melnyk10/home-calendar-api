package com.meln.app.common.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Error {

  private String code;
  private String message;

  public static Error from(String code, String message) {
    return Error.builder().code(code).message(message).build();
  }
}
