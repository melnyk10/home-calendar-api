package com.meln.event.hltv;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

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
    private String team1;
    private String team2;
    private Integer score1;
    private Integer score2;
    private Integer bestOf;
}
