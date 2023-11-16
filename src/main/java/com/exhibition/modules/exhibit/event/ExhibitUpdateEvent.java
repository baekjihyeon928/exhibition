package com.exhibition.modules.exhibit.event;

import com.exhibition.modules.exhibit.Exhibit;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ExhibitUpdateEvent {

    private final Exhibit exhibit;

    private final String message;
}
