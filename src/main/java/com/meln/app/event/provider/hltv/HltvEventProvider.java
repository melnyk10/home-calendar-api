package com.meln.app.event.provider.hltv;

import com.meln.app.event.Provider;
import com.meln.app.event.TargetRepository;
import com.meln.app.event.model.DateRange;
import com.meln.app.event.model.EventPayload;
import com.meln.app.event.model.EventPayload.TargetPayload;
import com.meln.app.event.model.Target;
import com.meln.app.event.model.ProviderType;
import com.meln.app.event.model.TargetType;
import com.meln.app.event.provider.hltv.dto.HltvMatchResponse;
import com.meln.app.event.provider.hltv.dto.HltvMatchResponse.Team;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.AllArgsConstructor;

@ApplicationScoped
@AllArgsConstructor(onConstructor_ = @Inject)
class HltvEventProvider implements Provider {

  private final HltvMatchClient hltvMatchClient;
  private final TargetRepository targetRepository;

  @Override
  public ProviderType providerType() {
    return ProviderType.HLTV;
  }

  @Override
  public List<EventPayload> fetchAll() {
    List<EventPayload> events = new ArrayList<>();

    var hltvTeamIds = targetRepository.listAllByProvider(providerType()).stream()
        .map(Target::getData)
        .map(it -> it.get("id"))
        .filter(Objects::nonNull)
        .map(String::valueOf)
        .toList();

    for (var teamId : hltvTeamIds) {
      var matchResponses = hltvMatchClient.syncMatches(teamId);
      for (var matchResponse : matchResponses) {
        var eventPayload = from(matchResponse);
        events.add(eventPayload);
      }
    }

    return events;
  }

  private EventPayload from(HltvMatchResponse hltvMatch) {
    var eventName =
        hltvMatch.getTeam1().getName() + " vs " + parseTeamName(hltvMatch.getTeam2());

    var eventDuration = calculateEventDuration(hltvMatch);
    return EventPayload.builder()
        .sourceId(buildSourceId(hltvMatch))
        .name(eventName)
        .provider(ProviderType.HLTV)
        .type(TargetType.MATCH)
        .details(parseDetails(hltvMatch))
        .url(hltvMatch.getMatchUrl())
        .isAllDay(false)
        .startAt(eventDuration.startAt())
        .endAt(eventDuration.endAt())
        .zone(ZoneId.systemDefault()) //todo: fix me!
        .targets(parseTargets(hltvMatch))
        .build();
  }

  //todo: maybe implement interface that will have buildSourceId ?
  private String buildSourceId(HltvMatchResponse eventPayload) {
    return providerType().name().toLowerCase() +
        TargetType.MATCH.name().toLowerCase() +
        eventPayload.getMatchId();
  }

  private String parseTeamName(HltvMatchResponse.Team team) {
    return Optional.ofNullable(team)
        .map(HltvMatchResponse.Team::getName)
        .orElse("TBD");
  }

  private List<TargetPayload> parseTargets(HltvMatchResponse hltvMatch) {
    if (hltvMatch == null) {
      return List.of();
    }

    List<TargetPayload> targets = new ArrayList<>();
    Team team1 = hltvMatch.getTeam1();
    if (team1 != null) {
      targets.add(TargetPayload.builder()
          .id(buildTargetId(team1))
          .name(team1.getName())
          .type(TargetType.TEAM)
          .data(Map.of("id", team1.getId(), "slug", team1.getSlug()))
          .build());
    }
    Team team2 = hltvMatch.getTeam2();
    if (team2 != null) {
      targets.add(TargetPayload.builder()
          .sourceId(buildTargetId(team2))
          .name(team2.getName())
          .type(TargetType.TEAM)
          .data(Map.of("id", team2.getId(), "slug", team2.getSlug()))
          .build());
    }

    return targets;
  }

  //todo: maybe implement interface that will have buildTargetId ?
  private String buildTargetId(HltvMatchResponse.Team hltvMatch) {
    return "hltv".concat(":").concat("team").concat(":").concat(hltvMatch.getId());
  }

  private String parseDetails(HltvMatchResponse hltvMatch) {
    var secondTeamName = parseTeamName(hltvMatch.getTeam2());
    return String.format("""
            Match: %s vs %s
            Tournament: %s
            Format: Best of %d
            Match Page: %s
            Event Page: %s
            """,
        hltvMatch.getTeam1().getName(),
        secondTeamName,
        hltvMatch.getEventName(),
        hltvMatch.getBestOf(),
        hltvMatch.getMatchUrl(),
        hltvMatch.getEventUrl()
    );
  }

  private DateRange calculateEventDuration(HltvMatchResponse hltvMatch) {
    //todo: calculate startAt and endAt based on hltvMatch.startAt and bestOf
    // respect zone
    return new DateRange(Instant.now(), Instant.now());
  }

}
