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

    if (teamId.equals("9565")) {
      for (int i = 1; i <= 3; i++) {
        matches.add(HltvMatchResponse.builder()
            .eventName("IEM Katowice " + i)
            .eventUrl("https://hltv.org/events/" + (2000 + i) + "/iem-katowice-" + i)
            .matchId(String.valueOf(matchCounter++))
            .matchUrl("https://hltv.org/matches/" + (3000 + i) + "/natus-vincere-vs-team" + i)
            .dateTime(Instant.now().plusSeconds(86400L * i))
            .team1(new Team("9565", "Navi", "navi"))
            .team2(new Team("12352", "Spirit", "spirit"))
            .score1(16)
            .score2(10 + i)
            .bestOf(3)
            .build());
      }
    } else {
      matches.add(HltvMatchResponse.builder()
//          .eventName("Blast Premier vs " + team.getName())
//          .eventUrl("https://hltv.org/events/" + (4000 + matchCounter) + "/blast-premier-vs-"
//              + team.getTeamIdName())
          .matchId(String.valueOf(matchCounter++))
//          .matchUrl(
//              "https://hltv.org/matches/" + (5000 + matchCounter) + "/match-vs-"
//                  + team.getTeamIdName())
          .dateTime(Instant.now().plusSeconds(172800L))
          .team1(null)
          .team2(null)
          .score1(14)
          .score2(16)
          .bestOf(1)
          .build());
    }

    return matches;
  }
}