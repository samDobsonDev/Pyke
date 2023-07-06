package com.samdobsondev.lcde4j.model.events.activeplayer;

import com.samdobsondev.lcde4j.model.data.AllGameData;
import com.samdobsondev.lcde4j.model.data.common.Rune;

public class KeystoneEvent implements ActivePlayerEvent {
    private final ActivePlayerEventType activePlayerEventType;
    private final Double activePlayerEventTime;
    private final AllGameData allGameData;
    private final Rune keystone;

    public KeystoneEvent(ActivePlayerEventType activePlayerEventType,
                         Double activePlayerEventTime,
                         AllGameData allGameData,
                         Rune keystone) {
        this.activePlayerEventType = activePlayerEventType;
        this.activePlayerEventTime = activePlayerEventTime;
        this.allGameData = allGameData;
        this.keystone = keystone;
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

    public Rune getKeystone() {
        return this.keystone;
    }
}
