package com.samdobsondev.pyke.model.events.gamedata;

import com.samdobsondev.pyke.model.data.AllGameData;

public class MapTerrainChangeEvent implements GameDataEvent {
    private final GameDataEventType gameDataEventType;
    private final Double gameDataEventTime;
    private final AllGameData allGameData;
    private final String oldMapTerrain;
    private final String newMapTerrain;

    public MapTerrainChangeEvent(GameDataEventType gamedataEventType, Double eventDataTime, AllGameData allGameData, String oldMapTerrain, String newMapTerrain) {
        this.gameDataEventType = gamedataEventType;
        this.gameDataEventTime = eventDataTime;
        this.allGameData = allGameData;
        this.oldMapTerrain = oldMapTerrain;
        this.newMapTerrain = newMapTerrain;
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

    public String getOldMapTerrain() {
        return this.oldMapTerrain;
    }

    public String getNewMapTerrain() {
        return this.newMapTerrain;
    }
}
