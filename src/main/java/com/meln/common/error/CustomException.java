package com.meln.common.error;

import jakarta.ws.rs.core.Response;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private final Response.Status httpStatus;
    private final String code;

    public CustomException(Response.Status httpStatus, String code) {
        this.httpStatus = httpStatus;
        this.code = code;
    }

    public CustomException(Response.Status httpStatus, String code, String message) {
        super(message);
        this.httpStatus = httpStatus;
        this.code = code;
    }

    public CustomException(Response.Status httpStatus, String code, String message, Throwable throwable) {
        super(message, throwable);
        this.httpStatus = httpStatus;
        this.code = code;
    }

}
