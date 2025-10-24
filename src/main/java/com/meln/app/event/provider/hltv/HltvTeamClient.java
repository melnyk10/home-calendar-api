package com.meln.app.event.provider.hltv;

import com.meln.app.event.provider.hltv.dto.HltvTeamResponse;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

//todo: implement me!
@ApplicationScoped
class HltvTeamClient {

  public List<HltvTeamResponse> syncTeams() {
    HltvTeamResponse team1 = HltvTeamResponse.builder()
        .rank(1)
        .name("Natus Vincere")
        .logoUrl("https://hltv.org/img/static/team/navi.png")
        .teamId("4608")
        .teamIdName("natus-vincere")
        .build();

    HltvTeamResponse team2 = HltvTeamResponse.builder()
        .rank(2)
        .name("Team Vitality")
        .logoUrl("https://hltv.org/img/static/team/vitality.png")
        .teamId("9565")
        .teamIdName("vitality")
        .build();

    HltvTeamResponse team3 = HltvTeamResponse.builder()
        .rank(3)
        .name("FaZe Clan")
        .logoUrl("https://hltv.org/img/static/team/faze.png")
        .teamId("6667")
        .teamIdName("faze")
        .build();

    return List.of(team1, team2, team3);
  }
}