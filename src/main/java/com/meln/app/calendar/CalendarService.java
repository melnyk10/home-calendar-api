package com.meln.app.calendar;

import com.meln.app.calendar.model.Calendar;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class CalendarService {

  private final CalendarRepository repository;

  public Map<ObjectId, CalendarProperties> getCalendarIntegrationPropsByUserId() {
    var calendars = repository.listAll();
    return calendars.stream()
        .collect(Collectors.toMap(
            Calendar::getUserId,
            Calendar::getProperties)
        );
  }

}
