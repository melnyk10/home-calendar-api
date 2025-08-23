package com.meln.subscription;

import org.bson.codecs.pojo.annotations.BsonDiscriminator;

@BsonDiscriminator(key = "_type")
public interface Criteria {
}
