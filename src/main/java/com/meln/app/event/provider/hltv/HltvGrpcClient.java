package com.meln.app.event.provider.hltv;

import com.google.protobuf.Empty;
import home_calendar.hltv.HltvServiceGrpc;
import home_calendar.hltv.SyncMatchesRequest;
import home_calendar.hltv.SyncMatchesResponse;
import home_calendar.hltv.SyncTeamsResponse;
import io.quarkus.grpc.GrpcClient;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class HltvGrpcClient {

  @GrpcClient("hltv")
  HltvServiceGrpc.HltvServiceBlockingStub blockingStub;

  public SyncTeamsResponse syncTeams() {
    return blockingStub.syncTeams(Empty.getDefaultInstance());
  }

  public SyncMatchesResponse syncMatches(String teamId, String slug) {
    SyncMatchesRequest req = SyncMatchesRequest.newBuilder()
        .setTeamId(teamId)
        .setSlug(slug)
        .build();
    return blockingStub.syncMatches(req);
  }
}
