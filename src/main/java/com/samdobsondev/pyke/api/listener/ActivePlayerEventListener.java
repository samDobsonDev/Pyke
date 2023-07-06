package com.samdobsondev.pyke.api.listener;

import com.samdobsondev.pyke.model.events.activeplayer.*;

public interface ActivePlayerEventListener {
    default void onAbilityLevelUp(AbilityLevelUpEvent event) { }
    default void onGeneralRune(GeneralRuneEvent event) { }
    default void onGoldChange(GoldChangeEvent event) { }
    default void onKeystone(KeystoneEvent event) { }
    default void onLevelUp(ActivePlayerLevelUpEvent event) { }
    default void onPassive(PassiveEvent event) { }
    default void onPrimaryRuneTree(PrimaryRuneTreeEvent event) { }
    default void onResourceTypeChange(ResourceTypeChangeEvent event) { }
    default void onSecondaryRuneTree(SecondaryRuneTreeEvent event) { }
    default void onStatChange(StatChangeEvent event) { }
    default void onStatRune(StatRuneEvent event) { }
    default void onSummonerName(SummonerNameEvent event) { }
    default void onTeamRelativeColorsChange(TeamRelativeColorsChangeEvent event) { }
}
