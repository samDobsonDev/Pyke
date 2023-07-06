package com.samdobsondev.pyke.model.events.activeplayer;

import com.samdobsondev.pyke.model.data.AllGameData;

public interface ActivePlayerEvent {
    ActivePlayerEventType getActivePlayerEventType();
    Double getActivePlayerEventTime();
    AllGameData getAllGameData();
}
