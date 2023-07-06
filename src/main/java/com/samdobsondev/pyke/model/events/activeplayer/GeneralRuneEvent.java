package com.samdobsondev.pyke.model.events.activeplayer;

import com.samdobsondev.pyke.model.data.AllGameData;
import com.samdobsondev.pyke.model.data.common.Rune;

public class GeneralRuneEvent implements ActivePlayerEvent {
    private final ActivePlayerEventType activePlayerEventType;
    private final Double activePlayerEventTime;
    private final AllGameData allGameData;
    private final Rune rune;

    public GeneralRuneEvent(ActivePlayerEventType activePlayerEventType,
                            Double activePlayerEventTime,
                            AllGameData allGameData,
                            Rune rune) {
        this.activePlayerEventType = activePlayerEventType;
        this.activePlayerEventTime = activePlayerEventTime;
        this.allGameData = allGameData;
        this.rune = rune;
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

    public Rune getRune() {
        return this.rune;
    }
}
