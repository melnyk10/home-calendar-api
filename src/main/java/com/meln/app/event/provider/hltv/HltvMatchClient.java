package com.meln.app.event.provider.hltv;

import com.meln.app.event.provider.hltv.dto.HltvMatchResponse;
import com.meln.app.event.provider.hltv.dto.HltvMatchResponse.Team;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

//todo: implement me!
@ApplicationScoped
class HltvMatchClient {

  public List<HltvMatchResponse> syncMatches(String teamId) {
    List<HltvMatchResponse> matches = new ArrayList<>();
    int matchCounter = 1000;

    matches.add(HltvMatchResponse.builder()
        .eventName("IEM Katowice ")
        .eventUrl("https://hltv.org/events/" + (2000) + "/iem-katowice")
        .matchId(String.valueOf(matchCounter++))
        .matchUrl("https://hltv.org/matches/" + (3000) + "/natus-vincere-vs-team")
        .dateTime(Instant.now().plusSeconds(86400L))
        .team1(new Team("4608", "Navi", "navi"))
        .team2(new Team("7020", "Spirit", "spirit"))
        .score1(16)
        .score2(10)
        .bestOf(3)
        .build());

    return matches;
  }
}