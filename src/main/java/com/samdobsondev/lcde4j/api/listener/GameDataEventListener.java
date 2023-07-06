package com.samdobsondev.lcde4j.api.listener;

import com.samdobsondev.lcde4j.model.events.gamedata.*;

public interface GameDataEventListener {
    default void onGameModeChange(GameModeEvent event) { }
    default void onGameTimeChange(GameTimeChangeEvent event) { }
    default void onMapName(MapNameEvent event) { }
    default void onMapNumber(MapNumberEvent event) { }
    default void onMapTerrainChange(MapTerrainChangeEvent event) { }
}

