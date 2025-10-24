package com.meln.app.event.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ProviderType {
  HLTV("Hltv.org"),
  SONARR("Sonarr");

  @Getter
  private final String displayName;
}
