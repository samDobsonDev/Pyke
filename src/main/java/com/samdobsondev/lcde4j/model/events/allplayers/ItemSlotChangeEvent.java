package com.samdobsondev.lcde4j.model.events.allplayers;

import com.samdobsondev.lcde4j.model.data.AllGameData;
import com.samdobsondev.lcde4j.model.data.allplayers.Item;
import com.samdobsondev.lcde4j.model.data.allplayers.Player;

public class ItemSlotChangeEvent implements AllPlayersEvent {
    private final AllPlayersEventType allPlayersEventType;
    private final Double allPlayersEventTime;
    private final AllGameData allGameData;
    private final Player player;
    private final String championName;
    private final Item item;
    private final Long oldSlot;
    private final Long newSlot;
    private final String summonerName;

    public ItemSlotChangeEvent(AllPlayersEventType allPlayersEventType,
                               Double allPlayersEventTime,
                               AllGameData allGameData,
                               Player player,
                               String championName,
                               Item item,
                               Long oldSlot,
                               Long newSlot,
                               String summonerName) {
        this.allPlayersEventType = allPlayersEventType;
        this.allPlayersEventTime = allPlayersEventTime;
        this.allGameData = allGameData;
        this.player = player;
        this.championName = championName;
        this.item = item;
        this.oldSlot = oldSlot;
        this.newSlot = newSlot;
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

    public Item getItem() {
        return this.item;
    }

    public Long getOldSlot() {
        return this.oldSlot;
    }

    public Long getNewSlot() {
        return this.newSlot;
    }

    public String getSummonerName() {
        return this.summonerName;
    }
}
