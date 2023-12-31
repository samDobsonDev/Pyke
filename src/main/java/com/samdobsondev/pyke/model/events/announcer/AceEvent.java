package com.samdobsondev.pyke.model.events.announcer;

import com.samdobsondev.pyke.model.data.AllGameData;
import com.samdobsondev.pyke.model.data.announcer.AnnouncerNotificationType;

public class AceEvent implements AnnouncerNotificationEvent {
    private final AnnouncerNotificationType announcerNotificationType;
    private final Double announcerEventTime;
    private final Long announcerEventID;
    private final AllGameData allGameData;
    private final String acer;
    private final String acingTeam;

    public AceEvent(AnnouncerNotificationType announcerNotificationType,
                    Double announcerEventTime,
                    Long announcerEventID,
                    AllGameData allGameData,
                    String acer,
                    String acingTeam) {
        this.announcerNotificationType = announcerNotificationType;
        this.announcerEventTime = announcerEventTime;
        this.announcerEventID = announcerEventID;
        this.allGameData = allGameData;
        this.acer = acer;
        this.acingTeam = acingTeam;
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

    public String getAcer() {
        return this.acer;
    }

    public String getAcingTeam() {
        return this.acingTeam;
    }
}
