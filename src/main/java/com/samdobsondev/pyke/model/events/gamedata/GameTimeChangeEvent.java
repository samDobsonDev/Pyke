package com.samdobsondev.pyke.model.events.gamedata;

import com.samdobsondev.pyke.model.data.AllGameData;

public class GameTimeChangeEvent implements GameDataEvent {
    private final GameDataEventType gameDataEventType;
    private final Double gameDataEventTime;
    private final AllGameData allGameData;
    private final Double gameTime;

    public GameTimeChangeEvent(GameDataEventType gamedataEventType, Double eventDataTime, AllGameData allGameData, Double gameTime) {
        this.gameDataEventType = gamedataEventType;
        this.gameDataEventTime = eventDataTime;
        this.allGameData = allGameData;
        this.gameTime = gameTime;
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

    public Double getGameTime() {
        return this.gameTime;
    }
}
