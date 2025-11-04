package com.meln.api;

import com.meln.api.dto.ProvidersResponse;
import com.meln.app.event.ProviderRepository;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.AllArgsConstructor;

@Path(Endpoints.API_V1)
@AllArgsConstructor(onConstructor_ = @Inject)
public class ProviderResource {

  private final ProviderRepository providerRepository;

  @GET
  @Path(Endpoints.Provider.PROVIDERS)
  @Produces(MediaType.APPLICATION_JSON)
  public ProvidersResponse list() {
    var providers = providerRepository.findAll().list();
    return new ProvidersResponse(providers);
  }

}
