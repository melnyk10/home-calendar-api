<<<<<<<< HEAD:src/main/java/com/meln/app/event/provider/hltv/HltvTeamService.java
package com.meln.app.event.provider.hltv;
========
package com.meln.app.event.hltv;
>>>>>>>> 4626548 (move files):src/main/java/com/meln/app/event/hltv/HltvTeamService.java

import com.meln.app.event.provider.hltv.model.HltvTeam;
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
    var response = hltvTeamClient.syncTeams();

    var hltvTeams = response.stream()
        .map(HltvConverter::from)
        .toList();

    return saveOrUpdate(hltvTeams);
  }

  private List<HltvTeam> saveOrUpdate(List<HltvTeam> hltvTeams) {
    try {
      hltvTeamRepo.bulkUpsert(hltvTeams);
      var teamSourceIds = hltvTeams.stream().map(HltvTeam::getSourceId).toList();
      return hltvTeamRepo.findAllBySourceId(teamSourceIds);
    } catch (Exception e) {
      throw new RuntimeException(e); //todo: add appropriate exception
    }
  }
}
