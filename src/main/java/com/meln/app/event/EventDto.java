<<<<<<<< HEAD:src/main/java/com/meln/app/event/model/EventDto.java
package com.meln.app.event.model;
========
package com.meln.app.event;
>>>>>>>> 4626548 (move files):src/main/java/com/meln/app/event/EventDto.java

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventDto {

  private String sourceId;
  private String provider;
  private String title;
  private String url;
  private String notes;
  private boolean allDay;
  private Instant startAt;
  private Instant endAt;
}
