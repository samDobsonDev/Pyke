package com.samdobsondev.lcde4j.api;

import com.samdobsondev.lcde4j.model.data.AllGameData;
import com.samdobsondev.lcde4j.model.data.announcer.AnnouncerNotification;
import com.samdobsondev.lcde4j.model.data.announcer.AnnouncerNotificationType;
import com.samdobsondev.lcde4j.model.data.announcer.AnnouncerNotifications;
import com.samdobsondev.lcde4j.model.events.announcer.AnnouncerNotificationEvent;

import java.util.ArrayList;
import java.util.List;

public class AnnouncerNotificationEventDetector
{
    public List<AnnouncerNotificationEvent> detectEvents(AllGameData currentAllGameData, AllGameData incomingAllGameData) {
        AnnouncerNotifications current = currentAllGameData.getAnnouncerNotifications();
        AnnouncerNotifications incoming = incomingAllGameData.getAnnouncerNotifications();

        List<AnnouncerNotificationEvent> events = new ArrayList<>();

        // Check for event changes
        List<AnnouncerNotification> currentEvents = current.getAnnouncerNotifications();
        List<AnnouncerNotification> incomingEvents = incoming.getAnnouncerNotifications();

        if (incomingEvents.size() > currentEvents.size()) {
            // New events have been added
            List<AnnouncerNotification> newEvents = incomingEvents.subList(currentEvents.size(), incomingEvents.size());

            for (AnnouncerNotification newEvent : newEvents) {
                AnnouncerNotificationEvent event = new AnnouncerNotificationEvent();

                // Might want to handle the conversion in a more elegant way, but here is the basic idea:
                event.setAnnouncerNotificationType(AnnouncerNotificationType.valueOf(newEvent.getAnnouncerNotificationType().name()));
                event.setAnnouncerEventTime(newEvent.getAnnouncerEventTime());
                event.setAnnouncerEventID(newEvent.getAnnouncerEventID());
                event.setAllGameData(incomingAllGameData);
                event.setKillerName(newEvent.getKillerName());
                event.setTurretKilled(newEvent.getTurretKilled());
                event.setInhibKilled(newEvent.getInhibKilled());
                event.setVictimName(newEvent.getVictimName());
                event.setDragonType(newEvent.getDragonType());
                event.setAcer(newEvent.getAcer());
                event.setAcingTeam(newEvent.getAcingTeam());
                event.setInhibRespawned(newEvent.getInhibRespawned());
                event.setInhibRespawningSoon(newEvent.getInhibRespawningSoon());
                event.setRecipient(newEvent.getRecipient());
                event.setKillStreak(newEvent.getKillStreak());
                event.setStolen(newEvent.getStolen());
                event.setAssisters(newEvent.getAssisters());

                events.add(event);
            }
        }

        return events;
    }
}
