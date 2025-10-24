package com.meln.app.event.provider.hltv.dto;

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
  private Team team1;
  private Team team2;
  private Integer score1;
  private Integer score2;
  private Integer bestOf;

  @Getter
  @Setter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Team {

    private String id;
    private String name;
  }

}
