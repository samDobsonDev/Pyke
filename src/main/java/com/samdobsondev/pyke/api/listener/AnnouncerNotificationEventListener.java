package com.samdobsondev.pyke.api.listener;

import com.samdobsondev.pyke.model.events.announcer.*;

public interface AnnouncerNotificationEventListener {
    default void onAce(AceEvent event) { }
    default void onBaronKill(BaronKillEvent event) { }
    default void onChampionKill(ChampionKillEvent event) { }
    default void onDragonKill(DragonKillEvent event) { }
    default void onFirstBlood(FirstBloodEvent event) { }
    default void onFirstTurret(FirstTurretEvent event) { }
    default void onGameEnd(GameEndEvent event) { }
    default void onGameStart(GameStartEvent event) { }
    default void onHeraldKill(HeraldKillEvent event) { }
    default void onInhibitorKill(InhibitorKillEvent event) { }
    default void onInhibitorRespawningSoon(InhibitorRespawingSoonEvent event) { }
    default void onInhibitorRespawn(InhibitorRespawnEvent event) { }
    default void onMinionsSpawning(MinionsSpawningEvent event) { }
    default void onMultikill(MultikillEvent event) { }
    default void onTurretKill(TurretKillEvent event) { }
}
