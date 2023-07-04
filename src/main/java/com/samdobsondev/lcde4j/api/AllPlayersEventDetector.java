package com.samdobsondev.lcde4j.api;

import com.samdobsondev.lcde4j.model.data.AllGameData;
import com.samdobsondev.lcde4j.model.data.allplayers.Item;
import com.samdobsondev.lcde4j.model.data.allplayers.Player;
import com.samdobsondev.lcde4j.model.events.allplayers.AllPlayersEvent;
import com.samdobsondev.lcde4j.model.events.allplayers.AllPlayersEventType;

import java.util.*;

public class AllPlayersEventDetector
{
    public List<AllPlayersEvent> detectEvents(AllGameData currentAllGameData, AllGameData incomingAllGameData, Double eventTime) {
        List<Player> current = currentAllGameData.getAllPlayers();
        List<Player> incoming = incomingAllGameData.getAllPlayers();

        List<AllPlayersEvent> events = new ArrayList<>();

        // This will contain all the expected changes if the LCDE4J library goes down and launches again mid-game
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
        // TODO: Need to handle buying items from components items (also wards), which will take up the same slot rather than a new slot and not trigger an ITEM_BOUGHT event in the current implementation
        if (incomingItems.size() > currentItems.size()) {
            // Filter out the item/s that are not in the current list to get a list of the new item/s
            List<Item> newItems = incomingItems.stream()
                    .filter(item -> !currentItems.contains(item))
                    .toList();

            // Generate an ITEM_BOUGHT event for every new item in the list
            for (Item newItem : newItems) {
                AllPlayersEvent event = new AllPlayersEvent();
                event.setAllPlayersEventType(AllPlayersEventType.ITEM_ACQUIRED);
                generateItemAcquiredOrSoldEvent(events, incomingPlayer, incomingAllGameData, eventTime, newItem, event);
            }
        }

        // If the size of the incoming items list is smaller, an item has been sold or consumed
        // TODO: Need to handle the undo function when we undo the purchase of an item into it's components
        else if (incomingItems.size() < currentItems.size()) {
            // Filter out the items that are not in the incoming list
            List<Item> soldItems = currentItems.stream()
                    .filter(item -> !incomingItems.contains(item))
                    .toList();

            for (Item soldItem : soldItems) {
                AllPlayersEvent event = new AllPlayersEvent();
                event.setAllPlayersEventType(AllPlayersEventType.ITEM_SOLD_OR_CONSUMED);
                generateItemAcquiredOrSoldEvent(events, incomingPlayer, incomingAllGameData, eventTime, soldItem, event);
            }
        }

        // If the currentItems and incomingItems lists are the same size...
        else {
            detectSlotChanges(events, incomingPlayer, incomingAllGameData, eventTime, currentItems, incomingItems);
            for (int i = 0; i < currentItems.size(); i++) {
                Item currentItem = currentItems.get(i);
                Item incomingItem = incomingItems.get(i);

                // If the item at the same index in both lists is not the same, the item may have transformed or been used (herald)
                if (!currentItem.equals(incomingItem)) {
                    detectItemTransformation(events, incomingPlayer, incomingAllGameData, eventTime, currentItem, incomingItem);
                    detectHeraldUsage(events, incomingPlayer, incomingAllGameData, eventTime, currentItem, incomingItem);
                }
            }
        }
    }

    private void generateItemAcquiredOrSoldEvent(List<AllPlayersEvent> events, Player incomingPlayer, AllGameData incomingAllGameData, Double eventTime, Item item, AllPlayersEvent event)
    {
        event.setAllPlayersEventTime(eventTime);
        event.setAllGameData(incomingAllGameData);
        event.setPlayer(incomingPlayer);
        event.setChampionName(incomingPlayer.getChampionName());
        event.setItem(item);
        event.setSummonerName(incomingPlayer.getSummonerName());
        events.add(event);
    }

