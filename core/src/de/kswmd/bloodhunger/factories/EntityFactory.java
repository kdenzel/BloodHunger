package de.kswmd.bloodhunger.factories;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import de.kswmd.bloodhunger.components.*;
import de.kswmd.bloodhunger.screens.GameScreen;
import de.kswmd.bloodhunger.utils.Mapper;

public final class EntityFactory {

    private EntityFactory() {}

    public static Entity createPlayer(){
        Entity player = new Entity();
        player.add(new PositionComponent());
        player.add(new VelocityComponent());
        player.add(new DimensionComponent(150,150));
        player.add(new RotationComponent());
        player.add(new FollowMouseComponent());
        player.add(new CenterCameraComponent());
        player.add(new BoundsComponent(Mapper.dimensionComponent.get(player),0.5f,10));
        player.add(new PlayerComponent());
        return player;
    }

    public static Entity createWall(float x, float y, float width, float height, TextureRegion textureRegion){
        Entity wall = new Entity();
        wall.add(new PositionComponent(x,y));
        wall.add(new TextureRegionComponent(textureRegion));
        wall.add(new DimensionComponent(width,height));
        wall.add(new RotationComponent());
        wall.add(new BoundsComponent(Mapper.dimensionComponent.get(wall)));
        return wall;
    }

    public static Entity createEnemey(float x, float y, float width, float height, TextureRegion textureRegion){
        Entity enemy = new Entity();
        enemy.add(new PositionComponent(x,y));
        enemy.add(new VelocityComponent());
        enemy.add(new TextureRegionComponent(textureRegion));
        enemy.add(new DimensionComponent(width,height));
        enemy.add(new RotationComponent());
        enemy.add(new BoundsComponent(Mapper.dimensionComponent.get(enemy)));
        enemy.add(new EnemyComponent());
        return enemy;
    }

    public static Entity createBullet(float x, float y, float angle){
        Entity bullet = new Entity();
        bullet.add(new PositionComponent(x,y));
        bullet.add(new VelocityComponent(2000,angle));
        bullet.add(new DimensionComponent(GameScreen.UNIT_SIZE/2,GameScreen.UNIT_SIZE/2));
        bullet.add(new BoundsComponent(Mapper.dimensionComponent.get(bullet),16));
        bullet.add(new BulletComponent());
        return bullet;
    }

    public static Entity createRoom(float x, float y, int tileSizeX, int tileSizeY, Engine engine){
        Entity room = new Entity();
        room.add(new PositionComponent(x,y));
        room.add(new RoomComponent(tileSizeX,tileSizeY,engine,room));
        return room;
    }

    public static Entity createTile(float x, float y){
        Entity tile = new Entity();
        tile.add(new PositionComponent(x,y));
        tile.add(new TileComponent());
        return tile;
    }

}
