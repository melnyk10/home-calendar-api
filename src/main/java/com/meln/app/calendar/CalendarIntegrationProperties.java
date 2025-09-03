package com.meln.app.calendar;

import org.bson.codecs.pojo.annotations.BsonDiscriminator;

@BsonDiscriminator(key = "_type")
public interface CalendarIntegrationProperties {

}
