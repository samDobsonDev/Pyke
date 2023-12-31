package com.samdobsondev.pyke.model.events.gamedata;

import com.samdobsondev.pyke.model.data.AllGameData;

public class MapNameEvent implements GameDataEvent {
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

    public String getMapName() {
        return this.mapName;
    }
}
