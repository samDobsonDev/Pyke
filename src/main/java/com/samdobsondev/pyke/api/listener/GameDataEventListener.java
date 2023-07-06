package com.samdobsondev.pyke.api.listener;

import com.samdobsondev.pyke.model.events.gamedata.*;

public interface GameDataEventListener {
    default void onGameModeChange(GameModeEvent event) { }
    default void onGameTimeChange(GameTimeChangeEvent event) { }
    default void onMapName(MapNameEvent event) { }
    default void onMapNumber(MapNumberEvent event) { }
    default void onMapTerrainChange(MapTerrainChangeEvent event) { }
}

