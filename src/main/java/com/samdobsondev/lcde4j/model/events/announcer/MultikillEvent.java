package com.samdobsondev.lcde4j.model.events.announcer;

import com.samdobsondev.lcde4j.model.data.AllGameData;
import com.samdobsondev.lcde4j.model.data.announcer.AnnouncerNotificationType;

public class MultikillEvent implements AnnouncerNotificationEvent
{
    private final AnnouncerNotificationType announcerNotificationType;
    private final Double announcerEventTime;
    private final Long announcerEventID;
    private final AllGameData allGameData;
    private final String killerName;
    private final Long killstreak;

    public MultikillEvent(AnnouncerNotificationType announcerNotificationType, Double announcerEventTime, Long announcerEventID, AllGameData allGameData, String killerName, Long killstreak)
    {
        this.announcerNotificationType = announcerNotificationType;
        this.announcerEventTime = announcerEventTime;
        this.announcerEventID = announcerEventID;
        this.allGameData = allGameData;
        this.killerName = killerName;
        this.killstreak = killstreak;
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

    public String getKillerName()
    {
        return this.killerName;
    }

    public Long getKillstreak()
    {
        return this.killstreak;
    }
}
