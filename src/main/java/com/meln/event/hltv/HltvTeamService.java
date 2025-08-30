package com.meln.event.hltv;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class HltvTeamService {

  private final HltvTeamRepo hltvTeamRepo;
  private final HltvTeamClient hltvTeamClient;

  protected HltvTeam getById(ObjectId id) {
    return hltvTeamRepo.findById(id);
  }

  protected List<HltvTeam> syncTeams() {
    List<HltvTeamResponse> teamsResponse = hltvTeamClient.syncTeams();

    List<HltvTeam> hltvTeams = teamsResponse.stream()
        .map(HltvTeamConverter::from)
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
