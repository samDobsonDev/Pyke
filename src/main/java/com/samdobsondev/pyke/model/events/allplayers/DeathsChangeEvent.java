package com.samdobsondev.pyke.model.events.allplayers;

import com.samdobsondev.pyke.model.data.AllGameData;
import com.samdobsondev.pyke.model.data.allplayers.Player;

public class DeathsChangeEvent implements AllPlayersEvent {
    private final AllPlayersEventType allPlayersEventType;
    private final Double allPlayersEventTime;
    private final AllGameData allGameData;
    private final Player player;
    private final String championName;
    private final Long deaths;
    private final String summonerName;

    public DeathsChangeEvent(AllPlayersEventType allPlayersEventType,
                             Double allPlayersEventTime,
                             AllGameData allGameData,
                             Player player,
                             String championName,
                             Long deaths,
                             String summonerName) {
        this.allPlayersEventType = allPlayersEventType;
        this.allPlayersEventTime = allPlayersEventTime;
        this.allGameData = allGameData;
        this.player = player;
        this.championName = championName;
        this.deaths = deaths;
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

    public Long getDeaths() {
        return this.deaths;
    }

    public String getSummonerName() {
        return this.summonerName;
    }
}
