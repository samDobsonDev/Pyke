package com.samdobsondev.lcde4j.api;

import com.samdobsondev.lcde4j.model.data.AllGameData;
import com.samdobsondev.lcde4j.model.data.allplayers.Item;
import com.samdobsondev.lcde4j.model.data.allplayers.Player;
import com.samdobsondev.lcde4j.model.events.allplayers.AllPlayersEvent;
import com.samdobsondev.lcde4j.model.events.allplayers.AllPlayersEventType;

import java.util.ArrayList;
import java.util.List;

public class AllPlayersEventDetector
{
    public List<AllPlayersEvent> detectEvents(AllGameData currentAllGameData, AllGameData incomingAllGameData, Double eventTime) {
        List<Player> current = currentAllGameData.getAllPlayers();
        List<Player> incoming = incomingAllGameData.getAllPlayers();

        List<AllPlayersEvent> events = new ArrayList<>();

        checkForPlayersJoined(events, current, incoming, incomingAllGameData, eventTime);

        // For each Player object in the current list, compare with the corresponding Player in the incoming list
        for (int i = 0; i < current.size(); i++) {
            Player currentPlayer = current.get(i);
            Player incomingPlayer = incoming.get(i);

            // Check for dead/alive changes
            checkDeadAliveChanges(events, currentPlayer, incomingPlayer, incomingAllGameData, eventTime);

            // Check for item changes
            checkItemChanges(events, currentPlayer, incomingPlayer, incomingAllGameData, eventTime);
        }

        return events;
    }

    private void checkForPlayersJoined(List<AllPlayersEvent> events, List<Player> current, List<Player> incoming, AllGameData incomingAllGameData, Double eventTime) {
        // Check for changes in the size of the current and incoming lists (total players). PLAYER_JOINED events thrown on size increase
        if (incoming.size() > current.size()) {
            List<Player> newPlayers = incoming.subList(current.size(), incoming.size());
            for (Player player : newPlayers) {
                AllPlayersEvent event = new AllPlayersEvent();
                event.setAllPlayersEventType(AllPlayersEventType.PLAYER_JOINED);
                event.setAllPlayersEventTime(eventTime);
                event.setAllGameData(incomingAllGameData);
                event.setPlayer(player);
                // add all static player info here
                event.setChampionName(player.getChampionName());
                event.setIsBot(player.getIsBot());
                event.setPosition(player.getPosition());
                event.setRawChampionName(player.getRawChampionName());
                event.setKeystone(player.getRunes().getKeystone());
                event.setPrimaryRuneTree(player.getRunes().getPrimaryRuneTree());
                event.setSecondaryRuneTree(player.getRunes().getSecondaryRuneTree());
                event.setSkinID(player.getSkinID());
                event.setSummonerName(player.getSummonerName());
                event.setSummonerSpellOne(player.getSummonerSpells().getSummonerSpellOne());
                event.setSummonerSpellTwo(player.getSummonerSpells().getSummonerSpellTwo());
                event.setTeam(player.getTeam());
                events.add(event);
            }
        }
    }

    private void checkDeadAliveChanges(List<AllPlayersEvent> events, Player currentPlayer, Player incomingPlayer, AllGameData incomingAllGameData, Double eventTime) {
        boolean currentPlayerIsDead = Boolean.TRUE.equals(currentPlayer.getIsDead());
        boolean incomingPlayerIsDead = Boolean.TRUE.equals(incomingPlayer.getIsDead());

        if (!currentPlayerIsDead && incomingPlayerIsDead || currentPlayerIsDead && !incomingPlayerIsDead) {
            AllPlayersEvent event = new AllPlayersEvent();
            event.setAllPlayersEventTime(eventTime);
            event.setAllGameData(incomingAllGameData);
            event.setPlayer(incomingPlayer);
            event.setChampionName(incomingPlayer.getChampionName());
            event.setIsDead(incomingPlayer.getIsDead());
            event.setSummonerName(incomingPlayer.getSummonerName());
            event.setDeaths(incomingPlayer.getScores().getDeaths());

            if (!currentPlayerIsDead) {
                // Player has died
                event.setAllPlayersEventType(AllPlayersEventType.DEATH);
            } else {
                // Player has respawned
                event.setAllPlayersEventType(AllPlayersEventType.RESPAWN);
            }

            events.add(event);
        }
    }

    private void checkItemChanges(List<AllPlayersEvent> events, Player currentPlayer, Player incomingPlayer, AllGameData incomingAllGameData, Double eventTime) {
        List<Item> currentItems = currentPlayer.getItems();
        List<Item> incomingItems = incomingPlayer.getItems();

        // If the size of the incoming items list is greater, a new item has been bought
        if (incomingItems.size() > currentItems.size()) {
            // Filter out the item/s that are not in the current list to get a list of the new item/s
            List<Item> newItems = incomingItems.stream()
                    .filter(item -> !currentItems.contains(item))
                    .toList();

            // Generate an ITEM_BOUGHT event for every new item in the list
            for (Item newItem : newItems) {
                AllPlayersEvent event = new AllPlayersEvent();
                event.setAllPlayersEventType(AllPlayersEventType.ITEM_BOUGHT);
                generateItemBoughtSoldEvent(events, incomingPlayer, incomingAllGameData, eventTime, newItem, event);
            }
        }

        // If the size of the incoming items list is less, an item has been sold (TODO: not necessarily, it could have been used (potions, Your Cut, etc.)
        else if (incomingItems.size() < currentItems.size()) {
            // Filter out the items that are not in the incoming list
            List<Item> soldItems = currentItems.stream()
                    .filter(item -> !incomingItems.contains(item))
                    .toList();

            for (Item soldItem : soldItems) {
                AllPlayersEvent event = new AllPlayersEvent();
                event.setAllPlayersEventType(AllPlayersEventType.ITEM_SOLD);
                generateItemBoughtSoldEvent(events, incomingPlayer, incomingAllGameData, eventTime, soldItem, event);
            }
        }

        // If the size of the incoming items list is equal to the current items list size, there could be two scenarios:
        // 1. An item has transformed/evolved (ITEM_TRANSFORMATION)
        // 2. An item has been moved to another slot (ITEM_SLOT_CHANGE)
        else if (incomingItems.size() == currentItems.size()) {
            for (int i = 0; i < currentItems.size(); i++) {
                Item currentItem = currentItems.get(i);
                Item incomingItem = incomingItems.get(i);

                // If the item at the same index in both lists is not the same, an item may have evolved or moved
                // Further comparisons may be needed to accurately determine the event
            }
        }
    }

    private void generateItemBoughtSoldEvent(List<AllPlayersEvent> events, Player incomingPlayer, AllGameData incomingAllGameData, Double eventTime, Item soldItem, AllPlayersEvent event)
    {
        event.setAllPlayersEventTime(eventTime);
        event.setAllGameData(incomingAllGameData);
        event.setPlayer(incomingPlayer);
        event.setChampionName(incomingPlayer.getChampionName());
        event.setItem(soldItem.getDisplayName());
        event.setItemID(soldItem.getItemID());
        event.setItemPrice(soldItem.getPrice());
        event.setItemSlot(soldItem.getSlot());
        event.setSummonerName(incomingPlayer.getSummonerName());
        events.add(event);
    }
}
