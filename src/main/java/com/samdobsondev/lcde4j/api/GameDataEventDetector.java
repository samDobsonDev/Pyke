package com.samdobsondev.lcde4j.api;

import com.samdobsondev.lcde4j.model.data.AllGameData;
import com.samdobsondev.lcde4j.model.data.gamedata.GameData;
import com.samdobsondev.lcde4j.model.events.gamedata.GameDataEvent;
import com.samdobsondev.lcde4j.model.events.gamedata.GameDataEventType;

import java.util.ArrayList;
import java.util.List;

public class GameDataEventDetector
{
    public List<GameDataEvent> detectEvents(AllGameData currentAllGameData, AllGameData incomingAllGameData, Double eventTime) {
        GameData current = currentAllGameData.getGameData();
        GameData incoming = incomingAllGameData.getGameData();

        List<GameDataEvent> events = new ArrayList<>();

        // Check for game mode
        if (!current.getGameMode().equals(incoming.getGameMode())) {
            GameDataEvent event = new GameDataEvent();
            event.setGameDataEventType(GameDataEventType.GAME_MODE);
            event.setGameDataEventTime(eventTime);
            event.setGameMode(incoming.getGameMode());
            event.setAllGameData(incomingAllGameData);
            events.add(event);
        }

        // Check for game time change (this event does not need an eventTime field)
        if (!current.getGameTime().equals(incoming.getGameTime())) {
            GameDataEvent event = new GameDataEvent();
            event.setGameDataEventType(GameDataEventType.GAME_TIME_CHANGE);
            event.setGameTime(incoming.getGameTime());
            event.setAllGameData(incomingAllGameData);
            events.add(event);
        }

        // Check for map name
        if (!current.getMapName().equals(incoming.getMapName())) {
            GameDataEvent event = new GameDataEvent();
            event.setGameDataEventType(GameDataEventType.MAP_NAME);
            event.setGameDataEventTime(eventTime);
            event.setMapName(incoming.getMapName());
            event.setAllGameData(incomingAllGameData);
            events.add(event);
        }

        // Check for map number
        if (!current.getMapNumber().equals(incoming.getMapNumber())) {
            GameDataEvent event = new GameDataEvent();
            event.setGameDataEventType(GameDataEventType.MAP_NUMBER);
            event.setGameDataEventTime(eventTime);
            event.setMapNumber(incoming.getMapNumber());
            event.setAllGameData(incomingAllGameData);
            events.add(event);
        }

        // Check for map terrain
        if (!current.getMapTerrain().equals(incoming.getMapTerrain())) {
            GameDataEvent event = new GameDataEvent();
            event.setGameDataEventType(GameDataEventType.MAP_TERRAIN_CHANGE);
            event.setGameDataEventTime(eventTime);
            event.setMapTerrain(incoming.getMapTerrain());
            event.setAllGameData(incomingAllGameData);
            events.add(event);
        }

        return events;
    }
}
