package com.samdobsondev.pyke.model.events.announcer;

import com.samdobsondev.pyke.model.data.AllGameData;
import com.samdobsondev.pyke.model.data.announcer.AnnouncerNotificationType;

public class GameStartEvent implements AnnouncerNotificationEvent {
    private final AnnouncerNotificationType announcerNotificationType;
    private final Double announcerEventTime;
    private final Long announcerEventID;
    private final AllGameData allGameData;

    public GameStartEvent(AnnouncerNotificationType announcerNotificationType,
                          Double announcerEventTime,
                          Long announcerEventID,
                          AllGameData allGameData) {
        this.announcerNotificationType = announcerNotificationType;
        this.announcerEventTime = announcerEventTime;
        this.announcerEventID = announcerEventID;
        this.allGameData = allGameData;
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
}
