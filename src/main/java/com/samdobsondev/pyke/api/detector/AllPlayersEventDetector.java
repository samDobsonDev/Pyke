package com.samdobsondev.pyke.api.detector;

import com.samdobsondev.pyke.model.data.AllGameData;
import com.samdobsondev.pyke.model.data.allplayers.*;
import com.samdobsondev.pyke.model.events.allplayers.*;

import java.util.*;
import java.util.stream.Collectors;

public class AllPlayersEventDetector
{
    public List<AllPlayersEvent> detectEvents(AllGameData currentAllGameData, AllGameData incomingAllGameData, Double eventTime) {
        List<Player> current = currentAllGameData.getAllPlayers();
        List<Player> incoming = incomingAllGameData.getAllPlayers();

        List<AllPlayersEvent> events = new ArrayList<>();

        // This will contain all the expected changes if the Pyke library is stopped and started again mid-game
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

                // Use optional to handle null cases (such as when Target Dummies are in the game)
                Runes runes = Optional.ofNullable(player.getRunes()).orElse(new Runes());
                SummonerSpells summonerSpells = Optional.ofNullable(player.getSummonerSpells()).orElse(new SummonerSpells());

                PlayerJoinedEvent event = new PlayerJoinedEvent(AllPlayersEventType.PLAYER_JOINED,
                        eventTime,
                        incomingAllGameData,
                        player,
                        player.getChampionName(),
                        player.getIsBot(),
                        player.getPosition(),
                        player.getRawChampionName(),
                        runes.getKeystone(),
                        runes.getPrimaryRuneTree(),
                        runes.getSecondaryRuneTree(),
                        player.getSkinID(),
                        player.getSummonerName(),
                        summonerSpells.getSummonerSpellOne(),
                        summonerSpells.getSummonerSpellTwo(),
                        player.getTeam()
                );

                events.add(event);
            }
        }
    }


    private void checkDeadAliveChanges(List<AllPlayersEvent> events, Player currentPlayer, Player incomingPlayer, AllGameData incomingAllGameData, Double eventTime) {
        boolean currentPlayerIsDead = Boolean.TRUE.equals(currentPlayer.getIsDead());
        boolean incomingPlayerIsDead = Boolean.TRUE.equals(incomingPlayer.getIsDead());

        if (!currentPlayerIsDead && incomingPlayerIsDead || currentPlayerIsDead && !incomingPlayerIsDead) {
            if (!currentPlayerIsDead) {
                // Player has died
                DeathEvent event = new DeathEvent(AllPlayersEventType.DEATH,
                        eventTime,
                        incomingAllGameData,
                        incomingPlayer,
                        incomingPlayer.getChampionName(),
                        incomingPlayer.getIsDead(),
                        incomingPlayer.getSummonerName(),
                        incomingPlayer.getScores().getDeaths());
                events.add(event);
            } else {
                // Player has respawned
                RespawnEvent event = new RespawnEvent(AllPlayersEventType.RESPAWN,
                        eventTime,
                        incomingAllGameData,
                        incomingPlayer,
                        incomingPlayer.getChampionName(),
                        incomingPlayer.getIsDead(),
                        incomingPlayer.getSummonerName(),
                        incomingPlayer.getScores().getDeaths());
                events.add(event);
            }
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
        // Iterate over each entry (key/value pair) in the incomingItemsCount map
        for (Map.Entry<Long, Long> entry : incomingItemsCount.entrySet()) {

            // Check if the currentItemsCount map does not contain this item OR the count of this item in the incomingItemsCount map is greater than its count in the currentItemsCount map
            if (!currentItemsCount.containsKey(entry.getKey()) || entry.getValue() > currentItemsCount.get(entry.getKey())) {

                // Calculate the number of items acquired. If the item is already in the currentItemsCount map, subtract the old count from the new count. Otherwise, the number of items acquired is just the count in the incomingItemsCount map.
                long numberOfItemsAcquired = currentItemsCount.containsKey(entry.getKey()) ? entry.getValue() - currentItemsCount.get(entry.getKey()) : entry.getValue();

                // Generate an ITEM_ACQUIRED event for each item acquired
                for (int i = 0; i < numberOfItemsAcquired; i++) {

                    // Find the item in the incomingItems list with the same ID as the current entry in the map
                    Item acquiredItem = incomingItems.stream().filter(item -> item.getItemID().equals(entry.getKey())).findFirst().orElse(null);

                    // If the item is not null, generate the event
                    if (acquiredItem != null) {
                        ItemAcquiredEvent event = new ItemAcquiredEvent(AllPlayersEventType.ITEM_ACQUIRED,
                                eventTime,
                                incomingAllGameData,
                                incomingPlayer,
                                incomingPlayer.getChampionName(),
                                acquiredItem,
                                incomingPlayer.getSummonerName()
                                );
                        events.add(event);
                    }
                }
            }
        }

        // Generate ITEM_SOLD_OR_CONSUMED events
        // Iterate over each entry in the currentItemsCount map
        for (Map.Entry<Long, Long> entry : currentItemsCount.entrySet()) {

            // Check if the incomingItemsCount map does not contain this item OR the count of this item in the currentItemsCount map is greater than its count in the incomingItemsCount map
            if (!incomingItemsCount.containsKey(entry.getKey()) || entry.getValue() > incomingItemsCount.get(entry.getKey())) {

                // Calculate the number of items sold. If the item is already in the incomingItemsCount map, subtract the new count from the old count. Otherwise, the number of items sold is just the count in the currentItemsCount map.
                long numberOfItemsSold = incomingItemsCount.containsKey(entry.getKey()) ? entry.getValue() - incomingItemsCount.get(entry.getKey()) : entry.getValue();

                // Generate an ITEM_SOLD_OR_CONSUMED event for each item sold
                for (int i = 0; i < numberOfItemsSold; i++) {

                    // Find the item in the currentItems list with the same ID as the current entry in the map
                    Item soldItem = currentItems.stream().filter(item -> item.getItemID().equals(entry.getKey())).findFirst().orElse(null);

                    // If the item is not null, generate the event
                    if (soldItem != null) {
                        ItemSoldOrConsumedEvent event = new ItemSoldOrConsumedEvent(AllPlayersEventType.ITEM_SOLD_OR_CONSUMED,
                                eventTime,
                                incomingAllGameData,
                                incomingPlayer,
                                incomingPlayer.getChampionName(),
                                soldItem,
                                incomingPlayer.getSummonerName()
                        );
                        events.add(event);
                    }
                }
            }
        }
    }

    private Map<Long, Long> getItemsCountMap(List<Item> items) {
        // We use the stream API to group items by their ID and sum their counts
        return items.stream().collect(Collectors.groupingBy(Item::getItemID, Collectors.summingLong(Item::getCount)));
    }

    private void detectSlotChanges(List<AllPlayersEvent> events, Player incomingPlayer, AllGameData incomingAllGameData, Double eventTime, List<Item> currentItems, List<Item> incomingItems) {

        if (containsSameItems(currentItems, incomingItems)) {
            for (int i = 0; i < currentItems.size(); i++) {
                Item currentItem = currentItems.get(i);
                Item incomingItem = incomingItems.get(i);

                // If the item is the same but the slot has changed
                if (currentItem.getItemID().equals(incomingItem.getItemID()) && !currentItem.getSlot().equals(incomingItem.getSlot())) {
                    ItemSlotChangeEvent event = new ItemSlotChangeEvent(AllPlayersEventType.ITEM_SLOT_CHANGE,
                            eventTime,
                            incomingAllGameData,
                            incomingPlayer,
                            incomingPlayer.getChampionName(),
                            incomingItem,
                            currentItem.getSlot(),
                            incomingItem.getSlot(),
                            incomingPlayer.getSummonerName());
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
                ItemTransformationEvent event = new ItemTransformationEvent(AllPlayersEventType.ITEM_TRANSFORMATION,
                        eventTime,
                        incomingAllGameData,
                        incomingPlayer,
                        incomingPlayer.getChampionName(),
                        currentItem,
                        incomingItem,
                        incomingPlayer.getSummonerName());
                events.add(event);
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

    private void detectHeraldUsage(List<AllPlayersEvent> events, Player incomingPlayer, AllGameData incomingAllGameData, Double eventTime, Item currentItem, Item incomingItem) {
        // If the player was holding the Eye of Herald and is no longer holding it, an EYE_OF_HERALD_USED_OR_LOST event has occurred
        if (currentItem.getItemID() == 3513L && incomingItem.getItemID() != 3513L) {
            EyeOfHeraldUsedOrLostEvent event = new EyeOfHeraldUsedOrLostEvent(AllPlayersEventType.EYE_OF_HERALD_USED_OR_LOST,
                    eventTime,
                    incomingAllGameData,
                    incomingPlayer,
                    incomingPlayer.getChampionName(),
                    incomingPlayer.getSummonerName());
            events.add(event);
        }
    }

    private void checkForLevelChanges(List<AllPlayersEvent> events, Player currentPlayer, Player incomingPlayer, AllGameData incomingAllGameData, Double eventTime) {
        if (!currentPlayer.getLevel().equals(incomingPlayer.getLevel())) {
            LevelUpEvent event = new LevelUpEvent(AllPlayersEventType.LEVEL_UP,
                    eventTime,
                    incomingAllGameData,
                    incomingPlayer,
                    incomingPlayer.getChampionName(),
                    incomingPlayer.getLevel(),
                    incomingPlayer.getSummonerName());
            events.add(event);
        }
    }

    private void checkForRespawnTimerChanges(List<AllPlayersEvent> events, Player currentPlayer, Player incomingPlayer, AllGameData incomingAllGameData, Double eventTime) {
        if (!incomingPlayer.getRespawnTimer().equals(currentPlayer.getRespawnTimer())) {
            RespawnTimerChangeEvent event = new RespawnTimerChangeEvent(AllPlayersEventType.RESPAWN_TIMER_CHANGE,
                    eventTime,
                    incomingAllGameData,
                    incomingPlayer,
                    incomingPlayer.getChampionName(),
                    incomingPlayer.getRespawnTimer(),
                    incomingPlayer.getSummonerName());
            events.add(event);
        }
    }

    private void checkForScoreChanges(List<AllPlayersEvent> events, Player currentPlayer, Player incomingPlayer, AllGameData incomingAllGameData, Double eventTime) {
        Scores currentScores = currentPlayer.getScores();
        Scores incomingScores = incomingPlayer.getScores();

        if (!currentScores.getAssists().equals(incomingScores.getAssists())) {
            AssistsChangeEvent event = new AssistsChangeEvent(AllPlayersEventType.ASSISTS_CHANGE,
                    eventTime,
                    incomingAllGameData,
                    incomingPlayer,
                    incomingPlayer.getChampionName(),
                    incomingScores.getAssists(),
                    incomingPlayer.getSummonerName());
            events.add(event);
        }

        // The CS score is updated every 10 CS, rather than every CS
        if (!currentScores.getCreepScore().equals(incomingScores.getCreepScore())) {
            CreepScoreChangeEvent event = new CreepScoreChangeEvent(AllPlayersEventType.CS_CHANGE,
                    eventTime,
                    incomingAllGameData,
                    incomingPlayer,
                    incomingPlayer.getChampionName(),
                    incomingScores.getCreepScore(),
                    incomingPlayer.getSummonerName());
            events.add(event);
        }

        if (!currentScores.getDeaths().equals(incomingScores.getDeaths())) {
            DeathsChangeEvent event = new DeathsChangeEvent(AllPlayersEventType.DEATHS_CHANGE,
                    eventTime,
                    incomingAllGameData,
                    incomingPlayer,
                    incomingPlayer.getChampionName(),
                    incomingScores.getDeaths(),
                    incomingPlayer.getSummonerName());
            events.add(event);
        }

        if (!currentScores.getKills().equals(incomingScores.getKills())) {

            KillsChangeEvent event = new KillsChangeEvent(AllPlayersEventType.KILLS_CHANGE,
                    eventTime,
                    incomingAllGameData,
                    incomingPlayer,
                    incomingPlayer.getChampionName(),
                    incomingScores.getKills(),
                    incomingPlayer.getSummonerName());
            events.add(event);
        }

        if (!currentScores.getWardScore().equals(incomingScores.getWardScore())) {

            VisionScoreChangeEvent event = new VisionScoreChangeEvent(AllPlayersEventType.VISION_SCORE_CHANGE,
                    eventTime,
                    incomingAllGameData,
                    incomingPlayer,
                    incomingPlayer.getChampionName(),
                    incomingScores.getWardScore(),
                    incomingPlayer.getSummonerName());
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
                        SummonerSpellOneChangeEvent event = new SummonerSpellOneChangeEvent(AllPlayersEventType.SUMMONER_SPELL_ONE_CHANGE,
                                eventTime,
                                incomingAllGameData,
                                incomingPlayer,
                                incomingPlayer.getChampionName(),
                                currentSpellOne,
                                incomingSpellOne,
                                incomingPlayer.getSummonerName());
                        events.add(event);
                    }
                });
            });

            Optional.ofNullable(current.getSummonerSpellTwo()).ifPresent(currentSpellTwo -> Optional.ofNullable(incoming.getSummonerSpellTwo()).ifPresent(incomingSpellTwo -> {
                if (!currentSpellTwo.equals(incomingSpellTwo)) {
                    SummonerSpellTwoChangeEvent event = new SummonerSpellTwoChangeEvent(AllPlayersEventType.SUMMONER_SPELL_TWO_CHANGE,
                            eventTime,
                            incomingAllGameData,
                            incomingPlayer,
                            incomingPlayer.getChampionName(),
                            currentSpellTwo,
                            incomingSpellTwo,
                            incomingPlayer.getSummonerName());
                    events.add(event);
                }
            }));
        }));
    }
}
