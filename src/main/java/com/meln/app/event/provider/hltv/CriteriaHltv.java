package com.meln.app.event.provider.hltv;

import com.meln.app.common.event.Criteria;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@BsonDiscriminator(key = "_type", value = "hltv")
public final class CriteriaHltv implements Criteria {

  @BsonProperty("team_ids")
  private List<ObjectId> teamIds;

}