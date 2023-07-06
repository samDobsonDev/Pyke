package com.samdobsondev.pyke.model.data.activeplayer;

import com.samdobsondev.pyke.model.data.activeplayer.abilities.Abilities;
import com.samdobsondev.pyke.model.data.activeplayer.championstats.ChampionStats;
import com.samdobsondev.pyke.model.data.activeplayer.fullrunes.FullRunes;
import lombok.Data;

@Data
public class ActivePlayer {
    private Abilities abilities;
    private ChampionStats championStats;
    private Double currentGold;
    private FullRunes fullRunes;
    private Long level;
    private String summonerName;
    private Boolean teamRelativeColors;
}
