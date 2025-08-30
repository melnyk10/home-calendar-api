<<<<<<<< HEAD:src/main/java/com/meln/app/event/provider/hltv/HltvEventProvider.java
package com.meln.app.event.provider.hltv;

import com.meln.app.event.provider.hltv.model.HltvMatch;
import com.meln.app.event.provider.hltv.model.HltvTeam;
import com.meln.app.event.model.EventDto;
========
package com.meln.app.event.hltv;

import com.meln.app.event.EventDto;
>>>>>>>> 4626548 (move files):src/main/java/com/meln/app/event/hltv/HltvEventProvider.java
import com.meln.app.event.EventProvider;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import lombok.AllArgsConstructor;

@ApplicationScoped
@AllArgsConstructor(onConstructor_ = @Inject)
public class HltvEventProvider implements EventProvider<CriteriaHltv> {

  private final HltvTeamService hltvTeamService;
  private final HltvMatchService hltvMatchService;

  @Override
  public Class<CriteriaHltv> criteriaType() {
    return CriteriaHltv.class;
  }

  @Override
  public List<EventDto> fetch(CriteriaHltv criteria) {
    var matches = hltvMatchService.getAllByTeamId(criteria.getTeamIds());
    return matches.stream()
        .map(this::from)
        .toList();
  }

  private EventDto from(HltvMatch hltvMatch) {
    //todo: improve! on each event creation we will query DB
    var team1 = hltvTeamService.getById(hltvMatch.getTeam1Id());
    var team2 = hltvTeamService.getById(hltvMatch.getTeam2Id());
    return HltvConverter.from(hltvMatch, team1, team2);
  }
}
