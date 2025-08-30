<<<<<<<< HEAD:src/main/java/com/meln/app/event/provider/hltv/HltvScheduler.java
package com.meln.app.event.provider.hltv;
========
package com.meln.app.event.hltv;
>>>>>>>> 4626548 (move files):src/main/java/com/meln/app/event/hltv/HltvService.java

import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.AllArgsConstructor;

@ApplicationScoped
@AllArgsConstructor(onConstructor_ = @Inject)
public class HltvScheduler {

  private final HltvTeamService hltvTeamService;
  private final HltvMatchService hltvMatchService;

  @Scheduled(every = "15m")
  protected void sync() {
    var hltvTeams = hltvTeamService.syncTeams();
    hltvMatchService.syncMatches(hltvTeams);
  }

}
