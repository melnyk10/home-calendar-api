package com.meln.event.hltv;

import com.mongodb.client.model.BulkWriteOptions;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class HltvMatchRepo implements PanacheMongoRepository<HltvMatch> {
    public List<HltvMatch> findByTeam1IdIn(Collection<ObjectId> teamIds) {
        return find("team1_id in ?1", teamIds).list();
    }

    public void bulkUpsert(List<HltvMatch> matches) {
        List<UpdateOneModel<HltvMatch>> writes = matches.stream()
                .filter(m -> m.getMatchId() != null)
                .map(m -> {
                    Instant now = Instant.now();

                    Bson filter = Filters.eq("match_id", m.getMatchId());

                    List<Bson> sets = new ArrayList<>();
                    Optional.ofNullable(m.getMatchUrl()).ifPresent(v -> sets.add(Updates.set("match_url", v)));
                    Optional.ofNullable(m.getStartsAt()).ifPresent(v -> sets.add(Updates.set("starts_at", v)));
                    Optional.ofNullable(m.getScore1()).ifPresent(v -> sets.add(Updates.set("score1", v)));
                    Optional.ofNullable(m.getScore2()).ifPresent(v -> sets.add(Updates.set("score2", v)));
                    Optional.ofNullable(m.getTeam1Id()).ifPresent(v -> sets.add(Updates.set("team1_id", v)));
                    Optional.ofNullable(m.getTeam2Id()).ifPresent(v -> sets.add(Updates.set("team2_id", v)));
                    sets.add(Updates.set("updated_at", now));

                    Bson setStage = Updates.combine(sets.toArray(new Bson[0]));
                    Bson setOnInsertStage = Updates.setOnInsert("created_at", now);
                    Bson update = Updates.combine(setStage, setOnInsertStage);

                    return new UpdateOneModel<HltvMatch>(
                            filter,
                            update,
                            new UpdateOptions().upsert(true)
                    );
                })
                .toList();

        if (!writes.isEmpty()) {
            mongoCollection().bulkWrite(writes, new BulkWriteOptions().ordered(false));
        }
    }

}
