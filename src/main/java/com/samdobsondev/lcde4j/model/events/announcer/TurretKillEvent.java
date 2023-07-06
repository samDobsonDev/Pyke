package com.samdobsondev.lcde4j.model.events.announcer;

import com.samdobsondev.lcde4j.model.data.AllGameData;
import com.samdobsondev.lcde4j.model.data.announcer.AnnouncerNotificationType;

import java.util.List;

public class TurretKillEvent implements AnnouncerNotificationEvent {
    private final AnnouncerNotificationType announcerNotificationType;
    private final Double announcerEventTime;
    private final Long announcerEventID;
    private final AllGameData allGameData;
    private final String turretKilled;
    private final String killerName;
    private final List<String> assisters;

    public TurretKillEvent(AnnouncerNotificationType announcerNotificationType,
                           Double announcerEventTime,
                           Long announcerEventID,
                           AllGameData allGameData,
                           String turretKilled,
                           String killerName,
                           List<String> assisters) {
        this.announcerNotificationType = announcerNotificationType;
        this.announcerEventTime = announcerEventTime;
        this.announcerEventID = announcerEventID;
        this.allGameData = allGameData;
        this.turretKilled = turretKilled;
        this.killerName = killerName;
        this.assisters = assisters;
    }

    @Override
    public AnnouncerNotificationType getAnnouncerNotificationEventType() {
        return this.announcerNotificationType;
    }

    @Override
    public Double getAnnouncerEventTime() {
        return this.announcerEventTime;
    }

    @Override
    public Long getAnnouncerEventID() {
        return this.announcerEventID;
    }

    @Override
    public AllGameData getAllGameData() {
        return this.allGameData;
    }

    public String getTurretKilled() {
        return this.turretKilled;
    }

    public String getKillerName() {
        return this.killerName;
    }

    public List<String> getAssisters() {
        return this.assisters;
    }
}
