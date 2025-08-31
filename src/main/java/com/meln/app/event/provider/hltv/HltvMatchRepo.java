package com.meln.app.event.provider.hltv;

import com.meln.app.event.provider.hltv.model.HltvMatch;
import com.mongodb.client.model.BulkWriteOptions;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

@ApplicationScoped
@AllArgsConstructor(onConstructor_ = @Inject)
public class HltvMatchRepo implements PanacheMongoRepository<HltvMatch> {

  public List<HltvMatch> findAllFeaturesMatchesByTeamIdIn(Collection<ObjectId> teamIds) {
    String query = "%s in ?1 or %s in ?1 and %s >= ?2"
        .formatted(HltvMatch.COL_TEAM1_ID, HltvMatch.COL_TEAM2_ID, HltvMatch.COL_STARTS_AT);
    return find(query, teamIds, Instant.now()).list();
  }

  public void bulkUpsert(List<HltvMatch> matches) {
    List<UpdateOneModel<HltvMatch>> writes = matches.stream()
        .filter(match -> match.getMatchId() != null)
        .map(match -> {
          Instant now = Instant.now();

          Bson filter = Filters.eq(HltvMatch.COL_MATCH_ID, match.getMatchId());

          List<Bson> sets = new ArrayList<>();
          Optional.ofNullable(match.getMatchUrl())
              .ifPresent(v -> sets.add(Updates.set(HltvMatch.COL_MATCH_URL, v)));
          Optional.ofNullable(match.getStartsAt())
              .ifPresent(v -> sets.add(Updates.set(HltvMatch.COL_STARTS_AT, v)));
          Optional.ofNullable(match.getScore1())
              .ifPresent(v -> sets.add(Updates.set(HltvMatch.COL_SCORE1, v)));
          Optional.ofNullable(match.getScore2())
              .ifPresent(v -> sets.add(Updates.set(HltvMatch.COL_SCORE2, v)));
          Optional.ofNullable(match.getTeam1Id())
              .ifPresent(v -> sets.add(Updates.set(HltvMatch.COL_TEAM1_ID, v)));
          Optional.ofNullable(match.getTeam2Id())
              .ifPresent(v -> sets.add(Updates.set(HltvMatch.COL_TEAM2_ID, v)));

          Bson setStage = Updates.combine(sets.toArray(new Bson[0]));
          Bson update = Updates.combine(setStage);

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
