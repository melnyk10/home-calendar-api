package com.meln.app.event.provider.hltv;

import com.meln.app.event.provider.hltv.model.HltvMatch;
import com.meln.app.event.provider.hltv.model.HltvMatchResponse;
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
    List<HltvMatchResponse> hltvMatchResponses = hltvMatchClient.syncMatches(teams);

    Map<String, HltvTeam> teamById = teams.stream()
        .collect(Collectors.toMap(HltvTeam::getSourceId, team -> team));

    List<HltvMatch> hltvMatches = hltvMatchResponses.stream()
        .map(match -> {
          HltvTeam team1 = teamById.get(match.getTeam1Id());
          HltvTeam team2 = teamById.getOrDefault(match.getTeam2Id(), null);
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
