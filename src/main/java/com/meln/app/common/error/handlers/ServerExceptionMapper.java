package com.meln.app.common.error.handlers;

import com.meln.app.common.error.Error;
import com.meln.app.common.error.ErrorResponse;
import com.meln.app.common.error.ServerException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ServerExceptionMapper implements ExceptionMapper<ServerException> {

  @Override
  public Response toResponse(ServerException exception) {
    Error error = Error.from(exception.getCode(), exception.getMessage());
    //todo: if in code we will throw ServerException how to know what http status code to user?
    return Response.status(Status.BAD_REQUEST)
        .type(MediaType.APPLICATION_JSON_TYPE)
        .entity(new ErrorResponse(error))
        .build();
  }
}
