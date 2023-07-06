package com.samdobsondev.lcde4j.model.events.activeplayer;

import com.samdobsondev.lcde4j.model.data.AllGameData;
import com.samdobsondev.lcde4j.model.data.activeplayer.fullrunes.StatRune;

public class StatRuneEvent implements ActivePlayerEvent {
    private final ActivePlayerEventType activePlayerEventType;
    private final Double activePlayerEventTime;
    private final AllGameData allGameData;
    private final StatRune statRune;

    public StatRuneEvent(ActivePlayerEventType activePlayerEventType,
                         Double activePlayerEventTime,
                         AllGameData allGameData,
                         StatRune statRune) {
        this.activePlayerEventType = activePlayerEventType;
        this.activePlayerEventTime = activePlayerEventTime;
        this.allGameData = allGameData;
        this.statRune = statRune;
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

    public StatRune getStatRune() {
        return this.statRune;
    }
}
