package com.samdobsondev.lcde4j.model.events.activeplayer;

import com.samdobsondev.lcde4j.model.data.AllGameData;

public class ResourceTypeChangeEvent implements ActivePlayerEvent {
    private final ActivePlayerEventType activePlayerEventType;
    private final Double activePlayerEventTime;
    private final AllGameData allGameData;
    private final String resourceType;

    public ResourceTypeChangeEvent(ActivePlayerEventType activePlayerEventType,
                                   Double activePlayerEventTime,
                                   AllGameData allGameData,
                                   String resourceType) {
        this.activePlayerEventType = activePlayerEventType;
        this.activePlayerEventTime = activePlayerEventTime;
        this.allGameData = allGameData;
        this.resourceType = resourceType;
    }

    @Override
    public ActivePlayerEventType getActivePlayerEventType() {
        return this.activePlayerEventType;
    }

    @Override
    public Double getActivePlayerEventTime() {
        return this.activePlayerEventTime;
    }

    @Override
    public AllGameData getAllGameData() {
        return this.allGameData;
    }

    public String getResourceType() {
        return this.resourceType;
    }
}
