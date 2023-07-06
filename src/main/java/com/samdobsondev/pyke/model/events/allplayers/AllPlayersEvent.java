package com.samdobsondev.pyke.model.events.allplayers;

import com.samdobsondev.pyke.model.data.AllGameData;
import com.samdobsondev.pyke.model.data.allplayers.Player;

public interface AllPlayersEvent {
    AllPlayersEventType getAllPlayersEventType();
    Double getAllPlayersEventTime();
    AllGameData getAllGameData();
    Player getPlayer();
}
