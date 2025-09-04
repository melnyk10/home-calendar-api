package com.meln.app.calendar;

import com.meln.app.calendar.model.Calendar;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CalendarRepository implements PanacheMongoRepository<Calendar> {

}
