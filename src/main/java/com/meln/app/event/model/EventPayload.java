package com.meln.app.event.model;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.Builder;

@Builder
public record EventPayload(
    String id,
    String sourceId,
    String calendarEventSourceId, //todo: move to another obj ?
    String name,
    String details,
    String url,
    boolean isAllDay,
    Instant startAt,
    Instant endAt,
    ZoneId zone,
    ProviderType provider,
    TargetType type,
    List<TargetPayload> targets
) {

  @Builder
  public record TargetPayload(
      String id,
      String sourceId,
      TargetType type,
      String name,
      Map<String, Object> data,
      ProviderType provider
  ) {

  }

  public static EventPayload from(Event event) {
    if (event == null) {
      return null;
    }

    return EventPayload.builder()
        .id(Optional.of(event).map(Event::getId).map(String::valueOf).orElse(null))
        .sourceId(event.getSourceId())
        .provider(event.getProvider() != null ? event.getProvider().getType() : null)
        .provider(Optional.of(event).map(Event::getProvider).map(Provider::getType).orElse(null))
        .type(event.getType())
        .name(event.getName())
        .details(event.getDetails())
        .targets(event.getTargets().stream().map(EventPayload::from).toList())
        .isAllDay(event.isAllDay())
        .startAt(event.getStartAt())
        .endAt(event.getEndAt())
        .build();
  }

  private static TargetPayload from(EventTarget target) {
    if (target == null) {
      return null;
    }

    return TargetPayload.builder()
        .id(Optional.of(target).map(EventTarget::getId).map(String::valueOf).orElse(null))
        .sourceId(target.getSourceId())
        .type(target.getType())
        .name(target.getName())
        .data(target.getData())
        .build();
  }
}
