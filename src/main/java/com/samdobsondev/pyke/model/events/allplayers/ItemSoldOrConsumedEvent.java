package com.samdobsondev.pyke.model.events.allplayers;

import com.samdobsondev.pyke.model.data.AllGameData;
import com.samdobsondev.pyke.model.data.allplayers.Item;
import com.samdobsondev.pyke.model.data.allplayers.Player;

public class ItemSoldOrConsumedEvent implements AllPlayersEvent {
    private final AllPlayersEventType allPlayersEventType;
    private final Double allPlayersEventTime;
    private final AllGameData allGameData;
    private final Player player;
    private final String championName;
    private final Item soldOrConsumedItem;
    private final String summonerName;

    public ItemSoldOrConsumedEvent(AllPlayersEventType allPlayersEventType,
                             Double allPlayersEventTime,
                             AllGameData allGameData,
                             Player player,
                             String championName,
                             Item soldOrConsumedItem,
                             String summonerName) {
        this.allPlayersEventType = allPlayersEventType;
        this.allPlayersEventTime = allPlayersEventTime;
        this.allGameData = allGameData;
        this.player = player;
        this.championName = championName;
        this.soldOrConsumedItem = soldOrConsumedItem;
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

    public Item getSoldOrConsumedItem() {
        return this.soldOrConsumedItem;
    }

    public String getSummonerName() {
        return this.summonerName;
    }
}