    private void detectSlotChanges(List<AllPlayersEvent> events, Player incomingPlayer, AllGameData incomingAllGameData, Double eventTime, List<Item> currentItems, List<Item> incomingItems) {

        if (containsSameItems(currentItems, incomingItems)) {
            for (int i = 0; i < currentItems.size(); i++) {
                Item currentItem = currentItems.get(i);
                Item incomingItem = incomingItems.get(i);

                // If the item is the same but the slot has changed
                if (currentItem.getItemID().equals(incomingItem.getItemID()) && !currentItem.getSlot().equals(incomingItem.getSlot())) {
                    AllPlayersEvent event = new AllPlayersEvent();
                    event.setAllPlayersEventType(AllPlayersEventType.ITEM_SLOT_CHANGE);
                    event.setAllPlayersEventTime(eventTime);
                    event.setAllGameData(incomingAllGameData);
                    event.setPlayer(incomingPlayer);
                    event.setChampionName(incomingPlayer.getChampionName());
                    event.setItem(incomingItem);
                    event.setOldItemSlot(currentItem.getSlot());
                    event.setNewItemSlot(incomingItem.getSlot());
                    event.setSummonerName(incomingPlayer.getSummonerName());
                    events.add(event);
                }
            }
        }
    }

    // Returns if two List<Item> lists contain the same Items regardless of index
    private boolean containsSameItems(List<Item> list1, List<Item> list2) {
        Map<Long, Integer> itemCount = new HashMap<>();

        // Count items in the first list
        for (Item item : list1) {
            itemCount.put(item.getItemID(), itemCount.getOrDefault(item.getItemID(), 0) + 1);
        }

        // Subtract the count with items from the second list
        for (Item item : list2) {
            if (!itemCount.containsKey(item.getItemID())) return false;
            if (itemCount.get(item.getItemID()) == 1) {
                itemCount.remove(item.getItemID());
            } else {
                itemCount.put(item.getItemID(), itemCount.get(item.getItemID()) - 1);
            }
        }

        // If there are items left, the lists are not the same
        return itemCount.isEmpty();
    }

    private void detectItemTransformation(List<AllPlayersEvent> events, Player incomingPlayer, AllGameData incomingAllGameData, Double eventTime, Item currentItem, Item incomingItem) {
        Map<Long, Long> transformations = getPossibleTransformations();
        // Check if this item is in the transformation map
        if (transformations.containsKey(currentItem.getItemID())) {
            Long nextItemInTransformation = transformations.get(currentItem.getItemID());

            // Check if the incoming item is the next item in the transformation sequence
            if (nextItemInTransformation.equals(incomingItem.getItemID())) {
                // The item has transformed
                AllPlayersEvent event = new AllPlayersEvent();
                event.setAllPlayersEventType(AllPlayersEventType.ITEM_TRANSFORMATION);
                generateItemTransformationEvent(events, incomingPlayer, incomingAllGameData, eventTime, currentItem, incomingItem, event);
            }
        }
    }

