package de.kswmd.bloodhunger.factories;

import box2dLight.LightData;
import box2dLight.RayHandler;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import de.kswmd.bloodhunger.BloodHungerGame;
import de.kswmd.bloodhunger.components.*;
import de.kswmd.bloodhunger.skins.PlayerSkin;
import de.kswmd.bloodhunger.utils.LevelManager;
import de.kswmd.bloodhunger.utils.Mapper;

public final class EntityFactory {

    private EntityFactory() {
    }

    public static Entity createPlayer(float x, float y, PlayerComponent playerComponent) {
        Entity player = new Entity();
        player.add(new PositionComponent(x, y));
        player.add(new VelocityComponent());
        player.add(new DimensionComponent(128 * BloodHungerGame.UNIT_SCALE, 128 * BloodHungerGame.UNIT_SCALE));
        player.add(new RotationComponent());
        player.add(new CenterCameraComponent());
        player.add(playerComponent);
        DimensionComponent dc = Mapper.dimensionComponent.get(player);
        //Creates new boundscomponent with feet vertices for z-layer 0
        BoundsComponent bc = new BoundsComponent(dc.width, dc.height, Box2DBodyFactory.CATEGORY_BOUNDARY);
        bc.setPolygon(new float[]{
                0.28125f * dc.width, 0.6484375f * dc.height,
                0.69921875f * dc.width, 0.65234375f * dc.height,
                0.703125f * dc.width, 0.28515625f * dc.height,
                0.28125f * dc.width, 0.29296875f * dc.height
        }, 0);
        player.add(bc);
        return player;
    }

    public static Entity createWall(float x, float y, float width, float height, TextureRegion textureRegion) {
        Entity wall = new Entity();
        wall.add(new PositionComponent(x, y));
        wall.add(new TextureRegionComponent(textureRegion));
        wall.add(new DimensionComponent(width, height));
        BoundsComponent bc = new BoundsComponent(width, height, Box2DBodyFactory.CATEGORY_BOUNDARY);
        bc.setBoundaryRectangle(0).setBoundaryRectangle(1);
        bc.setPosition(x, y);
        wall.add(bc);
        return wall;
    }

    public static Entity createWindow(float x, float y, float width, float height, TextureRegion textureRegion) {
        Entity wall = new Entity();
        wall.add(new PositionComponent(x, y));
        wall.add(new TextureRegionComponent(textureRegion));
        wall.add(new DimensionComponent(width, height));
        BoundsComponent bc = new BoundsComponent(width, height, Box2DBodyFactory.CATEGORY_IGNORE);
        bc.setBoundaryRectangle(0).setBoundaryRectangle(1);
        bc.setPosition(x, y);
        wall.add(bc);
        return wall;
    }

    public static Entity createRoof(float x, float y, float width, float height, TextureRegion textureRegion) {
        Entity roof = new Entity();
        roof.add(new PositionComponent(x, y));
        roof.add(new TextureRegionComponent(textureRegion));
        roof.add(new DimensionComponent(width, height));
        BoundsComponent bc = new BoundsComponent(width, height, Box2DBodyFactory.CATEGORY_BOUNDARY);
        bc.setBoundaryRectangle(2);
        ((LightData) bc.getBody(2).getUserData()).shadow = true;
        bc.setPosition(x, y);
        roof.add(bc);
        return roof;
    }

    public static Entity createEnemey(float x, float y, float width, float height, TextureRegion textureRegion) {
        Entity enemy = new Entity();
        enemy.add(new PositionComponent(x, y));
        enemy.add(new VelocityComponent());
        enemy.add(new TextureRegionComponent(textureRegion));
        DimensionComponent dc = new DimensionComponent(width, height);
        enemy.add(dc);
        enemy.add(new RotationComponent());
        //Boundscomponent gets updated in Playercontrolsystem for each frame
        enemy.add(new BoundsComponent(width, height, Box2DBodyFactory.CATEGORY_BOUNDARY).setBoundaryRectangle(0));
        enemy.add(new EnemyComponent());
        return enemy;
    }

