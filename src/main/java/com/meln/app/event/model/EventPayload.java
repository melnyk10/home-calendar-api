package com.meln.app.event.model;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import lombok.Builder;

@Builder
public record EventPayload(
    String provider,           // "hltv" | "sonarr"
    String eventType,          // "match.scheduled", "episode.released", ...
    Instant occurredAt,
    Instant receivedAt,
    String status,             // "scheduled" | "completed" | ...
    String privacy,            // optional
    SubjectPayload subject,
    Map<String, Object> payload,
    List<String> tags,
    String idempotencyKey      // if provider gives one; else we’ll compute
) {

  @Builder
  public record SubjectPayload(
      String type,           // "tournament" | "tv_show" | ...
      String id,
      // provider-scoped external id/slug (e.g. "hltv:event:iem-katowice-2025")
      String name,
      Map<String, Object> externalRefs
  ) {

  }

  public static EventPayload toPayload(Event event) {
    if (event == null) {
      return null;
    }

    return EventPayload.builder()
        .provider(event.getProvider() != null ? event.getProvider().getName() : null)
        .eventType(event.getType())
        .occurredAt(event.getStartAt())
        .receivedAt(event.getCreatedAt())
        .status("scheduled") // or derive dynamically
        .privacy(null)
        .subject(toSubjectPayload(event.getSubject()))
        .payload(event.getPayload())
        .tags(List.of())
        .idempotencyKey(event.getHash())
        .build();
  }

  private static EventPayload.SubjectPayload toSubjectPayload(Subject subject) {
    if (subject == null) {
      return null;
    }
    return EventPayload.SubjectPayload.builder()
        .type(subject.getType())
        .id(subject.getId().toString())
        .name(subject.getName())
        .build();
  }
}
