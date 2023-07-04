package com.samdobsondev.lcde4j.model.events.allplayers;

public enum AllPlayersEventType {
    PLAYER_JOINED, // Triggers when the List<Player> increases in size. This type of even will contain all static info about the player (championName, isBot, position, rawChampionName, keystone, primaryRuneTree, secondaryRuneTree, skinID, summonerName, team
    DEATH,
    RESPAWN,
    ITEM_ACQUIRED,
    ITEM_TRANSFORMATION,
    ITEM_SOLD_OR_CONSUMED,
    ITEM_SLOT_CHANGE,
    EYE_OF_HERALD_USED_OR_LOST,
    LEVEL_UP,
    RESPAWN_TIMER_CHANGE,
    ASSISTS_CHANGED,
    CS_CHANGED,
    DEATHS_CHANGED,
    KILLS_CHANGED,
    VISION_SCORE_CHANGED,
    SUMMONER_SPELL_ONE_CHANGE,
    SUMMONER_SPELL_TWO_CHANGE
}
