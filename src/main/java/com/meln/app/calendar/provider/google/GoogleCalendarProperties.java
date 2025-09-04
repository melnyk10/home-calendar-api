package com.meln.app.calendar.provider.google;

import com.meln.app.calendar.CalendarProperties;
import lombok.Getter;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;

@Getter
@Setter
@BsonDiscriminator(key = "_type", value = "google")
public final class GoogleCalendarProperties implements CalendarProperties {

}
