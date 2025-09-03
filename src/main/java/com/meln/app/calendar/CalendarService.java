package com.meln.app.calendar;

import com.meln.app.calendar.model.CalendarIntegration;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class CalendarService {

  private final CalendarIntegrationRepository repository;

  public Map<ObjectId, CalendarIntegrationProperties> getCalendarIntegrationPropsByUserId() {
    List<CalendarIntegration> calendarIntegrations = repository.listAll();
    return calendarIntegrations.stream()
        .collect(Collectors.toMap(
            CalendarIntegration::getUserId,
            CalendarIntegration::getProperties)
        );
  }

}
