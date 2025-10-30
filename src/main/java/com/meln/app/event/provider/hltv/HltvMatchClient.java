package com.meln.app.event.provider.hltv;

import com.meln.app.event.provider.hltv.dto.HltvMatchResponse;
import com.meln.app.event.provider.hltv.dto.HltvMatchResponse.Team;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

//todo: implement me!
@ApplicationScoped
class HltvMatchClient {

  public List<HltvMatchResponse> syncMatches(String teamId) {
    if (!teamId.equals("4608")) {
      return Collections.emptyList();
    }

    var naviMatch = HltvMatchResponse.builder()
        .eventName("IEM Katowice ")
        .eventUrl("https://hltv.org/events/" + (2000) + "/iem-katowice")
        .matchId("1000")
        .matchUrl("https://hltv.org/matches/" + (3000) + "/natus-vincere-vs-team")
        .dateTime(Instant.now().plusSeconds(86400L))
        .team1(new Team("4608", "Navi", "navi"))
        .team2(new Team("7020", "Spirit", "spirit"))
        .score1(16)
        .score2(10)
        .bestOf(5)
        .build();

    return List.of(naviMatch);
  }
}