package com.meln.app.calendar.model;

import com.meln.app.calendar.CalendarIntegrationProperties;
import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.Getter;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

@Getter
@Setter
@MongoEntity(collection = "calendarIntegrations")
public class CalendarIntegration extends PanacheMongoEntity {

  public static final String COL_USER_ID = "userId";
  public static final String COL_SOURCE_ID = "sourceId";
  public static final String COL_PROPERTIES = "properties";

  @BsonProperty(COL_USER_ID)
  private ObjectId userId;

  @BsonProperty(COL_SOURCE_ID)
  private String sourceId;

  @BsonProperty(value = COL_PROPERTIES, useDiscriminator = true)
  private CalendarIntegrationProperties properties;
}
