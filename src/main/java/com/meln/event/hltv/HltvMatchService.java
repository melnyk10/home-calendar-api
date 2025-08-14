package com.meln.event.hltv;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class HltvMatchService {
    private final HltvMatchRepo hltvMatchRepo;
    private final HltvMatchClient hltvMatchClient;

    protected List<HltvMatch> getAllByTeamId(Collection<ObjectId> teamIds) {
        return hltvMatchRepo.findByTeam1IdIn(teamIds);
    }

    protected List<HltvMatch> syncMatches(Collection<HltvTeam> teams) {
        List<HltvMatchResponse> hltvMatchResponses = hltvMatchClient.syncMatches(teams);

        Map<String, HltvTeam> teamById = teams.stream()
                .collect(Collectors.toMap(HltvTeam::getTeamId, team -> team));

        List<HltvMatch> hltvMatches = hltvMatchResponses.stream()
                .map(match -> {
                    HltvTeam team1 = teamById.get(match.getTeam1());
                    HltvTeam team2 = teamById.getOrDefault(match.getTeam2(), null);
                    return HltvMatchConverter.from(match, team1, team2);
                })
                .toList();

        return saveAll(hltvMatches);
    }

    private List<HltvMatch> saveAll(List<HltvMatch> hltvMatches) {
        try {
            hltvMatchRepo.bulkUpsert(hltvMatches);
            return hltvMatches;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
