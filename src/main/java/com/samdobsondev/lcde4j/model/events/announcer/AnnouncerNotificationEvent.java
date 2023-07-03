package com.samdobsondev.lcde4j.model.events.announcer;

import com.samdobsondev.lcde4j.model.data.AllGameData;
import com.samdobsondev.lcde4j.model.data.announcer.AnnouncerNotificationType;
import lombok.Data;
import java.util.List;

@Data
public class AnnouncerNotificationEvent
{
    private AnnouncerNotificationType announcerNotificationType;
    private Double announcerEventTime;
    private Long announcerEventID;
    private AllGameData allGameData;
    private String killerName;
    private String turretKilled;
    private String inhibKilled;
    private String victimName;
    private String dragonType;
    private String acer;
    private String acingTeam;
    private String inhibRespawned;
    private String inhibRespawningSoon;
    private String recipient;
    private Long killStreak;
    private Boolean stolen;
    private List<String> assisters;
}
