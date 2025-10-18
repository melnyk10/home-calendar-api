package com.meln.app.event.provider.hltv.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HltvTeamResponse {

  private Integer rank;
  private String name;
  private String logoUrl;
  private String teamId;
  private String teamIdName;
}
