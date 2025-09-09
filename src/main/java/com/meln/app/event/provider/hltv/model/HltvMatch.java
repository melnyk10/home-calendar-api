package com.meln.app.event.provider.hltv.model;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "matchId")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@MongoEntity(collection = "hltvMatch")
public class HltvMatch extends PanacheMongoEntity {

  public static final String COL_MATCH_ID = "matchId";
  public static final String COL_MATCH_URL = "matchUrl";
  public static final String COL_EVENT_URL = "eventUrl";
  public static final String COL_STARTS_AT = "startsAt";
  public static final String COL_SCORE1 = "score1";
  public static final String COL_SCORE2 = "score2";
  public static final String COL_TEAM1_ID = "team1Id";
  public static final String COL_TEAM2_ID = "team2Id";
  public static final String COL_BEST_OF = "bestOf";

  @NotBlank
  @BsonProperty(COL_MATCH_ID)
  private String matchId;

  @NotBlank
  @BsonProperty(COL_MATCH_URL)
  private String matchUrl;

  @BsonProperty(COL_EVENT_URL)
  private String eventUrl;

  @NotNull
  @BsonProperty(COL_STARTS_AT)
  private Instant startsAt;

  @BsonProperty(COL_SCORE1)
  private Integer score1;

  @BsonProperty(COL_SCORE2)
  private Integer score2;

  @NotNull
  @BsonProperty(COL_TEAM1_ID)
  private ObjectId team1Id;

  @NotNull
  @BsonProperty(COL_TEAM2_ID)
  private ObjectId team2Id;

  @BsonProperty(COL_BEST_OF)
  private Short bestOf;
}
