package com.samdobsondev.pyke.model.data.gamedata;

import lombok.Data;

@Data
public class GameData {
    private String gameMode;
    private Double gameTime;
    private String mapName;
    private Long mapNumber;
    private String mapTerrain;
}
