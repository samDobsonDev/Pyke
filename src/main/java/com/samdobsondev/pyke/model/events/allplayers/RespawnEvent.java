package com.samdobsondev.pyke.model.events.allplayers;

import com.samdobsondev.pyke.model.data.AllGameData;
import com.samdobsondev.pyke.model.data.allplayers.Player;

public class RespawnEvent implements AllPlayersEvent {
    private final AllPlayersEventType allPlayersEventType;
    private final Double allPlayersEventTime;
    private final AllGameData allGameData;
    private final Player player;
    private final String championName;
    private final Boolean isDead;
    private final String summonerName;
    private final Long deaths;

    public RespawnEvent(AllPlayersEventType allPlayersEventType,
                      Double allPlayersEventTime,
                      AllGameData allGameData,
                      Player player,
                      String championName,
                      Boolean isDead,
                      String summonerName,
                      Long deaths) {
        this.allPlayersEventType = allPlayersEventType;
        this.allPlayersEventTime = allPlayersEventTime;
        this.allGameData = allGameData;
        this.player = player;
        this.championName = championName;
        this.isDead = isDead;
        this.summonerName = summonerName;
        this.deaths = deaths;
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

    public Boolean getDead() {
        return this.isDead;
    }

    public String getSummonerName() {
        return this.summonerName;
    }

    public Long getDeaths() {
        return this.deaths;
    }
}

