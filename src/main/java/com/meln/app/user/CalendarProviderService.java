package com.meln.app.user;

import com.meln.app.common.error.ErrorMessage.CalendarProvider.Code;
import com.meln.app.common.error.ErrorMessage.CalendarProvider.Message;
import com.meln.app.common.error.ServerException;
import com.meln.app.user.model.CalendarProvider;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Collection;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;

@Slf4j
@ApplicationScoped
@AllArgsConstructor(onConstructor_ = @Inject)
public class CalendarProviderService {

  private final CalendarProviderRepository calendarProviderRepository;

  public void save(Collection<CalendarProvider> providersCalendars) {
    try {
      calendarProviderRepository.save(providersCalendars);
    } catch (Exception exception) {
      log.error("Can't save provider calendar entities. Error message: {}", exception.getMessage());
      if (exception instanceof ConstraintViolationException constraintViolationException) {
        var constraintName = constraintViolationException.getConstraintName();
        if (constraintName != null && constraintName.equals("uq_calendar_provider")) {
          throw new ServerException(Code.CALENDAR_PROVIDER_DUPLICATION,
              Message.CALENDAR_PROVIDER_DUPLICATION);
        }
      }
      throw new ServerException(Code.CANNOT_LINK_CALENDAR_PROVIDER,
          Message.CANNOT_LINK_CALENDAR_PROVIDER);
    }
  }

}
