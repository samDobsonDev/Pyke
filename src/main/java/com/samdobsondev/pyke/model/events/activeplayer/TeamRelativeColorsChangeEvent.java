package com.samdobsondev.pyke.model.events.activeplayer;

import com.samdobsondev.pyke.model.data.AllGameData;

public class TeamRelativeColorsChangeEvent implements ActivePlayerEvent {
    private final ActivePlayerEventType activePlayerEventType;
    private final Double activePlayerEventTime;
    private final AllGameData allGameData;
    private final Boolean teamRelativeColors;

    public TeamRelativeColorsChangeEvent(ActivePlayerEventType activePlayerEventType,
                                         Double activePlayerEventTime,
                                         AllGameData allGameData,
                                         Boolean teamRelativeColors) {
        this.activePlayerEventType = activePlayerEventType;
        this.activePlayerEventTime = activePlayerEventTime;
        this.allGameData = allGameData;
        this.teamRelativeColors = teamRelativeColors;
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

    public Boolean getTeamRelativeColors() {
        return this.teamRelativeColors;
    }
}
