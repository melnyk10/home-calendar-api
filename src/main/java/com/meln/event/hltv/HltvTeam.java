package com.meln.event.hltv;

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
@EqualsAndHashCode(of = {"sourceId"}, callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@MongoEntity(collection = "hltv_team")
public class HltvTeam extends PanacheMongoEntity {
    @BsonProperty("logo_url")
    @Pattern(regexp = "^https?://.+", message = "logoUrl must be http(s) URL")
    private String logoUrl;

    @NotBlank
    @BsonProperty("source_id")
    private String sourceId;

    @NotBlank
    @Size(max = 128)
    @BsonProperty("slug")
    private String slug;

    @NotBlank
    @Size(max = 128)
    @BsonProperty("team_name")
    private String teamName;

    @BsonProperty("rank")
    private Integer rank;

    public ObjectId getId() {
        return id;
    }
}
