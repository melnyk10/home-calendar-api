package com.meln.infra.db.changelog;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Collation;
import com.mongodb.client.model.CollationStrength;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.bson.Document;

@ChangeUnit(id = "init", order = "001", author = "system", systemVersion = "1")
public class V001_InitialMigration {

  private static final String COL_USERS = "users";
  private static final String COL_EVENTS = "events";
  private static final String COL_HLTV_TEAM = "hltv_team";
  private static final String COL_HLTV_MATCH = "hltv_match";
  private static final String COL_SUBSCRIPTIONS = "subscriptions";

  private static final String IDX_USERS_EMAIL_UNIQ = "uniq_users_email";
  private static final String IDX_EVENTS_PROVIDER_EXT_UNIQ = "uniq_events_provider_external_id";
  private static final String IDX_EVENTS_SUBSCRIPTION_START = "idx_events_subscription_start_at";
  private static final String IDX_EVENTS_SOURCE_START = "idx_events_source_start_at";
  private static final String IDX_EVENTS_START = "idx_events_start_at";
  private static final String IDX_TEAM_TEAMID_UNIQ = "uniq_hltv_team_team_id";
  private static final String IDX_MATCH_MATCHID_UNIQ = "uniq_hltv_match_match_id";
  private static final String IDX_MATCH_STARTS_AT = "idx_hltv_match_starts_at";
  private static final String IDX_MATCH_TEAM1 = "idx_hltv_match_team1_id";
  private static final String IDX_MATCH_TEAM2 = "idx_hltv_match_team2_id";
  private static final String IDX_SUB_USER = "idx_sub_user";
  private static final String IDX_SUB_USER_PROVIDER_ACTIVE = "idx_sub_user_provider_active";
  private static final String IDX_SUB_PROVIDER_ACTIVE = "idx_sub_provider_active";
  private static final String IDX_SUB_HLTV_PROVIDER_TEAM = "idx_sub_hltv_provider_team";

  private static final Collation CASE_INSENSITIVE =
      Collation.builder().locale("en").collationStrength(CollationStrength.SECONDARY).build();

  @Execution
  public void init(MongoDatabase db) {
    users(db);
    events(db);
    hltvTeam(db);
    hltvMatch(db);
    subscriptions(db);
  }

  private void users(MongoDatabase db) {
    db.getCollection(COL_USERS).createIndex(
        Indexes.ascending("email"),
        new IndexOptions()
            .name(IDX_USERS_EMAIL_UNIQ)
            .unique(true)
            .collation(CASE_INSENSITIVE)
    );
  }

  private void hltvTeam(MongoDatabase db) {
    db.getCollection(COL_HLTV_TEAM)
        .createIndex(Indexes.ascending("team_id"),
            new IndexOptions().unique(true).name(IDX_TEAM_TEAMID_UNIQ));
  }

  private void hltvMatch(MongoDatabase db) {
    var col = db.getCollection(COL_HLTV_MATCH);

    // Unique numeric match_id only (partial index)
    col.createIndex(
        Indexes.ascending("match_id"),
        new IndexOptions()
            .unique(true)
            .name(IDX_MATCH_MATCHID_UNIQ)
            .partialFilterExpression(new Document("match_id", new Document("$type", "string")))
    );

    // Common lookups/sorts
    col.createIndex(Indexes.ascending("starts_at"), new IndexOptions().name(IDX_MATCH_STARTS_AT));
    col.createIndex(Indexes.ascending("team1_id"), new IndexOptions().name(IDX_MATCH_TEAM1));
    col.createIndex(Indexes.ascending("team2_id"), new IndexOptions().name(IDX_MATCH_TEAM2));
  }

  private void events(MongoDatabase db) {
    var eventsCol = db.getCollection(COL_EVENTS);

    // Idempotency key
    eventsCol.createIndex(
        Indexes.compoundIndex(Indexes.ascending("provider"), Indexes.ascending("external_id")),
        new IndexOptions().unique(true).name(IDX_EVENTS_PROVIDER_EXT_UNIQ)
    );

    // Common queries: upcoming events per subscription/source + calendar views
    eventsCol.createIndex(Indexes.ascending("subscription_id", "start_at"),
        new IndexOptions().name(IDX_EVENTS_SUBSCRIPTION_START));
    eventsCol.createIndex(Indexes.ascending("event_source_id", "start_at"),
        new IndexOptions().name(IDX_EVENTS_SOURCE_START));
    eventsCol.createIndex(Indexes.ascending("start_at"),
        new IndexOptions().name(IDX_EVENTS_START));
  }

