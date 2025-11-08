package com.meln.api.dto;

import com.meln.app.event.model.Provider;
import java.util.List;

public record ProvidersResponse(List<Provider> providers) {

}
