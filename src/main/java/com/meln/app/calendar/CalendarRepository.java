package com.meln.app.calendar;

import com.meln.app.calendar.model.Calendar;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
class CalendarRepository implements PanacheRepository<Calendar> {

}