  private void subscriptions(MongoDatabase db) {
    var col = db.getCollection(COL_SUBSCRIPTIONS);

    col.createIndex(
        Indexes.ascending("user_id"),
        new IndexOptions().name(IDX_SUB_USER)
    );

    col.createIndex(
        Indexes.compoundIndex(
            Indexes.ascending("user_id"),
            Indexes.ascending("provider"),
            Indexes.ascending("active")
        ),
        new IndexOptions().name(IDX_SUB_USER_PROVIDER_ACTIVE)
    );

    col.createIndex(
        Indexes.compoundIndex(
            Indexes.ascending("provider"),
            Indexes.ascending("active")
        ),
        new IndexOptions().name(IDX_SUB_PROVIDER_ACTIVE)
    );

    col.createIndex(
        Indexes.compoundIndex(
            Indexes.ascending("provider"),
            Indexes.ascending("criteria._type"),
            Indexes.ascending("criteria.teamIds") // or "criteria.team_ids"
        ),
        new IndexOptions()
            .name(IDX_SUB_HLTV_PROVIDER_TEAM)
            .partialFilterExpression(
                new Document("criteria._type", "hltv")
                    .append("criteria.teamIds", new Document("$type", "objectId")) // or "team_ids"
            )
    );
  }

  @RollbackExecution
  public void rollback(MongoDatabase db) {
    // users
    try {
      db.getCollection(COL_USERS).dropIndex(IDX_USERS_EMAIL_UNIQ);
    } catch (Exception ignored) {
    }
    db.getCollection(COL_USERS).deleteOne(new Document("email", "o.melnyk10@gmail.com"));

    // events
    try {
      db.getCollection(COL_EVENTS).dropIndex(IDX_EVENTS_PROVIDER_EXT_UNIQ);
    } catch (Exception ignored) {
    }
    try {
      db.getCollection(COL_EVENTS).dropIndex(IDX_EVENTS_SUBSCRIPTION_START);
    } catch (Exception ignored) {
    }
    try {
      db.getCollection(COL_EVENTS).dropIndex(IDX_EVENTS_SOURCE_START);
    } catch (Exception ignored) {
    }
    try {
      db.getCollection(COL_EVENTS).dropIndex(IDX_EVENTS_START);
    } catch (Exception ignored) {
    }

    // hltv_team
    try {
      db.getCollection(COL_HLTV_TEAM).dropIndex(IDX_TEAM_TEAMID_UNIQ);
    } catch (Exception ignored) {
    }

    // hltv_match
    try {
      db.getCollection(COL_HLTV_MATCH).dropIndex(IDX_MATCH_MATCHID_UNIQ);
    } catch (Exception ignored) {
    }
    try {
      db.getCollection(COL_HLTV_MATCH).dropIndex(IDX_MATCH_STARTS_AT);
    } catch (Exception ignored) {
    }
    try {
      db.getCollection(COL_HLTV_MATCH).dropIndex(IDX_MATCH_TEAM1);
    } catch (Exception ignored) {
    }
    try {
      db.getCollection(COL_HLTV_MATCH).dropIndex(IDX_MATCH_TEAM2);
    } catch (Exception ignored) {
    }
    try {
      db.getCollection(COL_SUBSCRIPTIONS).dropIndex(IDX_SUB_USER);
    } catch (Exception ignored) {
    }
    try {
      db.getCollection(COL_SUBSCRIPTIONS).dropIndex(IDX_SUB_USER_PROVIDER_ACTIVE);
    } catch (Exception ignored) {
    }
    try {
      db.getCollection(COL_SUBSCRIPTIONS).dropIndex(IDX_SUB_PROVIDER_ACTIVE);
    } catch (Exception ignored) {
    }
    try {
      db.getCollection(COL_SUBSCRIPTIONS).dropIndex(IDX_SUB_HLTV_PROVIDER_TEAM);
    } catch (Exception ignored) {
    }
  }
}