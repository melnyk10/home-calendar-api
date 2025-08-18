package com.meln.event;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
public class ProviderRegistry {
    private final Map<Provider, EventProvider> eventProviderByType = new EnumMap<>(Provider.class);

    @Inject
    public ProviderRegistry(Instance<EventProvider> providers) {
        providers.forEach(p -> eventProviderByType.put(p.provider(), p));
    }

    public EventProvider get(Provider provider) {
        return Optional.ofNullable(eventProviderByType.get(provider))
                .orElseThrow(() -> new IllegalArgumentException("No provider for " + provider)); //todo: add appropriate exception
    }
}
