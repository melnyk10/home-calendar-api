package com.meln.event.hltv;

import com.meln.event.Event;
import com.meln.event.Provider;
import lombok.experimental.UtilityClass;

@UtilityClass
public class HltvMatchConverter {
    public HltvMatch from(HltvMatchResponse response, HltvTeam team1, HltvTeam team2) {
        HltvMatch match = new HltvMatch();
        match.setMatchId(response.getMatchId());
        match.setMatchUrl(response.getMatchUrl());
        match.setStartsDatetime(response.getDateTime());
        match.setScore1(response.getScore1());
        match.setScore2(response.getScore2());
        match.setTeam1Id(team1.getId());
        match.setTeam2Id(team2.getId());
        return match;
    }

    public Event from(HltvMatch hltvMatch, HltvTeam team1, HltvTeam team2) {
        Event event = new Event();
        event.setProvider(Provider.HLTV);
        event.setAllDay(false);
        event.setExternalId(String.valueOf(hltvMatch.getMatchId()));
        event.setUrl(hltvMatch.getMatchUrl());

        String team2Name = team2 != null ? team2.getTeamName() : "TBD";
        event.setTitle(team1.getTeamName() + " vs " + team2Name);

        event.setStartAt(hltvMatch.getStartsDatetime());
        event.setEndAt(hltvMatch.getStartsDatetime() != null
                ? hltvMatch.getStartsDatetime().plusSeconds(2 * 60 * 60)
                : null);

        return event;
    }
}
