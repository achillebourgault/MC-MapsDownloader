package com.nostalgiamaps.manager;

import com.nostalgiamaps.MapInstance;
import com.nostalgiamaps.NostalgiaMaps;

public class MapsManager {

    private MapInstance currentMap;

    public MapsManager() {
        this.currentMap = null;
    }

    public MapInstance getCurrentMap() {
        return currentMap;
    }
}
