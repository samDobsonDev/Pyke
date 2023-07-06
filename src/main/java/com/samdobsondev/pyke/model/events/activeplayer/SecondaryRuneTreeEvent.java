package com.samdobsondev.pyke.model.events.activeplayer;

import com.samdobsondev.pyke.model.data.AllGameData;
import com.samdobsondev.pyke.model.data.common.RuneTree;

public class SecondaryRuneTreeEvent implements ActivePlayerEvent {
    private final ActivePlayerEventType activePlayerEventType;
    private final Double activePlayerEventTime;
    private final AllGameData allGameData;
    private final RuneTree secondaryRuneTree;

    public SecondaryRuneTreeEvent(ActivePlayerEventType activePlayerEventType,
                                  Double activePlayerEventTime,
                                  AllGameData allGameData,
                                  RuneTree secondaryRuneTree) {
        this.activePlayerEventType = activePlayerEventType;
        this.activePlayerEventTime = activePlayerEventTime;
        this.allGameData = allGameData;
        this.secondaryRuneTree = secondaryRuneTree;
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

    public RuneTree getSecondaryRuneTree() {
        return this.secondaryRuneTree;
    }
}
