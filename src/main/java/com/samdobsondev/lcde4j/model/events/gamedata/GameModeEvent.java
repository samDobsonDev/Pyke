package com.samdobsondev.lcde4j.model.events.gamedata;

import com.samdobsondev.lcde4j.model.data.AllGameData;

public class GameModeEvent implements GameDataEvent {
    private final GameDataEventType gameDataEventType;
    private final Double gameDataEventTime;
    private final AllGameData allGameData;
    private final String gameMode;

    public GameModeEvent(GameDataEventType gamedataEventType, Double eventDataTime, AllGameData allGameData, String gameMode) {
        this.gameDataEventType = gamedataEventType;
        this.gameDataEventTime = eventDataTime;
        this.allGameData = allGameData;
        this.gameMode = gameMode;
    }

    @Override
    public GameDataEventType getGameDataEventType() {
        return this.gameDataEventType;
    }

    @Override
    public Double getGameDataEventTime() {
        return this.gameDataEventTime;
    }

    @Override
    public AllGameData getAllGameData() {
        return this.allGameData;
    }

    public String getGameMode() {
        return this.gameMode;
    }
}
