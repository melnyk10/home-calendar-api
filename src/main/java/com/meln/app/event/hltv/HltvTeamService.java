package com.meln.app.event.hltv;

import com.meln.app.event.hltv.model.HltvTeam;
import com.meln.app.event.hltv.model.HltvTeamResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;

@ApplicationScoped
@AllArgsConstructor(onConstructor_ = @Inject)
public class HltvTeamService {

  private final HltvTeamRepo hltvTeamRepo;
  private final HltvTeamClient hltvTeamClient;

  protected HltvTeam getById(ObjectId id) {
    return hltvTeamRepo.findById(id);
  }

  protected List<HltvTeam> syncTeams() {
    List<HltvTeamResponse> response = hltvTeamClient.syncTeams();

    List<HltvTeam> hltvTeams = response.stream()
        .map(HltvConverter::from)
        .toList();

    return saveOrUpdate(hltvTeams);
  }

  private List<HltvTeam> saveOrUpdate(List<HltvTeam> hltvTeams) {
    try {
      hltvTeamRepo.bulkUpsert(hltvTeams);
      List<String> teamSourceIds = hltvTeams.stream().map(HltvTeam::getSourceId).toList();
      return hltvTeamRepo.findAllBySourceId(teamSourceIds);
    } catch (Exception e) {
      throw new RuntimeException(e); //todo: add appropriate exception
    }
  }
}
