package com.meln.app.event.hltv;

import com.meln.app.event.hltv.model.HltvMatch;
import com.meln.app.event.hltv.model.HltvMatchResponse;
import com.meln.app.event.hltv.model.HltvTeam;
import com.meln.app.event.hltv.model.HltvTeamResponse;
import com.meln.app.event.model.EventDto;
import com.meln.app.event.model.Provider;
import lombok.experimental.UtilityClass;

@UtilityClass
public class HltvConverter {

  public HltvTeam from(HltvTeamResponse response) {
    if (response == null) {
      return null;
    }

    return HltvTeam.builder()
        .sourceId(response.getTeamId().toString())
        .slug(response.getTeamIdName())
        .teamName(response.getName())
        .logoUrl(response.getLogoUrl())
        .rank(response.getRank())
        .build();
  }

  public HltvMatch from(HltvMatchResponse response, HltvTeam team1, HltvTeam team2) {
    if (response == null || team1 == null) {
      return null;
    }

    return HltvMatch.builder()
        .matchId(response.getMatchId())
        .matchUrl(response.getMatchUrl())
        .startsAt(response.getDateTime())
        .score1(response.getScore1())
        .score2(response.getScore2())
        .team1Id(team1.getId())
        .team2Id(team2 != null ? team2.getId() : null)
        .build();
  }

  public EventDto from(HltvMatch hltvMatch, HltvTeam team1, HltvTeam team2) {
    if (hltvMatch == null || team1 == null) {
      return null;
    }

    EventDto event = new EventDto();
    event.setProvider(Provider.HLTV.toString());
    event.setAllDay(false);
    event.setSourceId(String.valueOf(hltvMatch.getMatchId()));
    event.setUrl(hltvMatch.getMatchUrl());

    String team2Name = team2 != null ? team2.getTeamName() : "TBD";
    event.setTitle(team1.getTeamName() + " vs " + team2Name);

    event.setStartAt(hltvMatch.getStartsAt());
    event.setEndAt(hltvMatch.getStartsAt() != null
        ? hltvMatch.getStartsAt().plusSeconds(2 * 60 * 60)
        : null);

    return event;
  }
}
