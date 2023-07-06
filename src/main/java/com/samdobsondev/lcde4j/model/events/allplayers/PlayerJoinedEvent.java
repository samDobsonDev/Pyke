package com.samdobsondev.lcde4j.model.events.allplayers;

import com.samdobsondev.lcde4j.model.data.AllGameData;
import com.samdobsondev.lcde4j.model.data.allplayers.Player;
import com.samdobsondev.lcde4j.model.data.allplayers.SummonerSpell;
import com.samdobsondev.lcde4j.model.data.common.Rune;
import com.samdobsondev.lcde4j.model.data.common.RuneTree;

public class PlayerJoinedEvent implements AllPlayersEvent {
    private final AllPlayersEventType allPlayersEventType;
    private final Double allPlayersEventTime;
    private final AllGameData allGameData;
    private final Player player;
    private final String championName;
    private final Boolean isBot;
    private final String position;
    private final String rawChampionName;
    private final Rune keystone;
    private final RuneTree primaryRuneTree;
    private final RuneTree secondaryRuneTree;
    private final Long skinID;
    private final String summonerName;
    private final SummonerSpell summonerSpellOne;
    private final SummonerSpell summonerSpellTwo;
    private final String team;

    public PlayerJoinedEvent(AllPlayersEventType allPlayersEventType,
                             Double allPlayersEventTime,
                             AllGameData allGameData,
                             Player player,
                             String championName,
                             Boolean isBot,
                             String position,
                             String rawChampionName,
                             Rune keystone,
                             RuneTree primaryRuneTree,
                             RuneTree secondaryRuneTree,
                             Long skinID,
                             String summonerName,
                             SummonerSpell summonerSpellOne,
                             SummonerSpell summonerSpellTwo,
                             String team) {
        this.allPlayersEventType = allPlayersEventType;
        this.allPlayersEventTime = allPlayersEventTime;
        this.allGameData = allGameData;
        this.player = player;
        this.championName = championName;
        this.isBot = isBot;
        this.position = position;
        this.rawChampionName = rawChampionName;
        this.keystone = keystone;
        this.primaryRuneTree = primaryRuneTree;
        this.secondaryRuneTree = secondaryRuneTree;
        this.skinID = skinID;
        this.summonerName = summonerName;
        this.summonerSpellOne = summonerSpellOne;
        this.summonerSpellTwo = summonerSpellTwo;
        this.team = team;
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

    public Boolean getBot() {
        return this.isBot;
    }

    public String getPosition() {
        return this.position;
    }

    public String getRawChampionName() {
        return this.rawChampionName;
    }

    public Rune getKeystone() {
        return this.keystone;
    }

    public RuneTree getPrimaryRuneTree() {
        return this.primaryRuneTree;
    }

    public RuneTree getSecondaryRuneTree() {
        return this.secondaryRuneTree;
    }

    public Long getSkinID() {
        return this.skinID;
    }

    public String getSummonerName() {
        return this.summonerName;
    }

    public SummonerSpell getSummonerSpellOne() {
        return this.summonerSpellOne;
    }

    public SummonerSpell getSummonerSpellTwo() {
        return this.summonerSpellTwo;
    }

    public String getTeam() {
        return this.team;
    }
}
