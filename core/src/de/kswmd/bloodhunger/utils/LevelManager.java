package de.kswmd.bloodhunger.utils;

import box2dLight.Light;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import de.kswmd.bloodhunger.Assets;
import de.kswmd.bloodhunger.BloodHungerGame;
import de.kswmd.bloodhunger.components.LightComponent;
import de.kswmd.bloodhunger.factories.EntityFactory;
import de.kswmd.bloodhunger.factories.LightFactory;

public final class LevelManager {

    private static final String TAG = LevelManager.class.getSimpleName();

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
    private final Array<Entity> entities = new Array<>(16);

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

    public Array<Entity> loadMapObjects(BloodHungerGame game) {
        entities.clear();
        MapLayer mapLayer = tiledMap.getLayers().get("objects");
        MapObjects objects = mapLayer.getObjects();
        objects.forEach(mapObject -> placeMapObjectOnMap(game, mapObject));
        return entities;
    }

    private void placeMapObjectOnMap(BloodHungerGame game, MapObject mapObject) {
        MapProperties properties = mapObject.getProperties();
        if (!mapObject.isVisible())
            return;
        float x = BloodHungerGame.toWorldUnits(properties.get("x", Float.class));
        float y = BloodHungerGame.toWorldUnits(properties.get("y", Float.class));
        float width = BloodHungerGame.toWorldUnits(properties.get("width", Float.class));
        float height = BloodHungerGame.toWorldUnits(properties.get("height", Float.class));
        String type = (String) properties.get("type");
        String name = (String) properties.get("name");
        Gdx.app.debug(TAG, "Create " + type + ":" + name);
        if (type == null)
            return;
        switch (type.toLowerCase()) {
            case "obstacle":
                Polygon poly = ((PolygonMapObject) mapObject).getPolygon();
                Rectangle rect = poly.getBoundingRectangle();
                float[] v = new float[poly.getVertices().length];
                for (int i = 0; i < poly.getVertices().length; i++) {
                    v[i] = BloodHungerGame.toWorldUnits(poly.getVertices()[i]);
                }
                short lightCategory = properties.get("lightcategory",Integer.class).shortValue();
                Entity obstacle = EntityFactory.createBasicObstacle(x, y, BloodHungerGame.toWorldUnits(rect.width), BloodHungerGame.toWorldUnits(rect.height), lightCategory,v);
                entities.add(obstacle);
                break;
            case "wall":
                Entity wall = EntityFactory.createWall(x, y, width, height, null);
                entities.add(wall);
                break;
            case "light":
                String lighttype = (String) properties.get("ltype");
                Color c = properties.get("color", Color.class);
                float directionDegree = properties.get("directionDegree", Float.class);
                float coneDegree = properties.get("coneDegree", Float.class);
                float distance = BloodHungerGame.worldUnits(properties.get("distance", Float.class));
                int rays = properties.get("rays", Integer.class);
                float lheight = properties.get("lheight", Float.class);
                switch (lighttype.toLowerCase()) {
                    case "cone":
                        Light light = LightFactory.createConeLight(game.rayHandler, rays, c, x, y, distance, directionDegree, coneDegree);
                        LightComponent component = new LightComponent(light);
                        Entity entity = EntityFactory.createStaticLight(x, y, component);
                        entities.add(entity);
                        break;
                    case "directional":
                        light = LightFactory.createDirectionalLight(game.rayHandler, rays, c, directionDegree, lheight);
                        component = new LightComponent(light);
                        entity = EntityFactory.createStaticLight(x, y, component);
                        entities.add(entity);
                        break;
                    case "point":
                        light = LightFactory.createPointLight(game.rayHandler, rays, c, distance, x, y);
                        component = new LightComponent(light);
                        entity = EntityFactory.createStaticLight(x, y, component);
                        entities.add(entity);
                        break;
                }
                break;
            case "window":
                Entity window = EntityFactory.createWindow(x, y, width, height, null);
                entities.add(window);
                break;
            case "start":
                entities.add(EntityFactory.createPlayer(x, y, game.playerComponent));
                entities.add(EntityFactory.createCrosshair(0, 0, BloodHungerGame.toWorldUnits(48), BloodHungerGame.toWorldUnits(48)));
                break;
            case "roof":
                entities.add(EntityFactory.createRoof(x, y, width, height, null));
                break;
        }
    }
}
