package com.samdobsondev.lcde4j.model.data.announcer;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;

@Data
public class AnnouncerNotification
{
    @SerializedName("EventID")
    private Long announcerEventID;
    @SerializedName("EventName")
    private AnnouncerNotificationType announcerNotificationType;
    @SerializedName("EventTime")
    private Double announcerEventTime;
    @SerializedName("KillerName")
    private String killerName;
    @SerializedName("TurretKilled")
    private String turretKilled;
    @SerializedName("InhibKilled")
    private String inhibKilled;
    @SerializedName("VictimName")
    private String victimName;
    @SerializedName("DragonType")
    private String dragonType;
    @SerializedName("Acer")
    private String acer;
    @SerializedName("AcingTeam")
    private String acingTeam;
    @SerializedName("InhibRespawned")
    private String inhibRespawned;
    @SerializedName("InhibRespawningSoon")
    private String inhibRespawningSoon;
    @SerializedName("Recipient")
    private String recipient;
    @SerializedName("KillStreak")
    private Long killStreak;
    @SerializedName("Stolen")
    private Boolean stolen;
    @SerializedName("Assisters")
    private List<String> assisters;
}
