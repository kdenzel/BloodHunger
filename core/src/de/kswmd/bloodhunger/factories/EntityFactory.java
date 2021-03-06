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
        player.add(new RotationComponent());
        player.add(playerComponent);
        DimensionComponent dc = new DimensionComponent(BloodHungerGame.worldUnits(2), BloodHungerGame.worldUnits(2));
        //Creates new boundscomponent with feet vertices for z-layer 0
        BoundsComponent bc = new BoundsComponent(dc.width, dc.height, Box2DBodyFactory.CATEGORY_BOUNDARY, player);
        player.add(dc);
        player.add(bc);
        bc.setPosition(x,y);
        return player;
    }

    public static Entity createWall(float x, float y, float width, float height, TextureRegion textureRegion) {
        Entity wall = new Entity();
        wall.add(new PositionComponent(x, y));
        wall.add(new TextureRegionComponent(textureRegion));
        wall.add(new DimensionComponent(width, height));
        BoundsComponent bc = new BoundsComponent(width, height, Box2DBodyFactory.CATEGORY_BOUNDARY, wall);
        bc.setBoundaryRectangle(0).setBoundaryRectangle(1);
        bc.setPosition(x, y);
        wall.add(bc);
        return wall;
    }

    public static Entity createBasicObstacle(float x, float y, float width, float height,short lightcategory) {
        Entity obstacle = new Entity();
        obstacle.add(new PositionComponent(x, y));
        obstacle.add(new DimensionComponent(width, height));
        BoundsComponent bc = new BoundsComponent(width, height, lightcategory, obstacle);
        bc.setBoundaryRectangle(0);
        bc.setPosition(x, y);
        obstacle.add(bc);
        return obstacle;
    }


    public static Entity createBasicObstacle(float x, float y, float width, float height, short lightcategory, float[] vertices) {
        Entity stone = new Entity();
        stone.add(new PositionComponent(x, y));
        stone.add(new DimensionComponent(width, height));
        BoundsComponent bc = new BoundsComponent(width, height, lightcategory, stone);
        bc.setPolygon(vertices, 0);
        bc.setPosition(x, y);
        stone.add(bc);
        return stone;
    }

    public static Entity createBasicObstacle(float x, float y, float width, float height, short lightcategory, float[] vertices, int z) {
        Entity stone = new Entity();
        stone.add(new PositionComponent(x, y));
        stone.add(new DimensionComponent(width, height));
        BoundsComponent bc = new BoundsComponent(width, height, lightcategory, stone);
        bc.setPolygon(vertices, z);
        bc.setPosition(x, y);
        stone.add(bc);
        return stone;
    }

    public static Entity createWindow(float x, float y, float width, float height, TextureRegion textureRegion) {
        Entity wall = new Entity();
        wall.add(new PositionComponent(x, y));
        wall.add(new TextureRegionComponent(textureRegion));
        wall.add(new DimensionComponent(width, height));
        BoundsComponent bc = new BoundsComponent(width, height, Box2DBodyFactory.CATEGORY_IGNORE, wall);
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
        BoundsComponent bc = new BoundsComponent(width, height, Box2DBodyFactory.CATEGORY_ROOF, roof);
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
        enemy.add(new BoundsComponent(width, height, Box2DBodyFactory.CATEGORY_BOUNDARY,enemy).setBoundaryRectangle(0));
        enemy.add(new ZombieComponent());
        return enemy;
    }

    public static Entity createBullet(float x, float y, float angle) {
        float width = 32 * BloodHungerGame.UNIT_SCALE;
        float height = 32 * BloodHungerGame.UNIT_SCALE;
        Entity bullet = new Entity();
        bullet.add(new PositionComponent(x, y));
        bullet.add(new VelocityComponent(2000 * BloodHungerGame.UNIT_SCALE, angle));
        bullet.add(new DimensionComponent(width, height));
        bullet.add(new BoundsComponent(width, height, Box2DBodyFactory.CATEGORY_BOUNDARY, bullet).setBoundaryPolygon(16, 0));
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
        BoundsComponent bc = new BoundsComponent(width, height, Box2DBodyFactory.CATEGORY_IGNORE, item);
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
        BoundsComponent bc = new BoundsComponent(width, height, Box2DBodyFactory.CATEGORY_IGNORE, item);
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
        BoundsComponent bc = new BoundsComponent(width, height, Box2DBodyFactory.CATEGORY_IGNORE, entity);
        bc.setBoundaryRectangle(0);
        bc.setPosition(x, y);
        entity.add(bc);
        entity.add(new PlayerSkinComponent(skin));
        return entity;
    }

}
