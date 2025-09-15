package com.meln.app.event.provider.hltv;

import com.meln.app.common.error.CustomException.CustomBadRequestException;
import com.meln.app.common.error.ErrorMessage;
import com.meln.app.event.provider.hltv.model.HltvTeam;
import com.meln.app.event.provider.hltv.model.HltvTeamResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;

@Slf4j
@ApplicationScoped
@AllArgsConstructor(onConstructor_ = @Inject)
public class HltvTeamService {

  private final HltvTeamRepository hltvTeamRepository;
  private final HltvTeamClient hltvTeamClient;

  protected HltvTeam findById(ObjectId id) {
    return hltvTeamRepository.findById(id);
  }

  protected List<HltvTeam> syncTeams() {
    List<HltvTeamResponse> response;
    try {
      response = hltvTeamClient.syncTeams();
    } catch (Exception exception) {
      log.error("HLTV sync failed during external fetch: {}", exception.getMessage());
      throw new CustomBadRequestException(
          ErrorMessage.Hltv.Code.HLTV_TEAMS_FETCH_FAILED,
          ErrorMessage.Hltv.Message.HLTV_TEAMS_FETCH_FAILED,
          exception
      );
    }

    var hltvTeams = response.stream()
        .map(HltvConverter::from)
        .toList();

    return saveOrUpdate(hltvTeams);
  }

  private List<HltvTeam> saveOrUpdate(List<HltvTeam> hltvTeams) {
    try {
      hltvTeamRepository.bulkUpsert(hltvTeams);
      var teamSourceIds = hltvTeams.stream().map(HltvTeam::getSourceId).toList();
      return hltvTeamRepository.findAllBySourceId(teamSourceIds);
    } catch (Exception exception) {
      log.error("Unable to save {} HLTV teams. Cause: {}", hltvTeams.size(), exception.getMessage());
      throw new CustomBadRequestException(
          ErrorMessage.Hltv.Code.HLTV_TEAMS_SAVE_FAILED,
          ErrorMessage.Hltv.Message.HLTV_TEAMS_SAVE_FAILED(hltvTeams.size()),
          exception);
    }
  }
}
