package com.exhibition.modules.exhibit.event;

import com.exhibition.modules.exhibit.Exhibit;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ExhibitCreatedEvent {

    private final Exhibit exhibit;
}
