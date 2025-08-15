package com.meln.event;

import org.bson.codecs.pojo.annotations.BsonDiscriminator;

@BsonDiscriminator(key = "_type")
public sealed interface Criteria permits CriteriaHltv {
}
