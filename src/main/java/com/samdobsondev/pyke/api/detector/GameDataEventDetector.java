package com.samdobsondev.pyke.api.detector;

import com.samdobsondev.pyke.model.data.AllGameData;
import com.samdobsondev.pyke.model.data.gamedata.GameData;
import com.samdobsondev.pyke.model.events.gamedata.*;

import java.util.ArrayList;
import java.util.List;

public class GameDataEventDetector {
    public List<GameDataEvent> detectEvents(AllGameData currentAllGameData, AllGameData incomingAllGameData, Double eventTime) {
        GameData current = currentAllGameData.getGameData();
        GameData incoming = incomingAllGameData.getGameData();

        List<GameDataEvent> events = new ArrayList<>();

        // Check for game mode
        if (!current.getGameMode().equals(incoming.getGameMode())) {
            events.add(new GameModeEvent(GameDataEventType.GAME_MODE, eventTime, incomingAllGameData, incoming.getGameMode()));
        }

        // Check for game time change
        if (!current.getGameTime().equals(incoming.getGameTime())) {
            events.add(new GameTimeChangeEvent(GameDataEventType.GAME_TIME_CHANGE, eventTime, incomingAllGameData, incoming.getGameTime()));
        }

        // Check for map name
        if (!current.getMapName().equals(incoming.getMapName())) {
            events.add(new MapNameEvent(GameDataEventType.MAP_NAME,eventTime, incomingAllGameData, incoming.getMapName()));
        }

        // Check for map number
        if (!current.getMapNumber().equals(incoming.getMapNumber())) {
            events.add(new MapNumberEvent(GameDataEventType.MAP_NUMBER, eventTime, incomingAllGameData, incoming.getMapNumber()));
        }

        // Check for map terrain
        if (!current.getMapTerrain().equals(incoming.getMapTerrain())) {
            events.add(new MapTerrainChangeEvent(GameDataEventType.MAP_TERRAIN_CHANGE, eventTime, incomingAllGameData, incoming.getMapTerrain()));
        }

        return events;
    }
}
