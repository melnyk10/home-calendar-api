package com.meln.app.event.hltv;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import lombok.RequiredArgsConstructor;

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
