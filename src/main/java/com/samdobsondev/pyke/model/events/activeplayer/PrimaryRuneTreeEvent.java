package com.samdobsondev.pyke.model.events.activeplayer;

import com.samdobsondev.pyke.model.data.AllGameData;
import com.samdobsondev.pyke.model.data.common.RuneTree;

public class PrimaryRuneTreeEvent implements ActivePlayerEvent {
    private final ActivePlayerEventType activePlayerEventType;
    private final Double activePlayerEventTime;
    private final AllGameData allGameData;
    private final RuneTree primaryRuneTree;

    public PrimaryRuneTreeEvent(ActivePlayerEventType activePlayerEventType,
                                Double activePlayerEventTime,
                                AllGameData allGameData,
                                RuneTree primaryRuneTree) {
        this.activePlayerEventType = activePlayerEventType;
        this.activePlayerEventTime = activePlayerEventTime;
        this.allGameData = allGameData;
        this.primaryRuneTree = primaryRuneTree;
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

    public RuneTree getPrimaryRuneTree() {
        return this.primaryRuneTree;
    }
}
