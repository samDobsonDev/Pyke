package com.samdobsondev.lcde4j.model.events.activeplayer;

import com.samdobsondev.lcde4j.model.data.AllGameData;

public class SummonerNameEvent implements ActivePlayerEvent {
    private final ActivePlayerEventType activePlayerEventType;
    private final Double activePlayerEventTime;
    private final AllGameData allGameData;
    private final String summonerName;

    public SummonerNameEvent(ActivePlayerEventType activePlayerEventType,
                             Double activePlayerEventTime,
                             AllGameData allGameData,
                             String summonerName) {
        this.activePlayerEventType = activePlayerEventType;
        this.activePlayerEventTime = activePlayerEventTime;
        this.allGameData = allGameData;
        this.summonerName = summonerName;
    }

    @Override
    public ActivePlayerEventType getActivePlayerEventType() {
        return this.activePlayerEventType;
    }

    @Override
    public Double getActivePlayerEventTime() {
        return this.activePlayerEventTime;
    }

    @Override
    public AllGameData getAllGameData() {
        return this.allGameData;
    }

    public String getSummonerName() {
        return this.summonerName;
    }
}
