package com.samdobsondev.lcde4j.api;

import com.samdobsondev.lcde4j.model.data.AllGameData;
import com.samdobsondev.lcde4j.model.data.allplayers.Item;
import com.samdobsondev.lcde4j.model.data.allplayers.Player;
import com.samdobsondev.lcde4j.model.data.allplayers.Scores;
import com.samdobsondev.lcde4j.model.data.allplayers.SummonerSpells;
import com.samdobsondev.lcde4j.model.events.allplayers.AllPlayersEvent;
import com.samdobsondev.lcde4j.model.events.allplayers.AllPlayersEventType;

import java.util.*;
import java.util.stream.Collectors;

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

            // Check for level changes
            checkForLevelChanges(events, currentPlayer, incomingPlayer, incomingAllGameData, eventTime);

            // Check for respawn timer changes
            checkForRespawnTimerChanges(events, currentPlayer, incomingPlayer, incomingAllGameData, eventTime);

            // Check for score changes
            checkForScoreChanges(events, currentPlayer, incomingPlayer, incomingAllGameData, eventTime);

            // Check for summoner spell changes
            checkForSummonerSpellChanges(events, currentPlayer, incomingPlayer, incomingAllGameData, eventTime);
        }

        return events;
    }

    private void checkForPlayersJoined(List<AllPlayersEvent> events, List<Player> current, List<Player> incoming, AllGameData incomingAllGameData, Double eventTime) {
        if (incoming.size() > current.size()) {
            List<Player> newPlayers = incoming.subList(current.size(), incoming.size());
            for (Player player : newPlayers) {
                AllPlayersEvent event = new AllPlayersEvent();
                event.setAllPlayersEventType(AllPlayersEventType.PLAYER_JOINED);
                event.setAllPlayersEventTime(eventTime);
                event.setAllGameData(incomingAllGameData);
                event.setPlayer(player);

                // Utilizing Optional to avoid NullPointerExceptions (such as when adding Target Dummies to the game)
                Optional.ofNullable(player.getChampionName()).ifPresent(event::setChampionName);
                Optional.ofNullable(player.getIsBot()).ifPresent(event::setIsBot);
                Optional.ofNullable(player.getPosition()).ifPresent(event::setPosition);
                Optional.ofNullable(player.getRawChampionName()).ifPresent(event::setRawChampionName);

                Optional.ofNullable(player.getRunes()).ifPresent(runes -> {
                    Optional.ofNullable(runes.getKeystone()).ifPresent(event::setKeystone);
                    Optional.ofNullable(runes.getPrimaryRuneTree()).ifPresent(event::setPrimaryRuneTree);
                    Optional.ofNullable(runes.getSecondaryRuneTree()).ifPresent(event::setSecondaryRuneTree);
                });

                Optional.ofNullable(player.getSkinID()).ifPresent(event::setSkinID);
                Optional.ofNullable(player.getSummonerName()).ifPresent(event::setSummonerName);

                Optional.ofNullable(player.getSummonerSpells()).ifPresent(spells -> {
                    Optional.ofNullable(spells.getSummonerSpellOne()).ifPresent(event::setSummonerSpellOne);
                    Optional.ofNullable(spells.getSummonerSpellTwo()).ifPresent(event::setSummonerSpellTwo);
                });

                Optional.ofNullable(player.getTeam()).ifPresent(event::setTeam);

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

        detectItemAcquiredAndItemSoldEvents(events, incomingPlayer, incomingAllGameData, eventTime, currentItems, incomingItems);

        if (incomingItems.size() == currentItems.size()) {
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

    private void detectItemAcquiredAndItemSoldEvents(List<AllPlayersEvent> events, Player incomingPlayer, AllGameData incomingAllGameData, Double eventTime, List<Item> currentItems, List<Item> incomingItems) {
        // Get the counts of each item in the currentItems and incomingItems lists
        Map<Long, Long> currentItemsCount = getItemsCountMap(currentItems);
        Map<Long, Long> incomingItemsCount = getItemsCountMap(incomingItems);

        // Generate ITEM_ACQUIRED events
        for (Map.Entry<Long, Long> entry : incomingItemsCount.entrySet()) {
            if (!currentItemsCount.containsKey(entry.getKey()) || entry.getValue() > currentItemsCount.get(entry.getKey())) {
                long numberOfItemsAcquired = currentItemsCount.containsKey(entry.getKey()) ? entry.getValue() - currentItemsCount.get(entry.getKey()) : entry.getValue();
                for (int i = 0; i < numberOfItemsAcquired; i++) {
                    Item acquiredItem = incomingItems.stream().filter(item -> item.getItemID().equals(entry.getKey())).findFirst().orElse(null);
                    if (acquiredItem != null) {
                        AllPlayersEvent event = new AllPlayersEvent();
                        event.setAllPlayersEventType(AllPlayersEventType.ITEM_ACQUIRED);
                        generateItemAcquiredEvent(events, incomingPlayer, incomingAllGameData, eventTime, acquiredItem, event);
                    }
                }
            }
        }

        // Generate ITEM_SOLD_OR_CONSUMED events
        for (Map.Entry<Long, Long> entry : currentItemsCount.entrySet()) {
            if (!incomingItemsCount.containsKey(entry.getKey()) || entry.getValue() > incomingItemsCount.get(entry.getKey())) {
                long numberOfItemsSold = incomingItemsCount.containsKey(entry.getKey()) ? entry.getValue() - incomingItemsCount.get(entry.getKey()) : entry.getValue();
                for (int i = 0; i < numberOfItemsSold; i++) {
                    Item soldItem = currentItems.stream().filter(item -> item.getItemID().equals(entry.getKey())).findFirst().orElse(null);
                    if (soldItem != null) {
                        AllPlayersEvent event = new AllPlayersEvent();
                        event.setAllPlayersEventType(AllPlayersEventType.ITEM_SOLD_OR_CONSUMED);
                        generateItemSoldEvent(events, incomingPlayer, incomingAllGameData, eventTime, soldItem, event);
                    }
                }
            }
        }
    }

    private Map<Long, Long> getItemsCountMap(List<Item> items) {
        // We use the stream API to group items by their ID and count occurrences
        return items.stream().collect(Collectors.groupingBy(Item::getItemID, Collectors.counting()));
    }

    private void generateItemAcquiredEvent(List<AllPlayersEvent> events, Player incomingPlayer, AllGameData incomingAllGameData, Double eventTime, Item item, AllPlayersEvent event)
    {
        event.setAllPlayersEventTime(eventTime);
        event.setAllGameData(incomingAllGameData);
        event.setPlayer(incomingPlayer);
        event.setChampionName(incomingPlayer.getChampionName());
        event.setAcquiredItem(item);
        event.setSummonerName(incomingPlayer.getSummonerName());
        events.add(event);
    }

    private void generateItemSoldEvent(List<AllPlayersEvent> events, Player incomingPlayer, AllGameData incomingAllGameData, Double eventTime, Item item, AllPlayersEvent event)
    {
        event.setAllPlayersEventTime(eventTime);
        event.setAllGameData(incomingAllGameData);
        event.setPlayer(incomingPlayer);
        event.setChampionName(incomingPlayer.getChampionName());
        event.setSoldOrConsumedItem(item);
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
        transformations.put(4638L, 4643L); // Watchful Wardstone -> Vigilant Wardstone
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
        // If the player was holding the Eye of Herald and is no longer holding it, an EYE_OF_HERALD_USED_OR_LOST event has occurred
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

    private void checkForLevelChanges(List<AllPlayersEvent> events, Player currentPlayer, Player incomingPlayer, AllGameData incomingAllGameData, Double eventTime) {
        if (!currentPlayer.getLevel().equals(incomingPlayer.getLevel())) {
            AllPlayersEvent event = new AllPlayersEvent();
            event.setAllPlayersEventType(AllPlayersEventType.LEVEL_UP);
            event.setAllPlayersEventTime(eventTime);
            event.setAllGameData(incomingAllGameData);
            event.setPlayer(incomingPlayer);
            event.setChampionName(incomingPlayer.getChampionName());
            event.setLevel(incomingPlayer.getLevel());
            event.setSummonerName(incomingPlayer.getSummonerName());
            events.add(event);
        }
    }

    private void checkForRespawnTimerChanges(List<AllPlayersEvent> events, Player currentPlayer, Player incomingPlayer, AllGameData incomingAllGameData, Double eventTime) {
        if (incomingPlayer.getRespawnTimer() < currentPlayer.getRespawnTimer()) {
            AllPlayersEvent event = new AllPlayersEvent();
            event.setAllPlayersEventType(AllPlayersEventType.RESPAWN_TIMER_CHANGE);
            event.setAllPlayersEventTime(eventTime);
            event.setAllGameData(incomingAllGameData);
            event.setPlayer(incomingPlayer);
            event.setChampionName(incomingPlayer.getChampionName());
            event.setRespawnTimer(incomingPlayer.getRespawnTimer());
            event.setSummonerName(incomingPlayer.getSummonerName());
            events.add(event);
        }
    }

    private void checkForScoreChanges(List<AllPlayersEvent> events, Player currentPlayer, Player incomingPlayer, AllGameData incomingAllGameData, Double eventTime) {
        Scores currentScores = currentPlayer.getScores();
        Scores incomingScores = incomingPlayer.getScores();

        if (!currentScores.getAssists().equals(incomingScores.getAssists())) {
            AllPlayersEvent event = new AllPlayersEvent();
            event.setAllPlayersEventType(AllPlayersEventType.ASSISTS_CHANGE);
            event.setAllPlayersEventTime(eventTime);
            event.setAllGameData(incomingAllGameData);
            event.setPlayer(incomingPlayer);
            event.setChampionName(incomingPlayer.getChampionName());
            event.setAssists(incomingScores.getAssists());
            event.setSummonerName(incomingPlayer.getSummonerName());
            events.add(event);
        }

        // The CS score is updated every 10 CS, rather than every CS
        if (!currentScores.getCreepScore().equals(incomingScores.getCreepScore())) {
            AllPlayersEvent event = new AllPlayersEvent();
            event.setAllPlayersEventType(AllPlayersEventType.CS_CHANGE);
            event.setAllPlayersEventTime(eventTime);
            event.setAllGameData(incomingAllGameData);
            event.setPlayer(incomingPlayer);
            event.setChampionName(incomingPlayer.getChampionName());
            event.setCreepScore(incomingScores.getCreepScore());
            event.setSummonerName(incomingPlayer.getSummonerName());
            events.add(event);
        }

        if (!currentScores.getDeaths().equals(incomingScores.getDeaths())) {
            AllPlayersEvent event = new AllPlayersEvent();
            event.setAllPlayersEventType(AllPlayersEventType.DEATHS_CHANGE);
            event.setAllPlayersEventTime(eventTime);
            event.setAllGameData(incomingAllGameData);
            event.setPlayer(incomingPlayer);
            event.setChampionName(incomingPlayer.getChampionName());
            event.setDeaths(incomingScores.getDeaths());
            event.setSummonerName(incomingPlayer.getSummonerName());
            events.add(event);
        }

        if (!currentScores.getKills().equals(incomingScores.getKills())) {
            AllPlayersEvent event = new AllPlayersEvent();
            event.setAllPlayersEventType(AllPlayersEventType.KILLS_CHANGE);
            event.setAllPlayersEventTime(eventTime);
            event.setAllGameData(incomingAllGameData);
            event.setPlayer(incomingPlayer);
            event.setChampionName(incomingPlayer.getChampionName());
            event.setKills(incomingScores.getKills());
            event.setSummonerName(incomingPlayer.getSummonerName());
            events.add(event);
        }

        if (!currentScores.getWardScore().equals(incomingScores.getWardScore())) {
            AllPlayersEvent event = new AllPlayersEvent();
            event.setAllPlayersEventType(AllPlayersEventType.VISION_SCORE_CHANGE);
            event.setAllPlayersEventTime(eventTime);
            event.setAllGameData(incomingAllGameData);
            event.setPlayer(incomingPlayer);
            event.setChampionName(incomingPlayer.getChampionName());
            event.setWardScore(incomingScores.getWardScore());
            event.setSummonerName(incomingPlayer.getSummonerName());
            events.add(event);
        }
    }

    private void checkForSummonerSpellChanges(List<AllPlayersEvent> events, Player currentPlayer, Player incomingPlayer, AllGameData incomingAllGameData, Double eventTime) {
        SummonerSpells currentSummonerSpells = currentPlayer.getSummonerSpells();
        SummonerSpells incomingSummonerSpells = incomingPlayer.getSummonerSpells();

        // Utilizing Optionals to avoid NullPointerExceptions (such as when adding Target Dummies to the game)
        Optional.ofNullable(currentSummonerSpells).ifPresent(current -> Optional.ofNullable(incomingSummonerSpells).ifPresent(incoming -> {
            Optional.ofNullable(current.getSummonerSpellOne()).ifPresent(currentSpellOne -> {
                Optional.ofNullable(incoming.getSummonerSpellOne()).ifPresent(incomingSpellOne -> {
                    if (!currentSpellOne.equals(incomingSpellOne)) {
                        AllPlayersEvent event = new AllPlayersEvent();
                        event.setAllPlayersEventType(AllPlayersEventType.SUMMONER_SPELL_ONE_CHANGE);
                        event.setAllPlayersEventTime(eventTime);
                        event.setAllGameData(incomingAllGameData);
                        event.setPlayer(incomingPlayer);
                        event.setChampionName(incomingPlayer.getChampionName());
                        event.setSummonerName(incomingPlayer.getSummonerName());
                        events.add(event);
                    }
                });
            });

            Optional.ofNullable(current.getSummonerSpellTwo()).ifPresent(currentSpellTwo -> Optional.ofNullable(incoming.getSummonerSpellTwo()).ifPresent(incomingSpellTwo -> {
                if (!currentSpellTwo.equals(incomingSpellTwo)) {
                    AllPlayersEvent event = new AllPlayersEvent();
                    event.setAllPlayersEventType(AllPlayersEventType.SUMMONER_SPELL_TWO_CHANGE);
                    event.setAllPlayersEventTime(eventTime);
                    event.setAllGameData(incomingAllGameData);
                    event.setPlayer(incomingPlayer);
                    event.setChampionName(incomingPlayer.getChampionName());
                    event.setSummonerName(incomingPlayer.getSummonerName());
                    events.add(event);
                }
            }));
        }));
    }
}
