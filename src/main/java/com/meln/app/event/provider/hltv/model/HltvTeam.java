package com.meln.app.event.provider.hltv.model;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
@EqualsAndHashCode(of = {"sourceId"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
@MongoEntity(collection = "hltvTeam")
public class HltvTeam extends PanacheMongoEntity {

  public static final String COL_LOGO_URL = "logoUrl";
  public static final String COL_SOURCE_ID = "sourceId";
  public static final String COL_SLUG = "slug";
  public static final String COL_TEAM_NAME = "teamName";
  public static final String COL_RANK = "rank";

  @BsonProperty(COL_LOGO_URL)
  @Pattern(regexp = "^https?://.+", message = "logoUrl must be http(s) URL")
  private String logoUrl;

  @NotBlank
  @BsonProperty(COL_SOURCE_ID)
  private String sourceId;

  @NotBlank
  @Size(max = 128)
  @BsonProperty(COL_SLUG)
  private String slug;

  @NotBlank
  @Size(max = 128)
  @BsonProperty(COL_TEAM_NAME)
  private String teamName;

  @BsonProperty(COL_RANK)
  private Integer rank;

  public ObjectId getId() {
    return id;
  }
}
