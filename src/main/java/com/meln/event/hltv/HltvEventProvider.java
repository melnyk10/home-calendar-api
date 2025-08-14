package com.meln.event.hltv;

import com.meln.event.CriteriaHltv;
import com.meln.event.Event;
import com.meln.event.EventProvider;
import com.meln.event.Provider;
import com.meln.event.Subscription;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;

import java.util.List;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class HltvEventProvider implements EventProvider {
    private final HltvTeamService hltvTeamService;
    private final HltvMatchService hltvMatchService;

    @Override
    public Provider provider() {
        return Provider.HLTV;
    }

    @Override
    public List<Event> fetch(Subscription sub) {
        if (sub.getCriteria() instanceof CriteriaHltv(List<ObjectId> teamIds)) {
            List<HltvMatch> matches = hltvMatchService.getAllByTeamId(teamIds);
            return matches.stream()
                    .map(this::from)
                    .toList();
        }
        throw new IllegalArgumentException("HLTV subscription has non-HLTV criteria");
    }

    private Event from(HltvMatch hltvMatch) {
        //todo: improve! on each event creation we will query DB
        HltvTeam team1 = hltvTeamService.getById(hltvMatch.getTeam1Id());
        HltvTeam team2 = hltvTeamService.getById(hltvMatch.getTeam2Id());
        return HltvMatchConverter.from(hltvMatch, team1, team2);
    }
}
