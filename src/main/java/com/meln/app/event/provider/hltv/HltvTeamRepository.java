package com.meln.app.event.provider.hltv;

import com.meln.app.event.provider.hltv.model.HltvTeam;
import com.mongodb.client.model.BulkWriteOptions;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.bson.conversions.Bson;

@ApplicationScoped
@AllArgsConstructor(onConstructor_ = @Inject)
class HltvTeamRepository implements PanacheMongoRepository<HltvTeam> {

  public List<HltvTeam> findAllBySourceId(Collection<String> sourceIds) {
    return find(HltvTeam.COL_SOURCE_ID + " in ?1", sourceIds).list();
  }

  public void bulkUpsert(List<HltvTeam> teams) {
    List<UpdateOneModel<HltvTeam>> writes = teams.stream()
        .filter(t -> t.getSourceId() != null) // idempotency key required
        .map(t -> {
          Bson filter = Filters.eq(HltvTeam.COL_SOURCE_ID, t.getSourceId());

          List<Bson> sets = new ArrayList<>();
          Optional.ofNullable(t.getSlug())
              .ifPresent(v -> sets.add(Updates.set(HltvTeam.COL_SLUG, v)));
          Optional.ofNullable(t.getTeamName())
              .ifPresent(v -> sets.add(Updates.set(HltvTeam.COL_TEAM_NAME, v)));
          Optional.ofNullable(t.getLogoUrl())
              .ifPresent(v -> sets.add(Updates.set(HltvTeam.COL_LOGO_URL, v)));
          Optional.ofNullable(t.getRank())
              .ifPresent(v -> sets.add(Updates.set(HltvTeam.COL_RANK, v)));

          Bson setStage = Updates.combine(sets.toArray(new Bson[0]));
          Bson update = Updates.combine(setStage);

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
