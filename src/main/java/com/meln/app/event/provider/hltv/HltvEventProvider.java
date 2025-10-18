package com.meln.app.event.provider.hltv;

import com.meln.app.event.EventProvider;
import com.meln.app.event.model.EventPayload;
import com.meln.app.event.model.ProviderType;
import com.meln.app.event.provider.hltv.model.HltvMatchResponse;
import com.meln.app.event.provider.hltv.model.HltvTeamResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;

@ApplicationScoped
@AllArgsConstructor(onConstructor_ = @Inject)
class HltvEventProvider implements EventProvider<HltvCriteria> {

  private HltvTeamClient hltvTeamClient;
  private HltvMatchClient hltvMatchClient;

  @Override
  public ProviderType providerType() {
    return ProviderType.HLTV;
  }

  @Override
  public List<EventPayload> fetchAll() {
    List<EventPayload> events = new ArrayList<>();

    var hltvTeamResponses = hltvTeamClient.syncTeams();
    for (var hltvTeamResponse : hltvTeamResponses) {
      var matchResponses = hltvMatchClient.syncMatches(hltvTeamResponse);
      for (var matchResponse : matchResponses) {
        var eventPayload = from(hltvTeamResponse, matchResponse);
        events.add(eventPayload);
      }
    }

    return events;
  }

  private EventPayload from(HltvTeamResponse hltvTeam, HltvMatchResponse hltvMatch) {
    return null;
  }

}
