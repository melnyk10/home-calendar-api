package com.meln.event.hltv;

import jakarta.enterprise.context.ApplicationScoped;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@ApplicationScoped
public class HltvMatchClient {
    public List<HltvMatchResponse> syncMatches(Collection<HltvTeam> teams) {
        List<HltvMatchResponse> matches = new ArrayList<>();
        int matchCounter = 1000;

        for (HltvTeam team : teams) {
            if (team.getTeamId().equals("9565")) {
                for (int i = 1; i <= 3; i++) {
                    matches.add(HltvMatchResponse.builder()
                            .eventName("IEM Katowice " + i)
                            .eventUrl("https://hltv.org/events/" + (2000 + i) + "/iem-katowice-" + i)
                            .matchId(String.valueOf(matchCounter++))
                            .matchUrl("https://hltv.org/matches/" + (3000 + i) + "/natus-vincere-vs-team" + i)
                            .dateTime(Instant.now().plusSeconds(86400L * i))
                            .team1(team.getTeamName())
                            .team2("Opponent " + i)
                            .score1(16)
                            .score2(10 + i)
                            .bestOf(3)
                            .build());
                }
            } else {
                matches.add(HltvMatchResponse.builder()
                        .eventName("Blast Premier vs " + team.getTeamName())
                        .eventUrl("https://hltv.org/events/" + (4000 + matchCounter) + "/blast-premier-vs-" + team.getSlug())
                        .matchId(String.valueOf(matchCounter++))
                        .matchUrl("https://hltv.org/matches/" + (5000 + matchCounter) + "/match-vs-" + team.getSlug())
                        .dateTime(Instant.now().plusSeconds(172800L))
                        .team1(team.getTeamName())
                        .team2("Random Opponent")
                        .score1(14)
                        .score2(16)
                        .bestOf(1)
                        .build());
            }
        }

        return matches;
    }
}