    private Map<Long, Long> getPossibleTransformations() {
        Map<Long, Long> transformations = new HashMap<>();
        transformations.put(3003L, 3040L); // Archangel's Staff -> Seraph's Embrace
        transformations.put(3119L, 3121L); // Winter's Approach -> Fimbulwinter
        transformations.put(3004L, 3042L); // Manamune -> Muramana
        transformations.put(2420L, 2421L); // Stopwatch -> Broken Stopwatch
        transformations.put(2419L, 2423L); // Commencing Stopwatch -> Perfectly Timed Stopwatch
        transformations.put(2423L, 2421L); // Perfectly Timed Stopwatch -> Broken Stopwatch
        transformations.put(3854L, 3855L); // Steel Shoulderguards -> Runesteel Spaulders
        transformations.put(3855L, 3875L); // Runesteel Spaulders -> Pauldrons of Whiterock
        transformations.put(3862L, 3863L); // Spectral Sickle -> Harrowing Crescent
        transformations.put(3863L, 3864L); // Harrowing Crescent -> Black Mist Scythe
        transformations.put(3858L, 3859L); // Relic Shield -> Targon's Buckler
        transformations.put(3859L, 3860L); // Targon's Buckler -> Bulwark of the Mountain
        transformations.put(3850L, 3851L); // Spellthief's Edge -> Frostfang
        transformations.put(3851L, 3853L); // Frostfang -> Shard of True Ice
        transformations.put(4644L, 7024L); // Crown of the Shattered Queen -> Caesura
        transformations.put(6630L, 7015L); // Goredrinker -> Ceaseless Hunger
        transformations.put(6620L, 7033L); // Echoes of Helia -> Cry of the Shrieking City
        transformations.put(6632L, 7017L); // Divine Sunderer -> Deicide
        transformations.put(6691L, 7002L); // Duskblade of Draktharr -> Draktharr's Shadowcarver
        transformations.put(6631L, 7016L); // Stridebreaker -> Dreamshatter
        transformations.put(3031L, 7031L); // Infinity Edge -> Edge of Finality
        transformations.put(3001L, 7023L); // Evenshroud -> Equinox
        transformations.put(6656L, 7014L); // Everfrost -> Eternal Winter
        transformations.put(6655L, 7013L); // Luden's Tempest -> Eye of Luden
        transformations.put(6675L, 7032L); // Navori Quickblades -> Flicker
        transformations.put(6662L, 7005L); // Iceborn Gauntlet -> Frozen Fist
        transformations.put(4633L, 7009L); // Riftmaker -> Icathia's Curse
        transformations.put(6657L, 7028L); // Rod of Ages -> Infinite Convergence
        transformations.put(3078L, 7018L); // Trinity Force -> Infinity Force
        transformations.put(3084L, 7025L); // Heartsteel -> Leviathan
        transformations.put(6653L, 7012L); // Liandry's Anguish -> Liandry's Lament
        transformations.put(6667L, 7027L); // Radiant Virtue -> Primordial Dawn
        transformations.put(3190L, 7019L); // Locket of the Iron Solari -> Reliquary of the Golden Dawn
        transformations.put(3124L, 7030L); // Guinsoo's Rageblade -> Seething Sorrow
        transformations.put(2065L, 7020L); // Shurelya's Battlesong -> Shurelya's Requiem
        transformations.put(6617L, 7021L); // Moonstone Renewer -> Starcaster
        transformations.put(6692L, 7001L); // Eclipse -> Syzygy
        transformations.put(6665L, 7026L); // Jak'Sho, The Protean -> The Unspoken Parasite
        transformations.put(6671L, 7006L); // Galeforce -> Typhoon
        transformations.put(3152L, 7011L); // Hextech Rocketbelt -> Upgraded Aeropack
        transformations.put(4636L, 7010L); // Night Harvester -> Vespertide
        transformations.put(3142L, 7029L); // Youmuu's Ghostblade -> Youmuu's Wake
        transformations.put(4638L, 4643L); // Watchful Wardstone -> Vigilant Wardstone

        return transformations;
    }

    private void generateItemTransformationEvent(List<AllPlayersEvent> events, Player incomingPlayer, AllGameData incomingAllGameData, Double eventTime, Item oldItem, Item newItem, AllPlayersEvent event)
    {
        event.setAllPlayersEventTime(eventTime);
        event.setAllGameData(incomingAllGameData);
        event.setPlayer(incomingPlayer);
        event.setChampionName(incomingPlayer.getChampionName());
        event.setOldItem(oldItem);
        event.setNewItem(newItem);
        event.setSummonerName(incomingPlayer.getSummonerName());
        events.add(event);
    }

    private void detectHeraldUsage(List<AllPlayersEvent> events, Player incomingPlayer, AllGameData incomingAllGameData, Double eventTime, Item currentItem, Item incomingItem) {
        // If the player was holding the Herald and is no longer holding it, the EYE_OF_HERALD_USED_OR_LOST event has occurred
        if (currentItem.getItemID() == 3513L && incomingItem.getItemID() != 3513L) {
            AllPlayersEvent event = new AllPlayersEvent();
            event.setAllPlayersEventType(AllPlayersEventType.EYE_OF_HERALD_USED_OR_LOST);
            event.setAllPlayersEventTime(eventTime);
            event.setAllGameData(incomingAllGameData);
            event.setPlayer(incomingPlayer);
            event.setChampionName(incomingPlayer.getChampionName());
            event.setSummonerName(incomingPlayer.getSummonerName());
            events.add(event);
        }
    }
}
