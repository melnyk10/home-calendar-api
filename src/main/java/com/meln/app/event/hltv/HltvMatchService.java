<<<<<<<< HEAD:src/main/java/com/meln/app/event/provider/hltv/HltvMatchService.java
package com.meln.app.event.provider.hltv;
========
package com.meln.app.event.hltv;
>>>>>>>> 4626548 (move files):src/main/java/com/meln/app/event/hltv/HltvMatchService.java

import com.meln.app.event.provider.hltv.model.HltvMatch;
import com.meln.app.event.provider.hltv.model.HltvTeam;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;

@ApplicationScoped
@AllArgsConstructor(onConstructor_ = @Inject)
public class HltvMatchService {

  private final HltvMatchRepo hltvMatchRepo;
  private final HltvMatchClient hltvMatchClient;

  protected List<HltvMatch> getAllByTeamId(Collection<ObjectId> teamIds) {
    if (teamIds == null || teamIds.isEmpty()) {
      return new ArrayList<>();
    }
    return hltvMatchRepo.findAllFeaturesMatchesByTeamIdIn(teamIds);
  }

  protected void syncMatches(Collection<HltvTeam> teams) {
    var response = hltvMatchClient.syncMatches(teams);

    Map<String, HltvTeam> teamById = teams.stream()
        .collect(Collectors.toMap(HltvTeam::getSourceId, team -> team));

    var hltvMatches = response.stream()
        .map(match -> {
          var team1 = teamById.get(match.getTeam1Id());
          var team2 = teamById.getOrDefault(match.getTeam2Id(), null);
          return HltvConverter.from(match, team1, team2);
        })
        .toList();

    saveOrUpdate(hltvMatches);
  }

  private void saveOrUpdate(List<HltvMatch> hltvMatches) {
    try {
      hltvMatchRepo.bulkUpsert(hltvMatches);
    } catch (Exception e) {
      throw new RuntimeException(e); //todo: add appropriate exception
    }
  }
}