    public static Entity createBullet(float x, float y, float angle) {
        float width = 32 * BloodHungerGame.UNIT_SCALE;
        float height = 32 * BloodHungerGame.UNIT_SCALE;
        Entity bullet = new Entity();
        bullet.add(new PositionComponent(x, y));
        bullet.add(new VelocityComponent(2000 * BloodHungerGame.UNIT_SCALE, angle));
        bullet.add(new DimensionComponent(width, height));
        bullet.add(new BoundsComponent(width, height, Box2DBodyFactory.CATEGORY_BOUNDARY).setBoundaryPolygon(16, 0));
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

    public static Entity createStone(float x, float y, float width, float height, float[] vertices) {
        Entity stone = new Entity();
        stone.add(new PositionComponent(x, y));
        stone.add(new DimensionComponent(width, height));
        BoundsComponent bc = new BoundsComponent(width, height, Box2DBodyFactory.CATEGORY_BOUNDARY);
        bc.setPolygon(vertices, 0);
        bc.setPosition(x, y);
        stone.add(bc);
        return stone;
    }

    public static Entity createCrosshair(float x, float y, float width, float height) {
        Entity crossHair = new Entity();
        crossHair.add(new PositionComponent(x, y));
        crossHair.add(new DimensionComponent(width, height));
        crossHair.add(new FollowMouseComponent());
        crossHair.add(new RotationComponent());
        return crossHair;
    }

    public static Entity createStaticLight(float x, float y, LightComponent lightComponent) {
        Entity light = new Entity();
        light.add(new PositionComponent(x, y));
        light.add(lightComponent);
        return light;
    }

    public static Entity createDynamicLight(float x, float y, LightComponent lightComponent) {
        Entity light = createStaticLight(x, y, lightComponent);
        light.add(new VelocityComponent());
        return light;
    }

    public static Entity createFlashLight(float x, float y, RayHandler rayHandler) {
        Entity light = createDynamicLight(x, y, new FlashLightComponent());
        light.add(new RotationComponent());
        LightComponent lc = Mapper.flashLightComponent.get(light);
        lc.setLightReference(LightFactory.createFlashLight(rayHandler));
        lc.getLightReference().setActive(false);
        return light;
    }

    public static Entity createPlayerLight(float x, float y, RayHandler rayHandler) {
        PlayerLightComponent pcLc = new PlayerLightComponent();
        Entity light = createDynamicLight(x, y, pcLc);
        pcLc.setLightReference(LightFactory.createPlayerLight(rayHandler));
        return light;
    }

    public static Entity createItem(float x, float y, float width, float height, ItemComponent.ItemType itemType) {
        Entity item = new Entity();
        item.add(new PositionComponent(x, y));
        item.add(new DimensionComponent(width, height));
        BoundsComponent bc = new BoundsComponent(width, height, Box2DBodyFactory.CATEGORY_IGNORE);
        bc.setBoundaryRectangle(0);
        bc.setPosition(x, y);
        item.add(bc);
        item.add(new ItemComponent(itemType));
        return item;
    }

    public static Entity createLevelExit(float x, float y, float width, float height, Screen nextScreen, LevelManager.Level level) {
        Entity item = new Entity();
        item.add(new PositionComponent(x, y));
        item.add(new DimensionComponent(width, height));
        BoundsComponent bc = new BoundsComponent(width, height, Box2DBodyFactory.CATEGORY_IGNORE);
        bc.setBoundaryRectangle(0);
        bc.setPosition(x, y);
        item.add(bc);
        item.add(new LevelExitComponent(nextScreen, level));
        return item;
    }

    public static Entity createSkinEntity(float x, float y, float width, float height, PlayerSkin skin) {
        Entity entity = new Entity();
        entity.add(new PositionComponent(x, y));
        entity.add(new DimensionComponent(width, height));
        BoundsComponent bc = new BoundsComponent(width, height, Box2DBodyFactory.CATEGORY_IGNORE);
        bc.setBoundaryRectangle(0);
        bc.setPosition(x, y);
        entity.add(bc);
        entity.add(new PlayerSkinComponent(skin));
        return entity;
    }

}
