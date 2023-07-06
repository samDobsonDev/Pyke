package com.samdobsondev.pyke.model.events.allplayers;

public enum AllPlayersEventType {
    ASSISTS_CHANGE,
    CS_CHANGE,
    DEATH,
    DEATHS_CHANGE,
    EYE_OF_HERALD_USED_OR_LOST,
    ITEM_ACQUIRED,
    ITEM_SLOT_CHANGE,
    ITEM_SOLD_OR_CONSUMED,
    ITEM_TRANSFORMATION,
    KILLS_CHANGE,
    LEVEL_UP,
    PLAYER_JOINED, // Triggers when the List<Player> increases in size. This type of event will contain all static info about the player (championName, isBot, position, rawChampionName, keystone, primaryRuneTree, secondaryRuneTree, skinID, summonerName, team
    RESPAWN,
    RESPAWN_TIMER_CHANGE,
    SUMMONER_SPELL_ONE_CHANGE,
    SUMMONER_SPELL_TWO_CHANGE,
    VISION_SCORE_CHANGE,
}