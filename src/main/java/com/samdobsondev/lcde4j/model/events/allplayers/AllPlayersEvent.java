package com.samdobsondev.lcde4j.model.events.allplayers;

import com.samdobsondev.lcde4j.model.data.AllGameData;
import com.samdobsondev.lcde4j.model.data.allplayers.Player;

public interface AllPlayersEvent {
    AllPlayersEventType getAllPlayersEventType();
    Double getAllPlayersEventTime();
    AllGameData getAllGameData();
    Player getPlayer();
}
