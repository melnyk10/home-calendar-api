package com.meln.app.calendar;

import com.meln.app.calendar.model.CalendarIntegration;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CalendarIntegrationRepository implements PanacheMongoRepository<CalendarIntegration> {

}
