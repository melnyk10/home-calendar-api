package com.meln.app.common.event;

import org.bson.codecs.pojo.annotations.BsonDiscriminator;

@BsonDiscriminator(key = "_type")
public interface EventProviderCriteria {

}
