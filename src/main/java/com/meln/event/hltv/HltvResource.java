package com.meln.event.hltv;

import com.meln.common.Endpoints;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;

@Path(Endpoints.API_V1)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class HltvResource {
    private final HltvService hltvService;

    @POST
    @Path(Endpoints.Hltv.HLTV_SYNC)
    public void sync() {
        hltvService.sync();
    }
}
