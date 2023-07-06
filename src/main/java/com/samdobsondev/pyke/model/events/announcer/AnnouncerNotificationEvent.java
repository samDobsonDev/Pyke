package com.samdobsondev.pyke.model.events.announcer;

import com.samdobsondev.pyke.model.data.AllGameData;
import com.samdobsondev.pyke.model.data.announcer.AnnouncerNotificationType;

public interface AnnouncerNotificationEvent {
    AnnouncerNotificationType getAnnouncerNotificationEventType();
    Double getAnnouncerEventTime();
    Long getAnnouncerEventID();
    AllGameData getAllGameData();
}
