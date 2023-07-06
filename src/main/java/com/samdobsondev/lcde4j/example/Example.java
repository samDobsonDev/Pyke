package com.samdobsondev.lcde4j.example;

import com.samdobsondev.lcde4j.api.LCDE4J;
import com.samdobsondev.lcde4j.api.listener.ActivePlayerEventListener;
import com.samdobsondev.lcde4j.api.listener.AllPlayersEventListener;
import com.samdobsondev.lcde4j.api.listener.AnnouncerNotificationEventListener;
import com.samdobsondev.lcde4j.api.listener.GameDataEventListener;
import com.samdobsondev.lcde4j.model.events.activeplayer.ActivePlayerLevelUpEvent;
import com.samdobsondev.lcde4j.model.events.allplayers.*;
import com.samdobsondev.lcde4j.model.events.announcer.FirstBloodEvent;
import com.samdobsondev.lcde4j.model.events.announcer.GameEndEvent;
import com.samdobsondev.lcde4j.model.events.announcer.MinionsSpawningEvent;
import com.samdobsondev.lcde4j.model.events.gamedata.MapTerrainChangeEvent;

public class Example {
    public static void main(String[] args) {
        LCDE4J lcde4J = new LCDE4J();
        lcde4J.start(); // starts the port watching and subsequent polling

        lcde4J.registerActivePlayerEventListener(new ActivePlayerEventListener() {
            @Override
            public void onLevelUp(ActivePlayerLevelUpEvent event) {
                System.out.println(event.getAllGameData().getActivePlayer().getSummonerName() + " leveled up to level " + event.getLevel());

                if (event.getLevel() >= 16) {
                    lcde4J.stop(); // stops the polling and port watching
                }
            }
        });

        lcde4J.registerAllPlayersEventListener(new AllPlayersEventListener() {
            @Override
            public void onEyeOfHeraldUsedOrLost(EyeOfHeraldUsedOrLostEvent event) {
                System.out.println(event.getChampionName() + " used or lost the Herald!");
            }

            @Override
            public void onItemAcquired(ItemAcquiredEvent event) {
                System.out.println(event.getChampionName() + " acquired " + event.getAcquiredItem().getDisplayName());
            }

            @Override
            public void onItemSlotChange(ItemSlotChangeEvent event) {
                System.out.println(event.getChampionName() + " moved the item: " + event.getItem().getDisplayName() + " from slot " + event.getOldSlot() + " to slot " + event.getNewSlot());
            }

            @Override
            public void onItemSoldOrConsumed(ItemSoldOrConsumedEvent event) {
                System.out.println(event.getChampionName() + " sold or consumed the item: " + event.getSoldOrConsumedItem().getDisplayName());
            }

            @Override
            public void onItemTransformation(ItemTransformationEvent event) {
                System.out.println(event.getChampionName() + "'s " + event.getOldItem().getDisplayName() + " transformed into a " + event.getNewItem().getDisplayName() + "!");
            }
        });

        lcde4J.registerAnnouncerNotificationEventListener(new AnnouncerNotificationEventListener() {
            @Override
            public void onMinionsSpawning(MinionsSpawningEvent event) {
                System.out.println("Minions Spawning!");
            }

            @Override
            public void onFirstBlood(FirstBloodEvent event) {
                System.out.println(event.getRecipient() + " got first blood!");
            }

            @Override
            public void onGameEnd(GameEndEvent event) {
                lcde4J.stop();
            }
        });

        lcde4J.registerGameDataEventListener(new GameDataEventListener() {
            @Override
            public void onMapTerrainChange(MapTerrainChangeEvent event) {
                System.out.println("Map Terrain has changed to: " + event.getMapTerrain());
            }
        });
    }
}

