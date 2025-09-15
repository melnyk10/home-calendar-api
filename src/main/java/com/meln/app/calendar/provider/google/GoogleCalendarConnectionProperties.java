package com.meln.app.calendar.provider.google;

import com.meln.app.calendar.CalendarConnectionProperties;
import lombok.Getter;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;

@Getter
@Setter
@BsonDiscriminator(key = "_type", value = "google")
public final class GoogleCalendarConnectionProperties implements CalendarConnectionProperties {

  String userEmail;
  String calendarId;
}
