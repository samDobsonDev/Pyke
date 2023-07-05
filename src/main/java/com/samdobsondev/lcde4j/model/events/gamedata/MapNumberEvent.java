package com.samdobsondev.lcde4j.model.events.gamedata;

import com.samdobsondev.lcde4j.model.data.AllGameData;

public class MapNumberEvent implements GameDataEvent
{
    private final GameDataEventType gameDataEventType;
    private final Double gameDataEventTime;
    private final AllGameData allGameData;
    private final Long mapNumber;

    public MapNumberEvent(GameDataEventType gamedataEventType, Double eventDataTime, AllGameData allGameData, Long mapNumber) {
        this.gameDataEventType = gamedataEventType;
        this.gameDataEventTime = eventDataTime;
        this.allGameData = allGameData;
        this.mapNumber = mapNumber;
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

    public Long getMapNumber() {
        return mapNumber;
    }
}
