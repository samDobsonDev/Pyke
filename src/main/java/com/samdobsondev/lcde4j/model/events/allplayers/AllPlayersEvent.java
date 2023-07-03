package com.samdobsondev.lcde4j.model.events.allplayers;

import com.samdobsondev.lcde4j.model.data.AllGameData;
import com.samdobsondev.lcde4j.model.data.allplayers.Player;
import com.samdobsondev.lcde4j.model.data.allplayers.SummonerSpell;
import com.samdobsondev.lcde4j.model.data.common.Rune;
import com.samdobsondev.lcde4j.model.data.common.RuneTree;
import lombok.Data;

@Data
public class AllPlayersEvent {
    private AllPlayersEventType allPlayersEventType;
    private Double allPlayersEventTime;
    private AllGameData allGameData;
    private Player player;
    private String championName;
    private Boolean isBot;
    private Boolean isDead;
    private String item;
    private Long itemID;
    private Long itemPrice;
    private Long itemSlot;
    private Long level;
    private String position;
    private String rawChampionName;
    private Double respawnTimer;
    private Rune keystone;
    private RuneTree primaryRuneTree;
    private RuneTree secondaryRuneTree;
    private Long assists;
    private Long creepScore;
    private Long deaths;
    private Long kills;
    private Double wardScore;
    private Long skinID;
    private String summonerName;
    private SummonerSpell summonerSpellOne;
    private SummonerSpell summonerSpellTwo;
    private String team;
}
