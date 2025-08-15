package com.meln.event;


import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

import java.util.List;

@BsonDiscriminator(value = "hltv")
public record CriteriaHltv(@BsonProperty("team_ids") List<ObjectId> teamIds) implements Criteria {
}