package com.samdobsondev.pyke.model.events.allplayers;

import com.samdobsondev.pyke.model.data.AllGameData;
import com.samdobsondev.pyke.model.data.allplayers.Player;

public class RespawnTimerChangeEvent implements AllPlayersEvent {
    private final AllPlayersEventType allPlayersEventType;
    private final Double allPlayersEventTime;
    private final AllGameData allGameData;
    private final Player player;
    private final String championName;
    private final Double respawnTimer;
    private final String summonerName;

    public RespawnTimerChangeEvent(AllPlayersEventType allPlayersEventType,
                                   Double allPlayersEventTime,
                                   AllGameData allGameData,
                                   Player player,
                                   String championName,
                                   Double respawnTimer,
                                   String summonerName) {
        this.allPlayersEventType = allPlayersEventType;
        this.allPlayersEventTime = allPlayersEventTime;
        this.allGameData = allGameData;
        this.player = player;
        this.championName = championName;
        this.respawnTimer = respawnTimer;
        this.summonerName = summonerName;
    }

    @Override
    public AllPlayersEventType getAllPlayersEventType() {
        return this.allPlayersEventType;
    }

    @Override
    public Double getAllPlayersEventTime() {
        return this.allPlayersEventTime;
    }

    @Override
    public AllGameData getAllGameData() {
        return this.allGameData;
    }

    @Override
    public Player getPlayer() {
        return this.player;
    }

    public String getChampionName() {
        return this.championName;
    }

    public Double getRespawnTimer() {
        return this.respawnTimer;
    }

    public String getSummonerName() {
        return this.summonerName;
    }
}
