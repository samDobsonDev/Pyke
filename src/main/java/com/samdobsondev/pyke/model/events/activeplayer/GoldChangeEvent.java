package com.samdobsondev.pyke.model.events.activeplayer;

import com.samdobsondev.pyke.model.data.AllGameData;

public class GoldChangeEvent implements ActivePlayerEvent {
    private final ActivePlayerEventType activePlayerEventType;
    private final Double activePlayerEventTime;
    private final AllGameData allGameData;
    private final Double goldAmount;

    public GoldChangeEvent(ActivePlayerEventType activePlayerEventType,
                           Double activePlayerEventTime,
                           AllGameData allGameData,
                           Double goldAmount) {
        this.activePlayerEventType = activePlayerEventType;
        this.activePlayerEventTime = activePlayerEventTime;
        this.allGameData = allGameData;
        this.goldAmount = goldAmount;
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

    public Double getGoldAmount() {
        return this.goldAmount;
    }
}
