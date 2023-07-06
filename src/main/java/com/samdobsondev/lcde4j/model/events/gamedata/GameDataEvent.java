package com.samdobsondev.lcde4j.model.events.gamedata;

import com.samdobsondev.lcde4j.model.data.AllGameData;

public interface GameDataEvent {
    GameDataEventType getGameDataEventType();
    Double getGameDataEventTime();
    AllGameData getAllGameData();
}
