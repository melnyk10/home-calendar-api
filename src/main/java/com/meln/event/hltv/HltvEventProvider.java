package com.meln.event.hltv;

import com.meln.common.event.EventDto;
import com.meln.common.event.EventProvider;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;

import java.util.List;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class HltvEventProvider implements EventProvider<CriteriaHltv> {
    private final HltvTeamService hltvTeamService;
    private final HltvMatchService hltvMatchService;

    @Override
    public Class<CriteriaHltv> criteriaType() {
        return CriteriaHltv.class;
    }

    @Override
    public List<EventDto> fetch(CriteriaHltv criteria) {
        List<HltvMatch> matches = hltvMatchService.getAllByTeamId(criteria.getTeamIds());
        return matches.stream()
                .map(this::from)
                .toList();
    }

    private EventDto from(HltvMatch hltvMatch) {
        //todo: improve! on each event creation we will query DB
        HltvTeam team1 = hltvTeamService.getById(hltvMatch.getTeam1Id());
        HltvTeam team2 = hltvTeamService.getById(hltvMatch.getTeam2Id());
        return HltvMatchConverter.from(hltvMatch, team1, team2);
    }
}
