package com.meln.app.event.provider.hltv;

import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@AllArgsConstructor(onConstructor_ = @Inject)
public class HltvScheduler {

  private final HltvTeamService hltvTeamService;
  private final HltvMatchService hltvMatchService;

  @Scheduled(every = "10m")
  protected void sync() {
    log.info("HLTV sync started");

    long start = System.currentTimeMillis();
    try {
      var hltvTeams = hltvTeamService.syncTeams();
      hltvMatchService.syncMatches(hltvTeams);
      log.info("HLTV sync finished successfully in {} ms", System.currentTimeMillis() - start);
    } catch (Exception e) {
      log.error("HLTV sync failed after {} ms", System.currentTimeMillis() - start, e);
    }
  }
}
