package com.samdobsondev.pyke.model.data;

import com.google.gson.annotations.SerializedName;
import com.samdobsondev.pyke.model.data.activeplayer.ActivePlayer;
import com.samdobsondev.pyke.model.data.allplayers.Player;
import com.samdobsondev.pyke.model.data.announcer.AnnouncerNotifications;
import com.samdobsondev.pyke.model.data.gamedata.GameData;
import lombok.Data;

import java.util.List;

@Data
public class AllGameData {
    private ActivePlayer activePlayer;
    private List<Player> allPlayers;
    @SerializedName("events")
    private AnnouncerNotifications announcerNotifications;
    private GameData gameData;
}
