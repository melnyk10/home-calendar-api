package com.meln.infra.db.changelog;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.UpdateOptions;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.bson.Document;
import org.bson.types.ObjectId;

//todo: inserting test data. Should be run only for local/dev env but not on prod. Fix me in future!
@ChangeUnit(id = "init-data", order = "002", author = "system", systemVersion = "1")
public class V002_InitialDataMigration {

  private static final String COL_USERS = "users";
  private static final String COL_HLTV_TEAM = "hltv_team";
  private static final String COL_HLTV_MATCH = "hltv_match";
  private static final String COL_SUBSCRIPTIONS = "subscriptions";

  @Execution
  public void seed(MongoDatabase db) {
    users(db);

    ObjectId teamId = seedTeam(db);
    seedMatches(db, teamId);

    seedHltvSubscription(db, "o.melnyk10@gmail.com", "natus-vincere");
  }

  private void users(MongoDatabase db) {
    Document seed = new Document("email", "o.melnyk10@gmail.com")
        .append("firstName", "Sam")
        .append("lastName", "Porter-Bridge");

    db.getCollection(COL_USERS).updateOne(
        new Document("email", seed.getString("email")),
        new Document("$setOnInsert", seed),
        new com.mongodb.client.model.UpdateOptions().upsert(true)
    );
  }

  private ObjectId seedTeam(MongoDatabase db) {
    Document filter = new Document("team_id", "4608");
    Document teamDoc = new Document("team_name", "Natus Vincere")
        .append("slug", "natus-vincere")
        .append("logo_url", "https://hltv.org/img/static/team/navi.png")
        .append("team_id", "4608")
        .append("rank", 1);

    db.getCollection(COL_HLTV_TEAM)
        .updateOne(filter,
            new Document("$setOnInsert", teamDoc),
            new UpdateOptions().upsert(true));

    Document t = db.getCollection(COL_HLTV_TEAM)
        .find(filter)
        .projection(Projections.include("_id"))
        .first();
    return t.getObjectId("_id");
  }

  private void seedMatches(MongoDatabase db, ObjectId teamId) {
    int matchCounter = 1;
    for (int i = 1; i <= 3; i++) { // insert 3 matches as example
      String matchId = String.valueOf(matchCounter++);
      Document filter = new Document("match_id", matchId);

      Instant eventStartDate = Instant.now()
          .plus(i, ChronoUnit.DAYS)
          .atZone(ZoneOffset.UTC)
          .plusYears(5)
          .toInstant();
      Document matchDoc = new Document("event_name", "IEM Katowice " + i)
          .append("event_url", "https://hltv.org/events/" + (2000 + i) + "/iem-katowice-" + i)
          .append("match_id", matchId)
          .append("match_url",
              "https://hltv.org/matches/" + (3000 + i) + "/natus-vincere-vs-team" + i)
          .append("date_time", eventStartDate)
          .append("team1_id", teamId)
          .append("team2_id", null)
          .append("score1", 16)
          .append("score2", 10 + i)
          .append("best_of", 3);

      db.getCollection(COL_HLTV_MATCH)
          .updateOne(filter,
              new Document("$setOnInsert", matchDoc),
              new UpdateOptions().upsert(true));
    }
  }

  private void seedHltvSubscription(MongoDatabase db, String userEmail, String teamKey) {
    ObjectId userId = findUserIdByEmail(db, userEmail);
    ObjectId teamId = findTeamObjectId(db, teamKey);

    Document subDoc = new Document("user_id", userId)
        .append("provider", "HLTV")
        .append("active", true)
        .append("criteria", new Document("_type", "hltv")
            .append("team_ids", List.of(teamId)));

    Document filter = new Document("user_id", userId)
        .append("provider", "HLTV")
        .append("criteria._type", "hltv")
        .append("criteria.team_ids", new Document("$all", List.of(teamId)));

    db.getCollection(COL_SUBSCRIPTIONS)
        .updateOne(filter,
            new Document("$setOnInsert", subDoc),
            new UpdateOptions().upsert(true));
  }

  private ObjectId findUserIdByEmail(MongoDatabase db, String email) {
    Document u = db.getCollection(COL_USERS)
        .find(new Document("email", email))
        .projection(Projections.include("_id"))
        .first();
    if (u == null) {
      throw new IllegalStateException("User not found: " + email);
    }
    return u.getObjectId("_id");
  }

  private ObjectId findTeamObjectId(MongoDatabase db, String teamKey) {
    Document team = db.getCollection(COL_HLTV_TEAM)
        .find(new Document("$or", List.of(
            new Document("team_id", teamKey),
            new Document("slug", teamKey),
            new Document("team_name", teamKey)
        )))
        .projection(Projections.include("_id"))
        .first();
    if (team == null) {
      throw new IllegalStateException("Team not found: " + teamKey);
    }
    return team.getObjectId("_id");
  }

  @RollbackExecution
  public void rollback(MongoDatabase db) {
    db.getCollection(COL_USERS).deleteOne(new Document("email", "o.melnyk10@gmail.com"));
    db.getCollection(COL_SUBSCRIPTIONS).deleteMany(new Document("provider", "HLTV"));
  }
}