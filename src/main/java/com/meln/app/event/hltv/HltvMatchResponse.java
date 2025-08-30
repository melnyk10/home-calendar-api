package com.meln.app.event.hltv;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HltvMatchResponse {

  private String eventName;
  private String eventUrl;
  private String matchId;
  private String matchUrl;
  private Instant dateTime;
  private String team1Id;
  private String team2Id;
  private Integer score1;
  private Integer score2;
  private Integer bestOf;
}
