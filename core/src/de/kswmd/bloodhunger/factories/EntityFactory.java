package de.kswmd.bloodhunger.factories;

import com.badlogic.ashley.core.Entity;
import de.kswmd.bloodhunger.components.*;

public final class EntityFactory {

    private EntityFactory() {}

    public static Entity createPlayer(){
        Entity player = new Entity();
        player.add(new PositionComponent());
        player.add(new VelocityComponent());
        player.add(new TextureComponent());
        player.add(new PlayerControlComponent());
        player.add(new DimensionComponent(20,20,5));
        player.add(new RotationComponent());
        player.add(new FollowMouseComponent());
        player.add(new CenterCameraComponent());
        player.add(new BoundsComponent(player.getComponent(DimensionComponent.class)));
        return player;
    }

    public static Entity createWall(float x, float y, float width, float height){
        Entity wall = new Entity();
        wall.add(new PositionComponent(x,y));
        wall.add(new TextureComponent());
        wall.add(new DimensionComponent(width,height));
        wall.add(new RotationComponent());
        wall.add(new BoundsComponent(wall.getComponent(DimensionComponent.class)));
        return wall;
    }

}
