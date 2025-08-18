package com.meln.event;


import lombok.Getter;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

import java.util.List;

@Setter
@Getter
@BsonDiscriminator(key = "_type", value = "hltv")
public final class CriteriaHltv implements Criteria {

    @BsonProperty("team_ids")
    private List<ObjectId> teamIds;

    public CriteriaHltv() {
    }

    public CriteriaHltv(List<ObjectId> teamIds) {
        this.teamIds = teamIds;
    }

}