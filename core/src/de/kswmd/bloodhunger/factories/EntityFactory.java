package de.kswmd.bloodhunger.factories;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import de.kswmd.bloodhunger.BloodHungerGame;
import de.kswmd.bloodhunger.components.*;
import de.kswmd.bloodhunger.screens.GameScreen;
import de.kswmd.bloodhunger.ui.inventory.Inventory;
import de.kswmd.bloodhunger.ui.inventory.InventorySlot;
import de.kswmd.bloodhunger.utils.LevelManager;
import de.kswmd.bloodhunger.utils.Mapper;

import java.util.ArrayList;
import java.util.List;

public final class EntityFactory {

    private EntityFactory() {
    }

    public static Entity createPlayer(float x, float y, Inventory inventory) {
        Entity player = new Entity();
        player.add(new PositionComponent(x,y));
        player.add(new VelocityComponent());
        player.add(new DimensionComponent(128 * BloodHungerGame.UNIT_SCALE, 128 * BloodHungerGame.UNIT_SCALE));
        player.add(new RotationComponent());
        player.add(new CenterCameraComponent());
        player.add(new PlayerComponent(inventory));
        DimensionComponent dc = Mapper.dimensionComponent.get(player);
        //Creates new boundscomponent with feet vertices for z-layer 0
        BoundsComponent bc = new BoundsComponent(dc.width, dc.height,
                new float[]{
                        0.28125f * dc.width, 0.6484375f * dc.height,
                        0.69921875f * dc.width, 0.65234375f * dc.height,
                        0.703125f * dc.width, 0.28515625f * dc.height,
                        0.28125f * dc.width, 0.29296875f * dc.height
                });
        player.add(bc);
        return player;
    }

    public static Entity createWall(float x, float y, float width, float height, TextureRegion textureRegion) {
        Entity wall = new Entity();
        wall.add(new PositionComponent(x, y));
        wall.add(new TextureRegionComponent(textureRegion));
        wall.add(new DimensionComponent(width, height));
        wall.add(new RotationComponent());
        BoundsComponent bc = new BoundsComponent(Mapper.dimensionComponent.get(wall));
        bc.getPolygon(0).setPosition(x,y);
        wall.add(bc);
        return wall;
    }

    public static Entity createEnemey(float x, float y, float width, float height, TextureRegion textureRegion) {
        Entity enemy = new Entity();
        enemy.add(new PositionComponent(x, y));
        enemy.add(new VelocityComponent());
        enemy.add(new TextureRegionComponent(textureRegion));
        DimensionComponent dc = new DimensionComponent(width, height);
        enemy.add(dc);
        enemy.add(new RotationComponent());
        BoundsComponent bc = new BoundsComponent(dc.width, dc.height,
                new float[]{
                        0.0f * dc.width, 0.0f * dc.height,
                        0.5f * dc.width, 0.0f * dc.height,
                        0.5f * dc.width, 0.5f * dc.height,
                        0.0f * dc.width, 0.5f * dc.height
                });
        bc.setBoundaryPolygon(4,1);
        enemy.add(bc);
        enemy.add(new EnemyComponent());
        return enemy;
    }

    public static Entity createBullet(float x, float y, float angle) {
        Entity bullet = new Entity();
        bullet.add(new PositionComponent(x, y));
        bullet.add(new VelocityComponent(2000 * BloodHungerGame.UNIT_SCALE, angle));
        bullet.add(new DimensionComponent(32 * BloodHungerGame.UNIT_SCALE, 32 * BloodHungerGame.UNIT_SCALE));
        bullet.add(new BoundsComponent(Mapper.dimensionComponent.get(bullet), 16));
        bullet.add(new BulletComponent());
        return bullet;
    }

    public static Entity createRoom(float x, float y, int tileSizeX, int tileSizeY, Engine engine) {
        Entity room = new Entity();
        room.add(new PositionComponent(x, y));
        room.add(new RoomComponent(tileSizeX, tileSizeY, engine, room));
        return room;
    }

    public static Entity createTile(float x, float y) {
        Entity tile = new Entity();
        tile.add(new PositionComponent(x, y));
        tile.add(new TileComponent());
        return tile;
    }

    public static List<Entity> createMapObjects(BloodHungerGame game, MapLayer mapLayer) {
        List<Entity> entities = new ArrayList<>(16);
        MapObjects objects = mapLayer.getObjects();
        objects.forEach(mapObject -> {
            MapProperties properties = mapObject.getProperties();
            String typeKey = "type";
            if (properties.containsKey(typeKey)) {
                if (properties.get(typeKey, String.class).equals("stone")) {
                    float x = properties.get("x", Float.class) * BloodHungerGame.UNIT_SCALE;
                    float y = properties.get("y", Float.class) * BloodHungerGame.UNIT_SCALE;

                    Polygon poly = ((PolygonMapObject) mapObject).getPolygon();
                    Rectangle rect = poly.getBoundingRectangle();
                    float[] v = new float[poly.getVertices().length];
                    for (int i = 0; i < poly.getVertices().length; i++) {
                        v[i] = poly.getVertices()[i] * BloodHungerGame.UNIT_SCALE;
                    }
                    Entity stone = createStone(x, y, rect.width * BloodHungerGame.UNIT_SCALE, rect.height * BloodHungerGame.UNIT_SCALE, v);
                    entities.add(stone);
                }
            }
        });
        return entities;
    }

    public static Entity createStone(float x, float y, float width, float height, float[] vertices) {
        Entity stone = new Entity();
        stone.add(new PositionComponent(x, y));
        stone.add(new DimensionComponent(width, height));
        BoundsComponent bc = new BoundsComponent(Mapper.dimensionComponent.get(stone));
        bc.setPolygon(vertices, 0);
        bc.setPosition(x,y);
        stone.add(bc);
        return stone;
    }

    public static Entity createCrosshair(float x, float y, float width, float height) {
        Entity crossHair = new Entity();
        crossHair.add(new PositionComponent(x,y));
        crossHair.add(new DimensionComponent(width,height));
        crossHair.add(new FollowMouseComponent());
        crossHair.add(new RotationComponent());
        return crossHair;
    }

    public static Entity createStaticLight(float x, float y, LightComponent.Type type){
        Entity light = new Entity();
        light.add(new PositionComponent(x,y));
        light.add(new RotationComponent());
        light.add(new LightComponent(type));
        return light;
    }

    public static Entity createDynamicLight(float x, float y, LightComponent.Type type){
        Entity light = createStaticLight(x,y,type);
        light.add(new VelocityComponent());
        return light;
    }

    public static Entity createFlashLight(float x, float y){
        Entity light = createDynamicLight(x,y, LightComponent.Type.CONE);
        light.remove(LightComponent.class);
        light.add(new FlashLightComponent(LightComponent.Type.CONE));
        return light;
    }

    public static Entity createItem(float x, float y,float width, float height,ItemComponent.ItemType itemType){
        Entity item = new Entity();
        item.add(new PositionComponent(x,y));
        item.add(new DimensionComponent(width,height));
        BoundsComponent bc = new BoundsComponent(width,height);
        bc.setPosition(x,y);
        item.add(bc);
        item.add(new ItemComponent(itemType));
        return item;
    }

    public static Entity createLevelExit(float x, float y, float width, float height, Screen nextScreen, LevelManager.Level level){
        Entity item = new Entity();
        item.add(new PositionComponent(x,y));
        item.add(new DimensionComponent(width,height));
        BoundsComponent bc = new BoundsComponent(width,height);
        bc.setPosition(x,y);
        item.add(bc);
        item.add(new LevelExitComponent(nextScreen,level));
        return item;
    }

}
