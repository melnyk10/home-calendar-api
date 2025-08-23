package com.meln.common.event;

import com.meln.event.Provider;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

//todo: rename me!
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventDto {
    @NotBlank
    private String sourceId;

    @NotNull
    private Provider provider;

    @NotBlank
    private String title;

    private String url;

    private String notes;

    private boolean allDay;

    @NotNull
    private Instant startAt;

    private Instant endAt;
}
