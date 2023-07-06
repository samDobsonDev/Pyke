package com.samdobsondev.lcde4j.api.detector;

import com.samdobsondev.lcde4j.model.data.AllGameData;
import com.samdobsondev.lcde4j.model.data.announcer.AnnouncerNotification;
import com.samdobsondev.lcde4j.model.data.announcer.AnnouncerNotificationType;
import com.samdobsondev.lcde4j.model.data.announcer.AnnouncerNotifications;
import com.samdobsondev.lcde4j.model.events.announcer.*;

import java.util.ArrayList;
import java.util.List;

public class AnnouncerNotificationEventDetector {
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
                switch (newEvent.getAnnouncerNotificationType()) {
                    case ACE:
                        events.add(new AceEvent(AnnouncerNotificationType.ACE, newEvent.getAnnouncerEventTime(), newEvent.getAnnouncerEventID(), incomingAllGameData, newEvent.getAcer(), newEvent.getAcingTeam()));
                        break;
                    case BARON_KILL:
                        events.add(new BaronKillEvent(AnnouncerNotificationType.BARON_KILL, newEvent.getAnnouncerEventTime(), newEvent.getAnnouncerEventID(), incomingAllGameData, newEvent.getStolen(), newEvent.getKillerName(), newEvent.getAssisters()));
                        break;
                    case CHAMPION_KILL:
                        events.add(new ChampionKillEvent(AnnouncerNotificationType.CHAMPION_KILL, newEvent.getAnnouncerEventTime(), newEvent.getAnnouncerEventID(), incomingAllGameData, newEvent.getVictimName(), newEvent.getKillerName(), newEvent.getAssisters()));
                        break;
                    case DRAGON_KILL:
                        events.add(new DragonKillEvent(AnnouncerNotificationType.DRAGON_KILL, newEvent.getAnnouncerEventTime(), newEvent.getAnnouncerEventID(), incomingAllGameData, newEvent.getDragonType(), newEvent.getStolen(), newEvent.getKillerName(), newEvent.getAssisters()));
                        break;
                    case FIRST_BLOOD:
                        events.add(new FirstBloodEvent(AnnouncerNotificationType.FIRST_BLOOD, newEvent.getAnnouncerEventTime(), newEvent.getAnnouncerEventID(), incomingAllGameData, newEvent.getRecipient()));
                        break;
                    case FIRST_TURRET:
                        events.add(new FirstTurretEvent(AnnouncerNotificationType.FIRST_TURRET, newEvent.getAnnouncerEventTime(), newEvent.getAnnouncerEventID(), incomingAllGameData, newEvent.getKillerName()));
                        break;
                    case GAME_END:
                        events.add(new GameEndEvent(AnnouncerNotificationType.GAME_END, newEvent.getAnnouncerEventTime(), newEvent.getAnnouncerEventID(), incomingAllGameData, newEvent.getResult()));
                        break;
                    case GAME_START:
                        events.add(new GameStartEvent(AnnouncerNotificationType.GAME_START, newEvent.getAnnouncerEventTime(), newEvent.getAnnouncerEventID(), incomingAllGameData));
                        break;
                    case HERALD_KILL:
                        events.add(new HeraldKillEvent(AnnouncerNotificationType.HERALD_KILL, newEvent.getAnnouncerEventTime(), newEvent.getAnnouncerEventID(), incomingAllGameData, newEvent.getStolen(), newEvent.getKillerName(), newEvent.getAssisters()));
                        break;
                    case INHIBITOR_KILL:
                        events.add(new InhibitorKillEvent(AnnouncerNotificationType.INHIBITOR_KILL, newEvent.getAnnouncerEventTime(), newEvent.getAnnouncerEventID(), incomingAllGameData, newEvent.getInhibKilled(), newEvent.getKillerName(), newEvent.getAssisters()));
                        break;
                    case INHIBITOR_RESPAWN:
                        events.add(new InhibitorRespawnEvent(AnnouncerNotificationType.INHIBITOR_RESPAWN, newEvent.getAnnouncerEventTime(), newEvent.getAnnouncerEventID(), incomingAllGameData, newEvent.getInhibRespawned()));
                        break;
                    case INHIBITOR_RESPAWNING_SOON:
                        events.add(new InhibitorRespawingSoonEvent(AnnouncerNotificationType.INHIBITOR_RESPAWNING_SOON, newEvent.getAnnouncerEventTime(), newEvent.getAnnouncerEventID(), incomingAllGameData, newEvent.getInhibRespawningSoon()));
                        break;
                    case MINIONS_SPAWNING:
                        events.add(new MinionsSpawningEvent(AnnouncerNotificationType.MINIONS_SPAWNING, newEvent.getAnnouncerEventTime(), newEvent.getAnnouncerEventID(), incomingAllGameData));
                        break;
                    case MULTIKILL:
                        events.add(new MultikillEvent(AnnouncerNotificationType.MULTIKILL, newEvent.getAnnouncerEventTime(), newEvent.getAnnouncerEventID(), incomingAllGameData, newEvent.getKillerName(), newEvent.getKillStreak()));
                        break;
                    case TURRET_KILL:
                        events.add(new TurretKillEvent(AnnouncerNotificationType.TURRET_KILL, newEvent.getAnnouncerEventTime(), newEvent.getAnnouncerEventID(), incomingAllGameData, newEvent.getTurretKilled(), newEvent.getKillerName(), newEvent.getAssisters()));
                        break;
                }
            }
        }
        return events;
    }
}
