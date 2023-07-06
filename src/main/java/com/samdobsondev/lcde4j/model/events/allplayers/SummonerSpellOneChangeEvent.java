package com.samdobsondev.lcde4j.model.events.allplayers;

import com.samdobsondev.lcde4j.model.data.AllGameData;
import com.samdobsondev.lcde4j.model.data.allplayers.Player;
import com.samdobsondev.lcde4j.model.data.allplayers.SummonerSpell;

public class SummonerSpellOneChangeEvent implements AllPlayersEvent {
    private final AllPlayersEventType allPlayersEventType;
    private final Double allPlayersEventTime;
    private final AllGameData allGameData;
    private final Player player;
    private final String championName;
    private final SummonerSpell oldSummonerSpellOne;
    private final SummonerSpell newSummonerSpellOne;
    private final String summonerName;

    public SummonerSpellOneChangeEvent(AllPlayersEventType allPlayersEventType,
                                       Double allPlayersEventTime,
                                       AllGameData allGameData,
                                       Player player,
                                       String championName,
                                       SummonerSpell oldSummonerSpellOne,
                                       SummonerSpell newSummonerSpellOne,
                                       String summonerName) {
        this.allPlayersEventType = allPlayersEventType;
        this.allPlayersEventTime = allPlayersEventTime;
        this.allGameData = allGameData;
        this.player = player;
        this.championName = championName;
        this.oldSummonerSpellOne = oldSummonerSpellOne;
        this.newSummonerSpellOne = newSummonerSpellOne;
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

    public SummonerSpell getOldSummonerSpellOne() {
        return this.oldSummonerSpellOne;
    }

    public SummonerSpell getNewSummonerSpellOne() {
        return this.newSummonerSpellOne;
    }

    public String getSummonerName() {
        return this.summonerName;
    }
}
