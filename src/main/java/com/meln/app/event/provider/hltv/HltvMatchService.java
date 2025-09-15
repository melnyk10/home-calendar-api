package com.meln.app.event.provider.hltv;

import com.meln.app.common.error.CustomException.CustomBadRequestException;
import com.meln.app.common.error.ErrorMessage;
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
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;

@Slf4j
@ApplicationScoped
@AllArgsConstructor(onConstructor_ = @Inject)
class HltvMatchService {

  private final HltvMatchRepository hltvMatchRepository;
  private final HltvMatchClient hltvMatchClient;

  protected List<HltvMatch> listByTeamId(Collection<ObjectId> teamIds) {
    if (teamIds == null || teamIds.isEmpty()) {
      return new ArrayList<>();
    }
    return hltvMatchRepository.findAllFeaturesMatchesByTeamIdIn(teamIds);
  }

  protected void syncMatches(Collection<HltvTeam> teams) {
    List<HltvMatchResponse> response;
    try {
      response = hltvMatchClient.syncMatches(teams);
    } catch (Exception exception) {
      log.error("HLTV match sync failed during external fetch: {}", exception.getMessage());
      throw new CustomBadRequestException(
          ErrorMessage.Hltv.Code.HLTV_MATCHES_FETCH_FAILED,
          ErrorMessage.Hltv.Message.HLTV_MATCHES_FETCH_FAILED,
          exception
      );
    }

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
      hltvMatchRepository.bulkUpsert(hltvMatches);
    } catch (Exception exception) {
      log.error("Unable to save {} HLTV matches. Cause: {}",
          hltvMatches.size(), exception.getMessage());
      throw new CustomBadRequestException(
          ErrorMessage.Hltv.Code.HLTV_MATCHES_SAVE_FAILED,
          ErrorMessage.Hltv.Message.HLTV_MATCHES_SAVE_FAILED(hltvMatches.size()),
          exception);
    }
  }
}
