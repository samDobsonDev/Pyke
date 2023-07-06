package com.samdobsondev.pyke.api.listener;

import com.samdobsondev.pyke.model.events.allplayers.*;

public interface AllPlayersEventListener {
    default void onAssistsChange(AssistsChangeEvent event) { }
    default void onCreepScoreChange(CreepScoreChangeEvent event) { }
    default void onDeath(DeathEvent event) { }
    default void onDeathsChange(DeathsChangeEvent event) { }
    default void onEyeOfHeraldUsedOrLost(EyeOfHeraldUsedOrLostEvent event) { }
    default void onItemAcquired(ItemAcquiredEvent event) { }
    default void onItemSlotChange(ItemSlotChangeEvent event) { }
    default void onItemSoldOrConsumed(ItemSoldOrConsumedEvent event) { }
    default void onItemTransformation(ItemTransformationEvent event) { }
    default void onKillsChange(KillsChangeEvent event) { }
    default void onLevelUp(LevelUpEvent event) { }
    default void onPlayerJoined(PlayerJoinedEvent event) { }
    default void onRespawn(RespawnEvent event) { }
    default void onRespawnTimerChange(RespawnTimerChangeEvent event) { }
    default void onSummonerSpellOneChange(SummonerSpellOneChangeEvent event) { }
    default void onSummonerSpellTwoChange(SummonerSpellTwoChangeEvent event) { }
    default void onVisionScoreChange(VisionScoreChangeEvent event) { }
}
