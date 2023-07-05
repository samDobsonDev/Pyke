package com.samdobsondev.lcde4j.model.events.gamedata;

import com.samdobsondev.lcde4j.model.data.AllGameData;

public class MapNameEvent implements GameDataEvent
{
    private final GameDataEventType gameDataEventType;
    private final Double gameDataEventTime;
    private final AllGameData allGameData;
    private final String mapName;

    public MapNameEvent(GameDataEventType gamedataEventType, Double eventDataTime, AllGameData allGameData, String mapName) {
        this.gameDataEventType = gamedataEventType;
        this.gameDataEventTime = eventDataTime;
        this.allGameData = allGameData;
        this.mapName = mapName;
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

    public String getMapName() {
        return mapName;
    }
}
