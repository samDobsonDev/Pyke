package com.samdobsondev.pyke.model.events.activeplayer;

import com.samdobsondev.pyke.model.data.AllGameData;

public class AbilityLevelUpEvent implements ActivePlayerEvent {
    private final ActivePlayerEventType activePlayerEventType;
    private final Double activePlayerEventTime;
    private final AllGameData allGameData;
    private final String ability;
    private final Long abilityLevel;

    public AbilityLevelUpEvent(ActivePlayerEventType activePlayerEventType,
                               Double activePlayerEventTime,
                               AllGameData allGameData,
                               String ability,
                               Long abilityLevel) {
        this.activePlayerEventType = activePlayerEventType;
        this.activePlayerEventTime = activePlayerEventTime;
        this.allGameData = allGameData;
        this.ability = ability;
        this.abilityLevel = abilityLevel;
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

    public String getAbility() {
        return this.ability;
    }

    public Long getAbilityLevel() {
        return this.abilityLevel;
    }
}
