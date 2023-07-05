package com.samdobsondev.lcde4j.api.detector;

import com.samdobsondev.lcde4j.model.data.AllGameData;
import com.samdobsondev.lcde4j.model.data.activeplayer.ActivePlayer;
import com.samdobsondev.lcde4j.model.data.activeplayer.championstats.ChampionStats;
import com.samdobsondev.lcde4j.model.data.activeplayer.fullrunes.StatRune;
import com.samdobsondev.lcde4j.model.data.common.Rune;
import com.samdobsondev.lcde4j.model.data.common.RuneTree;
import com.samdobsondev.lcde4j.model.events.activeplayer.ActivePlayerEvent;
import com.samdobsondev.lcde4j.model.events.activeplayer.ActivePlayerEventType;
import com.samdobsondev.lcde4j.model.data.activeplayer.abilities.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ActivePlayerEventDetector {
    public List<ActivePlayerEvent> detectEvents(AllGameData currentAllGameData, AllGameData incomingAllGameData, Double eventTime) {
        ActivePlayer current = currentAllGameData.getActivePlayer();
        ActivePlayer incoming = incomingAllGameData.getActivePlayer();

        List<ActivePlayerEvent> events = new ArrayList<>();

        // Check for ability level changes
        checkAbilityLevelChange(events, current.getAbilities().getQ(), incoming.getAbilities().getQ(), eventTime, "Q", incomingAllGameData);
        checkAbilityLevelChange(events, current.getAbilities().getW(), incoming.getAbilities().getW(), eventTime, "W", incomingAllGameData);
        checkAbilityLevelChange(events, current.getAbilities().getE(), incoming.getAbilities().getE(), eventTime, "E", incomingAllGameData);
        checkAbilityLevelChange(events, current.getAbilities().getR(), incoming.getAbilities().getR(), eventTime, "R", incomingAllGameData);

        // Check for Stat changes
        checkForStatChange(events, current.getChampionStats(), incoming.getChampionStats(), eventTime, incomingAllGameData);

        // Check for gold change
        if (!current.getCurrentGold().equals(incoming.getCurrentGold())) {
            ActivePlayerEvent event = new ActivePlayerEvent();
            event.setActivePlayerEventType(ActivePlayerEventType.GOLD_CHANGE);
            event.setActivePlayerEventTime(eventTime);
            event.setGoldAmount(incoming.getCurrentGold());
            event.setAllGameData(incomingAllGameData);
            events.add(event);
        }

        // Check for Runes
        checkForGeneralRunes(events, current.getFullRunes().getGeneralRunes(), incoming.getFullRunes().getGeneralRunes(), eventTime, incomingAllGameData);
        checkForKeystone(events, current.getFullRunes().getKeystone(), incoming.getFullRunes().getKeystone(), eventTime, incomingAllGameData);
        checkForPrimaryRuneTree(events, current.getFullRunes().getPrimaryRuneTree(), incoming.getFullRunes().getPrimaryRuneTree(), eventTime, incomingAllGameData);
        checkForSecondaryRuneTree(events, current.getFullRunes().getSecondaryRuneTree(), incoming.getFullRunes().getSecondaryRuneTree(), eventTime, incomingAllGameData);
        checkForStatRunes(events, current.getFullRunes().getStatRunes(), incoming.getFullRunes().getStatRunes(), eventTime, incomingAllGameData);

        // Check for level change
        if (!current.getLevel().equals(incoming.getLevel())) {
            ActivePlayerEvent event = new ActivePlayerEvent();
            event.setActivePlayerEventType(ActivePlayerEventType.LEVEL_UP);
            event.setActivePlayerEventTime(eventTime);
            event.setLevel(incoming.getLevel());
            event.setAllGameData(incomingAllGameData);
            events.add(event);
        }

        // Check for Summoner Name
        if (!current.getSummonerName().equals(incoming.getSummonerName())) {
            ActivePlayerEvent event = new ActivePlayerEvent();
            event.setActivePlayerEventType(ActivePlayerEventType.SUMMONER_NAME);
            event.setActivePlayerEventTime(eventTime);
            event.setSummonerName(incoming.getSummonerName());
            event.setAllGameData(incomingAllGameData);
            events.add(event);
        }

        // Check for Team Relative Colors
        if (!current.getTeamRelativeColors().equals(incoming.getTeamRelativeColors())) {
            ActivePlayerEvent event = new ActivePlayerEvent();
            event.setActivePlayerEventType(ActivePlayerEventType.TEAM_RELATIVE_COLORS_CHANGE);
            event.setActivePlayerEventTime(eventTime);
            event.setTeamRelativeColors(incoming.getTeamRelativeColors());
            event.setAllGameData(incomingAllGameData);
            events.add(event);
        }

        return events;
    }

    private void checkAbilityLevelChange(List<ActivePlayerEvent> events, Ability currentAbility, Ability incomingAbility, Double eventTime, String abilityName, AllGameData incoming) {
        if (!currentAbility.getAbilityLevel().equals(incomingAbility.getAbilityLevel())) {
            ActivePlayerEvent event = new ActivePlayerEvent();
            event.setActivePlayerEventType(ActivePlayerEventType.ABILITY_LEVEL_UP);
            event.setActivePlayerEventTime(eventTime);
            event.setAbility(abilityName);
            event.setAbilityLevel(incomingAbility.getAbilityLevel().toString());
            event.setAllGameData(incoming);
            events.add(event);
        }
    }

    private void checkForStatChange(List<ActivePlayerEvent> events, ChampionStats currentStats, ChampionStats incomingStats, Double eventTime, AllGameData incoming) {
        Field[] fields = ChampionStats.class.getDeclaredFields();

        for (Field field : fields) {
            try {
                field.setAccessible(true);
                if (field.getType().equals(String.class)) {
                    String currentStat = (String) field.get(currentStats);
                    String incomingStat = (String) field.get(incomingStats);

                    if (!currentStat.equals(incomingStat)) {
                        ActivePlayerEvent event = new ActivePlayerEvent();
                        event.setActivePlayerEventType(ActivePlayerEventType.RESOURCE_TYPE);
                        event.setActivePlayerEventTime(eventTime);
                        event.setChampionStat(field.getName());
                        event.setResourceType(incomingStat);
                        event.setAllGameData(incoming);
                        events.add(event);
                    }
                } else if (field.getType().equals(Double.class)) {
                    Double currentStat = (Double) field.get(currentStats);
                    Double incomingStat = (Double) field.get(incomingStats);

                    if (!currentStat.equals(incomingStat)) {
                        ActivePlayerEvent event = new ActivePlayerEvent();
                        event.setActivePlayerEventType(ActivePlayerEventType.STAT_CHANGE);
                        event.setActivePlayerEventTime(eventTime);
                        event.setChampionStat(field.getName());
                        event.setChampionStatAmount(incomingStat);
                        event.setAllGameData(incoming);
                        events.add(event);
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private void checkForGeneralRunes(List<ActivePlayerEvent> events, List<Rune> currentRunes, List<Rune> incomingRunes, Double eventTime, AllGameData incoming) {
        for (Rune incomingRune : incomingRunes) {
            if (!currentRunes.contains(incomingRune)) {
                ActivePlayerEvent event = new ActivePlayerEvent();
                event.setActivePlayerEventType(ActivePlayerEventType.GENERAL_RUNE);
                event.setActivePlayerEventTime(eventTime);
                event.setRune(incomingRune);
                event.setAllGameData(incoming);
                events.add(event);
            }
        }
    }

    private void checkForKeystone(List<ActivePlayerEvent> events, Rune currentRune, Rune incomingRune, Double eventTime, AllGameData incoming) {
        if (!currentRune.equals(incomingRune)) {
            ActivePlayerEvent event = new ActivePlayerEvent();
            event.setActivePlayerEventType(ActivePlayerEventType.KEYSTONE);
            event.setActivePlayerEventTime(eventTime);
            event.setRune(incomingRune);
            event.setAllGameData(incoming);
            events.add(event);
        }
    }

    private void checkForPrimaryRuneTree(List<ActivePlayerEvent> events, RuneTree currentRuneTree, RuneTree incomingRuneTree, Double eventTime, AllGameData incoming) {
        if (!currentRuneTree.equals(incomingRuneTree)) {
            ActivePlayerEvent event = new ActivePlayerEvent();
            event.setActivePlayerEventType(ActivePlayerEventType.PRIMARY_RUNE_TREE);
            event.setActivePlayerEventTime(eventTime);
            event.setRuneTree(incomingRuneTree);
            event.setAllGameData(incoming);
            events.add(event);
        }
    }

    private void checkForSecondaryRuneTree(List<ActivePlayerEvent> events, RuneTree currentRuneTree, RuneTree incomingRuneTree, Double eventTime, AllGameData incoming) {
        if (!currentRuneTree.equals(incomingRuneTree)) {
            ActivePlayerEvent event = new ActivePlayerEvent();
            event.setActivePlayerEventType(ActivePlayerEventType.SECONDARY_RUNE_TREE);
            event.setActivePlayerEventTime(eventTime);
            event.setRuneTree(incomingRuneTree);
            event.setAllGameData(incoming);
            events.add(event);
        }
    }

    private void checkForStatRunes(List<ActivePlayerEvent> events, List<StatRune> currentStatRunes, List<StatRune> incomingStatRunes, Double eventTime, AllGameData incoming) {
        for (StatRune incomingStatRune : incomingStatRunes) {
            if (!currentStatRunes.contains(incomingStatRune)) {
                ActivePlayerEvent event = new ActivePlayerEvent();
                event.setActivePlayerEventType(ActivePlayerEventType.STAT_RUNE);
                event.setActivePlayerEventTime(eventTime);
                event.setStatRune(incomingStatRune);
                event.setAllGameData(incoming);
                events.add(event);
            }
        }
    }
}