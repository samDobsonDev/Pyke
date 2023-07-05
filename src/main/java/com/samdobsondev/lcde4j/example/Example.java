package com.samdobsondev.lcde4j.example;

import com.samdobsondev.lcde4j.api.LCDE4J;
import com.samdobsondev.lcde4j.api.listener.AnnouncerNotificationEventListener;
import com.samdobsondev.lcde4j.api.listener.GameDataEventListener;
import com.samdobsondev.lcde4j.model.events.announcer.MinionsSpawningEvent;
import com.samdobsondev.lcde4j.model.events.gamedata.MapTerrainChangeEvent;

public class Example {
    public static void main(String[] args)
    {
        LCDE4J lcde4J = new LCDE4J();
        lcde4J.start();
        lcde4J.registerGameDataEventListener(new GameDataEventListener()
        {
            @Override
            public void onMapTerrainChange(MapTerrainChangeEvent event) {
                System.out.println("Map Terrain has changed to: " + event.getMapTerrain());
            }
        });

        lcde4J.registerAnnouncerNotificationEventListener(new AnnouncerNotificationEventListener()
        {
            @Override
            public void onMinionsSpawning(MinionsSpawningEvent event) {
                System.out.println("Minions Spawning!");
            }
        });
    }
}

