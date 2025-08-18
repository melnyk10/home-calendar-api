package com.meln.event.hltv;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;

import java.util.List;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class HltvService {
    private final HltvTeamService hltvTeamService;
    private final HltvMatchService hltvMatchService;

    protected void sync() {
        List<HltvTeam> hltvTeams = hltvTeamService.syncTeams();
        hltvMatchService.syncMatches(hltvTeams);
    }

}
