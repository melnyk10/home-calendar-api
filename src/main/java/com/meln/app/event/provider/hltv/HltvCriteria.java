package com.meln.app.event.provider.hltv;

import com.meln.app.common.event.EventProviderCriteria;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public final class HltvCriteria implements EventProviderCriteria {

  private List<String> teamIds;

}