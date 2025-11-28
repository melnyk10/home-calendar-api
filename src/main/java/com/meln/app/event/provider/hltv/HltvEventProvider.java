package com.meln.app.event.provider.hltv;

import com.meln.app.event.Provider;
import com.meln.app.event.TargetRepository;
import com.meln.app.event.model.DateRange;
import com.meln.app.event.model.EventPayload;
import com.meln.app.event.model.EventPayload.TargetPayload;
import com.meln.app.event.model.ProviderType;
import com.meln.app.event.model.Target;
import com.meln.app.event.model.TargetType;
import home_calendar.hltv.HltvMatch;
import home_calendar.hltv.HltvTeamBrief;
import home_calendar.hltv.SyncMatchesResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@AllArgsConstructor(onConstructor_ = @Inject)
class HltvEventProvider implements Provider {

  private final HltvGrpcClient hltvGrpcClient;
  private final TargetRepository targetRepository;

  @Override
  public ProviderType providerType() {
    return ProviderType.HLTV;
  }

  @Override
  public List<EventPayload> fetchAll() {
    List<EventPayload> events = new ArrayList<>();

    var hltvTeams = targetRepository.listAllByProvider(providerType()).stream()
        .map(Target::getData)
        .filter(Objects::nonNull)
        .toList();

    for (var teamSourceData : hltvTeams) {
      var id = String.valueOf(teamSourceData.get("id"));
      var slug = String.valueOf(teamSourceData.get("slug"));

      List<HltvMatch> matchResponses = fetchMatchesByTeamId(id, slug);

      var teamMatches = matchResponses.stream()
          .filter(Objects::nonNull)
          .map(this::from)
          .toList();
      events.addAll(teamMatches);
    }

    return events;
  }

  private List<HltvMatch> fetchMatchesByTeamId(String teamId, String slug) {
    try {
      SyncMatchesResponse response = hltvGrpcClient.syncMatches(teamId, slug);
      return response.getMatchesList();
    } catch (Exception exception) {
      log.error("Can't fetch matches by team id: {}. Error message: {}",
          teamId, exception.getMessage(), exception);
      return Collections.emptyList();
    }
  }

  private EventPayload from(HltvMatch hltvMatch) {
    if (hltvMatch == null) {
      return null;
    }

    var eventName =
        parseTeamName(hltvMatch.getTeam1()) + " vs " + parseTeamName(hltvMatch.getTeam2());

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
  private String buildSourceId(HltvMatch eventPayload) {
    return providerType().name().toLowerCase().concat(":") +
        TargetType.MATCH.name().toLowerCase().concat(":") +
        eventPayload.getMatchId();
  }

  private String parseTeamName(HltvTeamBrief team) {
    return Optional.ofNullable(team)
        .map(HltvTeamBrief::getName)
        .orElse("TBD");
  }

  private List<TargetPayload> parseTargets(HltvMatch hltvMatch) {
    if (hltvMatch == null) {
      return List.of();
    }

    List<TargetPayload> targets = new ArrayList<>();
    HltvTeamBrief team1 = hltvMatch.getTeam1();
    if (team1 != null) {
      targets.add(TargetPayload.builder()
          .sourceId(buildTargetId(team1))
          .name(team1.getName())
          .type(TargetType.TEAM)
          .data(Map.of("id", team1.getId(), "slug", team1.getSlug()))
          .build());
    }
    HltvTeamBrief team2 = hltvMatch.getTeam2();
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
  private String buildTargetId(HltvTeamBrief hltvMatch) {
    return "hltv".concat(":").concat("team").concat(":").concat(String.valueOf(hltvMatch.getId()));
  }

  private String parseDetails(HltvMatch hltvMatch) {
    var firstTeamName = parseTeamName(hltvMatch.getTeam1());
    var secondTeamName = parseTeamName(hltvMatch.getTeam2());
    return String.format("""
            Match: %s vs %s
            Tournament: %s
            Format: Best of %d
            Match Page: %s
            Event Page: %s
            """,
        firstTeamName,
        secondTeamName,
        hltvMatch.getEventName(),
        hltvMatch.getBestOf(),
        hltvMatch.getMatchUrl(),
        hltvMatch.getEventUrl()
    );
  }

  private DateRange calculateEventDuration(HltvMatch hltvMatch) {
    //todo: calculate startAt and endAt based on hltvMatch.startAt and bestOf
    // respect zone
    return new DateRange(Instant.now(), Instant.now());
  }

}
