package com.samdobsondev.lcde4j.model.events.gamedata;

import com.samdobsondev.lcde4j.model.data.AllGameData;

public class MapTerrainChangeEvent implements GameDataEvent {
    private final GameDataEventType gameDataEventType;
    private final Double gameDataEventTime;
    private final AllGameData allGameData;
    private final String mapTerrain;

    public MapTerrainChangeEvent(GameDataEventType gamedataEventType, Double eventDataTime, AllGameData allGameData, String mapTerrain) {
        this.gameDataEventType = gamedataEventType;
        this.gameDataEventTime = eventDataTime;
        this.allGameData = allGameData;
        this.mapTerrain = mapTerrain;
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

    public String getMapTerrain() {
        return this.mapTerrain;
    }
}
