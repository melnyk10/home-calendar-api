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

  public Optional<String> findByState(String state) {
    Entry entry = store.remove(state);
    if (entry == null || System.nanoTime() > entry.expiresAtNanos) {
      return Optional.empty();
    }
    return Optional.of(entry.userId);
  }

  private record Entry(String userId, long expiresAtNanos) {

  }
}
