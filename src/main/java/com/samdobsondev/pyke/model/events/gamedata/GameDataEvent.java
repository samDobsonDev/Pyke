package com.samdobsondev.pyke.model.events.gamedata;

import com.samdobsondev.pyke.model.data.AllGameData;

public interface GameDataEvent {
    GameDataEventType getGameDataEventType();
    Double getGameDataEventTime();
    AllGameData getAllGameData();
}
