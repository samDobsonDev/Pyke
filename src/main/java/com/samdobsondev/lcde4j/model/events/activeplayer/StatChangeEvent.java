package com.samdobsondev.lcde4j.model.events.activeplayer;

import com.samdobsondev.lcde4j.model.data.AllGameData;

public class StatChangeEvent implements ActivePlayerEvent {
    private final ActivePlayerEventType activePlayerEventType;
    private final Double activePlayerEventTime;
    private final AllGameData allGameData;
    private final String stat;
    private final Double statAmount;

    public StatChangeEvent(ActivePlayerEventType activePlayerEventType,
                           Double activePlayerEventTime,
                           AllGameData allGameData,
                           String stat,
                           Double statAmount) {
        this.activePlayerEventType = activePlayerEventType;
        this.activePlayerEventTime = activePlayerEventTime;
        this.allGameData = allGameData;
        this.stat = stat;
        this.statAmount = statAmount;
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

    public String getStat() {
        return this.stat;
    }

    public Double getStatAmount() {
        return this.statAmount;
    }
}
