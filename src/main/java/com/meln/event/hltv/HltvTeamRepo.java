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

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class HltvTeamRepo implements PanacheMongoRepository<HltvTeam> {
    public void bulkUpsert(List<HltvTeam> teams) {
        List<UpdateOneModel<HltvTeam>> writes = teams.stream()
                .filter(t -> t.getTeamId() != null) // idempotency key required
                .map(t -> {
                    Instant now = Instant.now();

                    Bson filter = Filters.eq("team_id", t.getTeamId());

                    List<Bson> sets = new ArrayList<>();
                    Optional.ofNullable(t.getSlug()).ifPresent(v -> sets.add(Updates.set("slug", v)));
                    Optional.ofNullable(t.getTeamName()).ifPresent(v -> sets.add(Updates.set("team_name", v)));
                    Optional.ofNullable(t.getLogoUrl()).ifPresent(v -> sets.add(Updates.set("logo_url", v)));
                    Optional.ofNullable(t.getRank()).ifPresent(v -> sets.add(Updates.set("rank", v)));
                    sets.add(Updates.set("updated_at", now));

                    Bson setStage = Updates.combine(sets.toArray(new Bson[0]));
                    Bson setOnInsertStage = Updates.setOnInsert("created_at", now);
                    Bson update = Updates.combine(setStage, setOnInsertStage);

                    return new UpdateOneModel<HltvTeam>(
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
