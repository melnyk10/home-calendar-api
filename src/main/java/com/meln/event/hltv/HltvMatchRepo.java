package com.meln.event.hltv;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.BulkWriteOptions;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.model.WriteModel;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class HltvMatchRepo implements PanacheMongoRepository<HltvMatch> {
    private final MongoClient mongoClient;

    public List<HltvMatch> findByTeam1IdIn(Collection<ObjectId> teamIds) {
        return find("team1_id in ?1", teamIds).list();
    }

    public void bulkUpsert(List<HltvMatch> matches) {
        List<WriteModel<HltvMatch>> writes = matches.stream()
                .filter(match -> match.getMatchId() != null)
                .map(m -> {
                    Bson filter = Filters.eq("matchId", m.getMatchId());
                    Bson update = Updates.combine(
                            Updates.set("matchUrl", m.getMatchUrl()),
                            Updates.set("dateTime", m.getStartsDatetime()),
                            Updates.set("score1", m.getScore1()),
                            Updates.set("score2", m.getScore2()),
                            Updates.set("team1Id", m.getTeam1Id()),
                            Updates.set("team2Id", m.getTeam2Id())
                    );
                    return new UpdateOneModel<HltvMatch>(
                            filter,
                            update,
                            new UpdateOptions().upsert(true)
                    );
                })
                .collect(Collectors.toList());

        if (!writes.isEmpty()) {
            getCollection().bulkWrite(writes, new BulkWriteOptions().ordered(false));
        }
    }

    //todo: investigate why MongoCollection<Hltv> not working
    private MongoCollection<HltvMatch> getCollection() {
        return mongoClient.getDatabase("home-calendar-db").getCollection("hltv_match", HltvMatch.class);
    }
}
