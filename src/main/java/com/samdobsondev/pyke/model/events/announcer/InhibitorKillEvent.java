package com.samdobsondev.pyke.model.events.announcer;

import com.samdobsondev.pyke.model.data.AllGameData;
import com.samdobsondev.pyke.model.data.announcer.AnnouncerNotificationType;

import java.util.List;

public class InhibitorKillEvent implements AnnouncerNotificationEvent {
    private final AnnouncerNotificationType announcerNotificationType;
    private final Double announcerEventTime;
    private final Long announcerEventID;
    private final AllGameData allGameData;
    private final String inhibitorKilled;
    private final String killerName;
    private final List<String> assisters;

    public InhibitorKillEvent(AnnouncerNotificationType announcerNotificationType,
                              Double announcerEventTime,
                              Long announcerEventID,
                              AllGameData allGameData,
                              String inhibitorKilled,
                              String killerName,
                              List<String> assisters) {
        this.announcerNotificationType = announcerNotificationType;
        this.announcerEventTime = announcerEventTime;
        this.announcerEventID = announcerEventID;
        this.allGameData = allGameData;
        this.inhibitorKilled = inhibitorKilled;
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

    public String getInhibitorKilled() {
        return this.inhibitorKilled;
    }

    public String getKillerName() {
        return this.killerName;
    }

    public List<String> getAssisters() {
        return this.assisters;
    }
}
