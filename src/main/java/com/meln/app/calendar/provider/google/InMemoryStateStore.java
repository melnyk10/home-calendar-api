package com.meln.app.calendar.provider.google;

import jakarta.enterprise.context.ApplicationScoped;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class InMemoryStateStore {

  private final Map<String, Entry> store = new ConcurrentHashMap<>();

  public String issue(String userId, Duration ttl) {
    String state = UUID.randomUUID().toString();
    store.put(state, new Entry(userId, System.nanoTime() + ttl.toNanos()));
    return state;
  }

  public Optional<String> consume(String state) {
    Entry e = store.remove(state);
    if (e == null || System.nanoTime() > e.expiresAtNanos) {
      return Optional.empty();
    }
    return Optional.of(e.userId);
  }

  private record Entry(String userId, long expiresAtNanos) {

  }
}
