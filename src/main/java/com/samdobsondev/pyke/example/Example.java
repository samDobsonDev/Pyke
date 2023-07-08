package com.samdobsondev.pyke.example;

import com.samdobsondev.pyke.api.Pyke;
import com.samdobsondev.pyke.api.listener.AllPlayersEventListener;
import com.samdobsondev.pyke.api.listener.AnnouncerNotificationEventListener;
import com.samdobsondev.pyke.api.listener.GameDataEventListener;
import com.samdobsondev.pyke.model.data.allplayers.Player;
import com.samdobsondev.pyke.model.events.allplayers.CreepScoreChangeEvent;
import com.samdobsondev.pyke.model.events.allplayers.ItemAcquiredEvent;
import com.samdobsondev.pyke.model.events.allplayers.ItemSoldOrConsumedEvent;
import com.samdobsondev.pyke.model.events.announcer.ChampionKillEvent;
import com.samdobsondev.pyke.model.events.gamedata.MapTerrainChangeEvent;
import no.stelar7.api.r4j.basic.APICredentials;
import no.stelar7.api.r4j.impl.R4J;
import no.stelar7.api.r4j.impl.lol.raw.DDragonAPI;

import java.util.List;
import java.util.Optional;

public class Example {

    static APICredentials apiCredentials = new APICredentials("some-api-key");
    static R4J r4j = new R4J(apiCredentials);
    static DDragonAPI dDragonAPI = r4j.getDDragonAPI();

    public static void main(String[] args) {

        Pyke pyke = new Pyke();

        pyke.registerAllPlayersEventListener(new AllPlayersEventListener() {

            // This method will trigger when any player's CS in the game increase by a factor of 10
            @Override
            public void onCreepScoreChange(CreepScoreChangeEvent event) {

                // Get the current game time in minutes
                Double currentGameTime = event.getAllPlayersEventTime() / 60;

                // Calculate the CS per minute
                double csMin = event.getCreepScore() / currentGameTime;

                System.out.println(event.getChampionName() + " is achieving " + csMin + " CS per minute!");
            }

            // This method will get called when anybody in the game buys an item
            @Override
            public void onItemAcquired(ItemAcquiredEvent event) {

                // We create two lists of Player objects, one for each team
                List<Player> orderPlayers = event.getAllGameData().getAllPlayers().stream()
                        .filter(player -> "ORDER".equals(player.getTeam()))
                        .toList();

                List<Player> chaosPlayers = event.getAllGameData().getAllPlayers().stream()
                        .filter(player -> "CHAOS".equals(player.getTeam()))
                        .toList();

                // We create two variable to track the total cost of every item on each team
                int totalOrderCost = 0;
                int totalChaosCost = 0;

                // For each player on the ORDER team, calculate the total cost of their items and add it to the totalOrderCost
                for (Player player : orderPlayers) {
                    totalOrderCost += player.getItems().stream()
                            .mapToInt(item -> Math.toIntExact(getItemGold(item.getItemID()) * item.getCount()))
                            .sum();
                }

                // For each player on the CHAOS team, calculate the total cost of their items and add it to the totalChaosCost
                for (Player player : chaosPlayers) {
                    totalChaosCost += player.getItems().stream()
                            .mapToInt(item -> Math.toIntExact(getItemGold(item.getItemID()) * item.getCount()))
                            .sum();
                }

                System.out.println(totalOrderCost);
                System.out.println(totalChaosCost);
            }
        });

        pyke.registerAnnouncerNotificationEventListener(new AnnouncerNotificationEventListener() {

            // This method will trigger when any kill happens in the game
            @Override
            public void onChampionKill(ChampionKillEvent event) {

                // Grab the active player summoner name
                String activePlayerSummonerName = event.getAllGameData().getActivePlayer().getSummonerName();

                // Use the activePlayerSummonerName to retrieve the position of the active player (MID, TOP, etc..)
                String activePlayerPosition = event.getAllGameData().getAllPlayers().stream()
                        .filter(player -> player.getSummonerName().equals(activePlayerSummonerName))
                        .map(Player::getPosition)
                        .findFirst()
                        .orElse(null);

                // Find the player on the opposing team of the same position as the active player (lane opponent)
                Optional<Player> enemyMidlanerOptional = event.getAllGameData().getAllPlayers().stream()
                        .filter(player -> player.getPosition().equals(activePlayerPosition) && player.getTeam().equals("CHAOS"))
                        .findAny();

                while (enemyMidlanerOptional.isPresent()) {
                    Player enemyMidlaner = enemyMidlanerOptional.get();

                    // If the ChampionKillEvent killer is the active player, and the victim is the lane opponent...
                    if ((event.getKillerName().equals(event.getAllGameData().getActivePlayer().getSummonerName())) && event.getVictimName().equals(enemyMidlaner.getSummonerName())) {
                        System.out.println(activePlayerSummonerName + " killed their lane opponent!");
                    }
                }
            }
        });

        pyke.registerGameDataEventListener(new GameDataEventListener() {

            // This method will trigger when the map terrain changes (Mountain, Chemtech, Infernal, etc...)
            @Override
            public void onMapTerrainChange(MapTerrainChangeEvent event) {
                System.out.println("The map terrain has changed from " + event.getOldMapTerrain() + " to " + event.getNewMapTerrain());
            }
        });

        pyke.start(); // starts the port watching and subsequent polling of the /liveclientdata endpoint
    }

    // Accepts an itemId and returns the total gold value of that item
    private static int getItemGold(Long itemId) {
        Optional<no.stelar7.api.r4j.pojo.lol.staticdata.item.Item> optionalItem = dDragonAPI.getItems().values().stream()
                .filter(item -> itemId.equals((long) item.getId()))
                .findFirst();
        return optionalItem.map(item -> item.getGold().getTotal()).orElse(0);
    }

}

