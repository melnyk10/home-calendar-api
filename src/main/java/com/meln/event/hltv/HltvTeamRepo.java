package com.meln.event.hltv;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.model.WriteModel;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class HltvTeamRepo implements PanacheMongoRepository<HltvTeam> {
    private final MongoClient mongoClient;

    public void bulkUpsert(List<HltvTeam> teams) {
        List<WriteModel<HltvTeam>> hltvWrites = teams.stream()
                .map(team -> new UpdateOneModel<HltvTeam>(
                        Filters.eq("team_id", team.getTeamId()),
                        Updates.combine(
                                Updates.set("slug", team.getSlug()),
                                Updates.set("team_name", team.getTeamName()),
                                Updates.set("logo_url", team.getLogoUrl()),
                                Updates.set("rank", team.getRank())
                        ),
                        new UpdateOptions().upsert(true)
                ))
                .collect(Collectors.<WriteModel<HltvTeam>>toList());

        getCollection().bulkWrite(hltvWrites);
    }

    //todo: investigate why MongoCollection<Hltv> not working
    private MongoCollection<HltvTeam> getCollection() {
        return mongoClient.getDatabase("home-calendar-db").getCollection("hltv_team", HltvTeam.class);
    }
}
