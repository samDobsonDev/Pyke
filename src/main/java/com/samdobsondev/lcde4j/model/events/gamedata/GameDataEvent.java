package com.samdobsondev.lcde4j.model.events.gamedata;

import com.samdobsondev.lcde4j.model.data.AllGameData;
import lombok.Data;

@Data
public class GameDataEvent {
    private GameDataEventType gameDataEventType;
    private Double gameDataEventTime;
    private AllGameData allGameData;
    private String gameMode;
    private Double gameTime;
    private String mapName;
    private Long mapNumber;
    private String mapTerrain;
}
