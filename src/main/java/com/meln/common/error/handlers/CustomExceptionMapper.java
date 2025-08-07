package com.meln.common.error.handlers;

import com.meln.common.error.CustomException;
import com.meln.common.error.Error;
import com.meln.common.error.ErrorResponse;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class CustomExceptionMapper implements ExceptionMapper<CustomException> {
    @Override
    public Response toResponse(CustomException e) {
        Error error = Error.from(e.getCode(), e.getMessage());
        return Response.status(e.getHttpStatus())
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(new ErrorResponse(error))
                .build();
    }
}
