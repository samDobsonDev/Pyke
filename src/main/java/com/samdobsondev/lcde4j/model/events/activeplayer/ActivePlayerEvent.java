package com.samdobsondev.lcde4j.model.events.activeplayer;


import com.samdobsondev.lcde4j.model.data.AllGameData;
import com.samdobsondev.lcde4j.model.data.activeplayer.fullrunes.StatRune;
import com.samdobsondev.lcde4j.model.data.common.Rune;
import com.samdobsondev.lcde4j.model.data.common.RuneTree;
import lombok.Data;

@Data
public class ActivePlayerEvent {
    private ActivePlayerEventType activePlayerEventType;
    private Double activePlayerEventTime;
    private AllGameData allGameData;
    private String ability;
    private String abilityLevel;
    private String championStat;
    private Double championStatAmount;
    private String resourceType;
    private Double goldAmount;
    private Rune rune;
    private RuneTree runeTree;
    private StatRune statRune;
    private Long level;
    private String summonerName;
    private Boolean teamRelativeColors;
}
