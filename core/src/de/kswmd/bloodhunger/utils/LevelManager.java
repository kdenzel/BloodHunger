package de.kswmd.bloodhunger.utils;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import de.kswmd.bloodhunger.Assets;
import de.kswmd.bloodhunger.BloodHungerGame;

public final class LevelManager {

    public enum Level {
        EXAMPLE(Assets.MAP_TMX_EXAMPLE),
        LEVEL_1(Assets.MAP_TMX_LEVEL1),
        LEVEL_2(Assets.MAP_TMX_LEVEL2);

        private TiledMap level;
        private String map;

        Level(String map) {
            this.map = map;
        }

        public String getMap() {
            return map;
        }
    }

    private static LevelManager INSTANCE;

    public Level level = Level.EXAMPLE;
    private TiledMap tiledMap;

    private LevelManager() {
    }

    public static LevelManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new LevelManager();
        }
        return INSTANCE;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public String getMap() {
        return level.map;
    }

    public void setTiledMap(TiledMap tiledMap) {
        this.tiledMap = tiledMap;
    }

    public TiledMap getTiledMap() {
        return tiledMap;
    }
}
