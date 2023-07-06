package com.samdobsondev.pyke.model.data.allplayers;

import lombok.Data;

import java.util.List;

@Data
public class Player {
    private String championName;
    private Boolean isBot;
    private Boolean isDead;
    private List<Item> items;
    private Long level;
    private String position;
    private String rawChampionName;
    private Double respawnTimer;
    private Runes runes;
    private Scores scores;
    private Long skinID;
    private String summonerName;
    private SummonerSpells summonerSpells;
    private String team;
}
