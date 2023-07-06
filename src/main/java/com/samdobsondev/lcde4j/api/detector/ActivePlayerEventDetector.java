package com.samdobsondev.lcde4j.api.detector;

import com.samdobsondev.lcde4j.model.data.AllGameData;
import com.samdobsondev.lcde4j.model.data.activeplayer.ActivePlayer;
import com.samdobsondev.lcde4j.model.data.activeplayer.championstats.ChampionStats;
import com.samdobsondev.lcde4j.model.data.activeplayer.fullrunes.StatRune;
import com.samdobsondev.lcde4j.model.data.common.Rune;
import com.samdobsondev.lcde4j.model.data.common.RuneTree;
import com.samdobsondev.lcde4j.model.events.activeplayer.*;
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
            GoldChangeEvent event = new GoldChangeEvent(ActivePlayerEventType.GOLD_CHANGE,
                    eventTime,
                    incomingAllGameData,
                    incoming.getCurrentGold());
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
            ActivePlayerLevelUpEvent event = new ActivePlayerLevelUpEvent(ActivePlayerEventType.LEVEL_UP,
                    eventTime,
                    incomingAllGameData,
                    incoming.getLevel());
            events.add(event);
        }

        // Check for Summoner Name
        if (!current.getSummonerName().equals(incoming.getSummonerName())) {
            SummonerNameEvent event = new SummonerNameEvent(ActivePlayerEventType.SUMMONER_NAME,
                    eventTime,
                    incomingAllGameData,
                    incoming.getSummonerName());
            events.add(event);
        }

        // Check for Team Relative Colors
        if (!current.getTeamRelativeColors().equals(incoming.getTeamRelativeColors())) {
            TeamRelativeColorsChangeEvent event = new TeamRelativeColorsChangeEvent(ActivePlayerEventType.TEAM_RELATIVE_COLORS_CHANGE,
                    eventTime,
                    incomingAllGameData,
                    incoming.getTeamRelativeColors());
            events.add(event);
        }

        return events;
    }

    private void checkAbilityLevelChange(List<ActivePlayerEvent> events, Ability currentAbility, Ability incomingAbility, Double eventTime, String abilityName, AllGameData incoming) {
        if (!currentAbility.getAbilityLevel().equals(incomingAbility.getAbilityLevel())) {
            AbilityLevelUpEvent event = new AbilityLevelUpEvent(ActivePlayerEventType.ABILITY_LEVEL_UP,
                    eventTime,
                    incoming,
                    abilityName,
                    incomingAbility.getAbilityLevel());
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
                        ResourceTypeChangeEvent event = new ResourceTypeChangeEvent(ActivePlayerEventType.RESOURCE_TYPE,
                                eventTime,
                                incoming,
                                incomingStat);
                        events.add(event);
                    }
                } else if (field.getType().equals(Double.class)) {
                    Double currentStat = (Double) field.get(currentStats);
                    Double incomingStat = (Double) field.get(incomingStats);
                    if (!currentStat.equals(incomingStat)) {
                        StatChangeEvent event = new StatChangeEvent(ActivePlayerEventType.STAT_CHANGE,
                                eventTime,
                                incoming,
                                field.getName(),
                                incomingStat);
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
                GeneralRuneEvent event = new GeneralRuneEvent(ActivePlayerEventType.GENERAL_RUNE,
                        eventTime,
                        incoming,
                        incomingRune);
                events.add(event);
            }
        }
    }

    private void checkForKeystone(List<ActivePlayerEvent> events, Rune currentRune, Rune incomingRune, Double eventTime, AllGameData incoming) {
        if (!currentRune.equals(incomingRune)) {
            KeystoneEvent event = new KeystoneEvent(ActivePlayerEventType.KEYSTONE,
                    eventTime,
                    incoming,
                    incomingRune);
            events.add(event);
        }
    }

    private void checkForPrimaryRuneTree(List<ActivePlayerEvent> events, RuneTree currentRuneTree, RuneTree incomingRuneTree, Double eventTime, AllGameData incoming) {
        if (!currentRuneTree.equals(incomingRuneTree)) {
            PrimaryRuneTreeEvent event = new PrimaryRuneTreeEvent(ActivePlayerEventType.PRIMARY_RUNE_TREE,
                    eventTime,
                    incoming,
                    incomingRuneTree);
            events.add(event);
        }
    }

    private void checkForSecondaryRuneTree(List<ActivePlayerEvent> events, RuneTree currentRuneTree, RuneTree incomingRuneTree, Double eventTime, AllGameData incoming) {
        if (!currentRuneTree.equals(incomingRuneTree)) {
            SecondaryRuneTreeEvent event = new SecondaryRuneTreeEvent(ActivePlayerEventType.SECONDARY_RUNE_TREE,
                    eventTime,
                    incoming,
                    incomingRuneTree);
            events.add(event);
        }
    }

    private void checkForStatRunes(List<ActivePlayerEvent> events, List<StatRune> currentStatRunes, List<StatRune> incomingStatRunes, Double eventTime, AllGameData incoming) {
        for (StatRune incomingStatRune : incomingStatRunes) {
            if (!currentStatRunes.contains(incomingStatRune)) {
                StatRuneEvent event = new StatRuneEvent(ActivePlayerEventType.STAT_RUNE,
                        eventTime,
                        incoming,
                        incomingStatRune);
                events.add(event);
            }
        }
    }
}