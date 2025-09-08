package com.meln.infra.db.changelog;

import com.mongodb.client.MongoCollection;
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
import java.util.Map;
import org.bson.Document;
import org.bson.types.ObjectId;

//todo: inserting test data. Should be run only for local/dev env but not on prod. Fix me in future!
@ChangeUnit(id = "init-data", order = "002", author = "system", systemVersion = "1")
public class V002_InitialDataMigration {

  private static final String COL_USERS = "users";
  private static final String COL_HLTV_TEAM = "hltvTeam";
  private static final String COL_HLTV_MATCH = "hltvMatch";
  private static final String COL_SUBSCRIPTIONS = "subscriptions";

  @Execution
  public void seed(MongoDatabase db) {
    users(db);

    ObjectId teamId = seedTeam(db);
    seedMatches(db, teamId);

    seedHltvSubscription(db, "o.melnyk10@gmail.com", "natus-vincere");
    addCalendar(db, "o.melnyk10@gmail.com");
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
        .append("logoUrl", "https://hltv.org/img/static/team/navi.png")
        .append("teamId", "4608")
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
      Document filter = new Document("matchId", matchId);

      Instant eventStartDate = Instant.now()
          .plus(i, ChronoUnit.DAYS)
          .atZone(ZoneOffset.UTC)
          .plusYears(5)
          .toInstant();
      Document matchDoc = new Document("event_name", "IEM Katowice " + i)
          .append("eventUrl", "https://hltv.org/events/" + (2000 + i) + "/iem-katowice-" + i)
          .append("matchId", matchId)
          .append("matchUrl",
              "https://hltv.org/matches/" + (3000 + i) + "/natus-vincere-vs-team" + i)
          .append("startsAt", eventStartDate)
          .append("team1Id", teamId)
          .append("team2Id", null)
          .append("score1", 16)
          .append("score2", 10 + i)
          .append("bestOf", 3);

      db.getCollection(COL_HLTV_MATCH)
          .updateOne(filter,
              new Document("$setOnInsert", matchDoc),
              new UpdateOptions().upsert(true));
    }
  }

  private void seedHltvSubscription(MongoDatabase db, String userEmail, String teamKey) {
    ObjectId userId = findUserByEmail(db, userEmail).id();
    ObjectId teamId = findTeamObjectId(db, teamKey);

    Document subDoc = new Document("userId", userId)
        .append("provider", "HLTV")
        .append("active", true)
        .append("criteria", new Document("_type", "hltv")
            .append("teamIds", List.of(teamId)));

    Document filter = new Document("userId", userId)
        .append("provider", "HLTV")
        .append("criteria._type", "hltv")
        .append("criteria.teamIds", new Document("$all", List.of(teamId)));

    db.getCollection(COL_SUBSCRIPTIONS)
        .updateOne(filter,
            new Document("$setOnInsert", subDoc),
            new UpdateOptions().upsert(true));
  }

  private UserRef findUserByEmail(MongoDatabase db, String email) {
    Document u = db.getCollection(COL_USERS)
        .find(new Document("email", email))
        .projection(Projections.include("_id", "email"))
        .first();
    if (u == null) {
      throw new IllegalStateException("User not found: " + email);
    }
    return new UserRef(u.getObjectId("_id"), u.getString("email"));
  }

  public record UserRef(ObjectId id, String email) {}

  private ObjectId findTeamObjectId(MongoDatabase db, String teamKey) {
    Document team = db.getCollection(COL_HLTV_TEAM)
        .find(new Document("$or", List.of(
            new Document("teamId", teamKey),
            new Document("slug", teamKey),
            new Document("teamName", teamKey)
        )))
        .projection(Projections.include("_id"))
        .first();
    if (team == null) {
      throw new IllegalStateException("Team not found: " + teamKey);
    }
    return team.getObjectId("_id");
  }

  private void addCalendar(MongoDatabase db, String userEmail) {
    UserRef user = findUserByEmail(db, userEmail);

    MongoCollection<Document> collection = db.getCollection("calendar");

    Document properties = new Document(Map.of(
        "_type", "google",
        "userEmail", user.email(),
        "calendarId", "d27cfb6cf4b09045227b9d431b67571128c2b6e1122053cb518fe1739b081574@group.calendar.google.com"
    ));

    Document calendar = new Document()
        .append("userId", user.id())
        .append("sourceId", "d27cfb6cf4b09045227b9d431b67571128c2b6e1122053cb518fe1739b081574@group.calendar.google.com")
        .append("properties", properties);

    collection.insertOne(calendar);
  }

  @RollbackExecution
  public void rollback(MongoDatabase db) {
    db.getCollection(COL_USERS).deleteOne(new Document("email", "o.melnyk10@gmail.com"));
    db.getCollection(COL_SUBSCRIPTIONS).deleteMany(new Document("provider", "HLTV"));
  }
}