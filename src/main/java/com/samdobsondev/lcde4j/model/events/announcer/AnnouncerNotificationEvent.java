package com.samdobsondev.lcde4j.model.events.announcer;

import com.samdobsondev.lcde4j.model.data.AllGameData;
import com.samdobsondev.lcde4j.model.data.announcer.AnnouncerNotificationType;

public interface AnnouncerNotificationEvent
{
    AnnouncerNotificationType getAnnouncerNotificationEventType();
    Double getAnnouncerEventTime();
    Long getAnnouncerEventID();
    AllGameData getAllGameData();
}
