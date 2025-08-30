<<<<<<<< HEAD:src/main/java/com/meln/app/event/provider/hltv/model/HltvTeamResponse.java
package com.meln.app.event.provider.hltv.model;
========
package com.meln.app.event.hltv;
>>>>>>>> 4626548 (move files):src/main/java/com/meln/app/event/hltv/HltvTeamResponse.java

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
  private Integer teamId;
  private String teamIdName;
}
