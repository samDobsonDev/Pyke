package com.samdobsondev.lcde4j.model.events.gamedata;

import com.samdobsondev.lcde4j.model.data.AllGameData;

public class GameTimeChangeEvent implements GameDataEvent
{
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
        return gameDataEventType;
    }

    @Override
    public Double getGameDataEventTime() {
        return gameDataEventTime;
    }

    @Override
    public AllGameData getAllGameData() {
        return allGameData;
    }

    public Double getGameTime() {
        return gameTime;
    }
}
