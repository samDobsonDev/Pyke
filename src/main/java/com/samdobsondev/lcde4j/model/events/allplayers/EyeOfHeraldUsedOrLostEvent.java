package com.samdobsondev.lcde4j.model.events.allplayers;

import com.samdobsondev.lcde4j.model.data.AllGameData;
import com.samdobsondev.lcde4j.model.data.allplayers.Player;

public class EyeOfHeraldUsedOrLostEvent implements AllPlayersEvent {
    private final AllPlayersEventType allPlayersEventType;
    private final Double allPlayersEventTime;
    private final AllGameData allGameData;
    private final Player player;
    private final String championName;
    private final String summonerName;

    public EyeOfHeraldUsedOrLostEvent(AllPlayersEventType allPlayersEventType,
                                      Double allPlayersEventTime,
                                      AllGameData allGameData,
                                      Player player,
                                      String championName,
                                      String summonerName) {
        this.allPlayersEventType = allPlayersEventType;
        this.allPlayersEventTime = allPlayersEventTime;
        this.allGameData = allGameData;
        this.player = player;
        this.championName = championName;
        this.summonerName = summonerName;
    }

    @Override
    public AllPlayersEventType getAllPlayersEventType() {
        return allPlayersEventType;
    }

    @Override
    public Double getAllPlayersEventTime() {
        return allPlayersEventTime;
    }

    @Override
    public AllGameData getAllGameData() {
        return allGameData;
    }

    @Override
    public Player getPlayer() {
        return this.player;
    }

    public String getChampionName() {
        return this.championName;
    }

    public String getSummonerName() {
        return this.summonerName;
    }
}
