package com.meln.event.hltv;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

import java.time.Instant;

@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "matchId", callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@MongoEntity(collection = "hltv_match")
public class HltvMatch extends PanacheMongoEntity {
    @NotBlank
    @BsonProperty("match_id")
    private String matchId;

    @NotBlank
    @BsonProperty("match_url")
    private String matchUrl;

    @NotNull
    @BsonProperty("starts_at")
    private Instant startsAt;

    @BsonProperty("score1")
    private Integer score1;

    @BsonProperty("score2")
    private Integer score2;

    @NotNull
    @BsonProperty("team1_id")
    private ObjectId team1Id;

    @NotNull
    @BsonProperty("team2_id")
    private ObjectId team2Id;
}
