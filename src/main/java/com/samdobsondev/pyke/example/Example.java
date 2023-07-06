package com.samdobsondev.pyke.example;

import com.samdobsondev.pyke.api.Pyke;
import com.samdobsondev.pyke.api.listener.ActivePlayerEventListener;
import com.samdobsondev.pyke.api.listener.AllPlayersEventListener;
import com.samdobsondev.pyke.api.listener.AnnouncerNotificationEventListener;
import com.samdobsondev.pyke.api.listener.GameDataEventListener;
import com.samdobsondev.pyke.model.events.activeplayer.AbilityLevelUpEvent;
import com.samdobsondev.pyke.model.events.activeplayer.ActivePlayerLevelUpEvent;
import com.samdobsondev.pyke.model.events.activeplayer.PassiveEvent;
import com.samdobsondev.pyke.model.events.allplayers.*;
import com.samdobsondev.pyke.model.events.announcer.FirstBloodEvent;
import com.samdobsondev.pyke.model.events.announcer.GameEndEvent;
import com.samdobsondev.pyke.model.events.announcer.MinionsSpawningEvent;
import com.samdobsondev.pyke.model.events.gamedata.MapTerrainChangeEvent;

public class Example {
    public static void main(String[] args) {
        Pyke pyke = new Pyke();
        pyke.start(); // starts the port watching and subsequent polling

        pyke.registerActivePlayerEventListener(new ActivePlayerEventListener() {
            @Override
            public void onLevelUp(ActivePlayerLevelUpEvent event) {
                System.out.println(event.getAllGameData().getActivePlayer().getSummonerName() + " leveled up to level " + event.getLevel());

                if (event.getLevel() >= 16) {
                    pyke.stop(); // stops the polling and port watching
                }
            }

            @Override
            public void onAbilityLevelUp(AbilityLevelUpEvent event) {
                System.out.println(event.getAbility() + " is level " + event.getAbilityLevel());
            }

            @Override
            public void onPassive(PassiveEvent event) {
                System.out.println(event.getAllGameData().getActivePlayer().getSummonerName() + "'s passive is " + event.getPassive().getDisplayName());
            }
        });

        pyke.registerAllPlayersEventListener(new AllPlayersEventListener() {
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

        pyke.registerAnnouncerNotificationEventListener(new AnnouncerNotificationEventListener() {
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
                pyke.stop();
            }
        });

        pyke.registerGameDataEventListener(new GameDataEventListener() {
            @Override
            public void onMapTerrainChange(MapTerrainChangeEvent event) {
                System.out.println("Map Terrain has changed to: " + event.getMapTerrain());
            }
        });
    }
}

