package com.meln.event.hltv;

import lombok.experimental.UtilityClass;

@UtilityClass
public class HltvTeamConverter {
    public HltvTeam from(HltvTeamResponse response) {
        HltvTeam hltvTeam = new HltvTeam();
        hltvTeam.setTeamId(response.getTeamId().toString());
        hltvTeam.setSlug(response.getTeamIdName());
        hltvTeam.setTeamName(response.getName());
        hltvTeam.setLogoUrl(response.getLogoUrl());
        hltvTeam.setRank(response.getRank());
        return hltvTeam;
    }
}
