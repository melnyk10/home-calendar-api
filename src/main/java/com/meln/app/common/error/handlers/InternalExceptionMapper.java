package com.meln.app.common.error.handlers;

import com.meln.app.common.error.Error;
import com.meln.app.common.error.ErrorMessage;
import com.meln.app.common.error.ErrorResponse;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Provider
public class InternalExceptionMapper implements ExceptionMapper<Exception> {

  @Override
  public Response toResponse(Exception e) {
    log.error("Something went wrong", e);
    Error error = Error.from(ErrorMessage.General.Code.SOMETHING_WENT_WRONG,
        ErrorMessage.General.Message.SOMETHING_WENT_WRONG);
    return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
        .type(MediaType.APPLICATION_JSON_TYPE)
        .entity(new ErrorResponse(error))
        .build();
  }
}
