package com.meln.event;


import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.types.ObjectId;

import java.util.List;

@BsonDiscriminator(key = "_type", value = "hltv")
public record CriteriaHltv(List<ObjectId> teamIds) implements Criteria { }
