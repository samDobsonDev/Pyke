package com.samdobsondev.pyke.model.events.announcer;

import com.samdobsondev.pyke.model.data.AllGameData;
import com.samdobsondev.pyke.model.data.announcer.AnnouncerNotificationType;

public class FirstTurretEvent implements AnnouncerNotificationEvent {
    private final AnnouncerNotificationType announcerNotificationType;
    private final Double announcerEventTime;
    private final Long announcerEventID;
    private final AllGameData allGameData;
    private final String killerName;

    public FirstTurretEvent(AnnouncerNotificationType announcerNotificationType,
                            Double announcerEventTime,
                            Long announcerEventID,
                            AllGameData allGameData,
                            String killerName) {
        this.announcerNotificationType = announcerNotificationType;
        this.announcerEventTime = announcerEventTime;
        this.announcerEventID = announcerEventID;
        this.allGameData = allGameData;
        this.killerName = killerName;
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

    public String getKillerName() {
        return this.killerName;
    }
}
