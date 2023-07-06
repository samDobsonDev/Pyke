package com.samdobsondev.lcde4j.model.events.activeplayer;

import com.samdobsondev.lcde4j.model.data.AllGameData;

public interface ActivePlayerEvent {
    ActivePlayerEventType getActivePlayerEventType();
    Double getActivePlayerEventTime();
    AllGameData getAllGameData();
}
