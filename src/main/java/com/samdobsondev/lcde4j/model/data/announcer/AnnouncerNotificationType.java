package com.samdobsondev.lcde4j.model.data.announcer;

import com.google.gson.annotations.SerializedName;

public enum AnnouncerNotificationType
{
    @SerializedName("Ace")
    ACE,
    @SerializedName("BaronKill")
    BARON_KILL,
    @SerializedName("ChampionKill")
    CHAMPION_KILL,
    @SerializedName("DragonKill")
    DRAGON_KILL,
    @SerializedName("FirstBlood")
    FIRST_BLOOD,
    @SerializedName("FirstBrick")
    FIRST_TURRET,
    @SerializedName("GameEnd")
    GAME_END,
    @SerializedName("GameStart")
    GAME_START,
    @SerializedName("HeraldKill")
    HERALD_KILL,
    @SerializedName("InhibKilled")
    INHIBITOR_KILL,
    @SerializedName("InhibRespawned")
    INHIBITOR_RESPAWN,
    @SerializedName("InhibRespawningSoon")
    INHIBITOR_RESPAWNING_SOON,
    @SerializedName("MinionsSpawning")
    MINIONS_SPAWNING,
    @SerializedName("Multikill")
    MULTIKILL,
    @SerializedName("TurretKilled")
    TURRET_KILL,
}
