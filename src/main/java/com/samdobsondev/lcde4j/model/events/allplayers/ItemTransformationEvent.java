package com.samdobsondev.lcde4j.model.events.allplayers;

import com.samdobsondev.lcde4j.model.data.AllGameData;
import com.samdobsondev.lcde4j.model.data.allplayers.Item;
import com.samdobsondev.lcde4j.model.data.allplayers.Player;

public class ItemTransformationEvent implements AllPlayersEvent {
    private final AllPlayersEventType allPlayersEventType;
    private final Double allPlayersEventTime;
    private final AllGameData allGameData;
    private final Player player;
    private final String championName;
    private final Item oldItem;
    private final Item newItem;
    private final String summonerName;

    public ItemTransformationEvent(AllPlayersEventType allPlayersEventType,
                                   Double allPlayersEventTime,
                                   AllGameData allGameData,
                                   Player player,
                                   String championName,
                                   Item oldItem,
                                   Item newItem,
                                   String summonerName) {
        this.allPlayersEventType = allPlayersEventType;
        this.allPlayersEventTime = allPlayersEventTime;
        this.allGameData = allGameData;
        this.player = player;
        this.championName = championName;
        this.oldItem = oldItem;
        this.newItem = newItem;
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

    public Item getOldItem() {
        return this.oldItem;
    }

    public Item getNewItem() {
        return this.newItem;
    }

    public String getSummonerName() {
        return this.summonerName;
    }
}
