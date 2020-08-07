package de.kswmd.bloodhunger.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import de.kswmd.bloodhunger.components.PlayerComponent;
import de.kswmd.bloodhunger.components.VelocityComponent;
import de.kswmd.bloodhunger.utils.Mapper;

public class PlayerControlSystem extends EntitySystem {

    private ImmutableArray<Entity> entities;

    @Override
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(PlayerComponent.class, VelocityComponent.class).get());
    }

    @Override
    public void removedFromEngine(Engine engine) {
        entities = null;
    }

    @Override
    public void update(float deltaTime) {
        for(Entity e : entities) {
            VelocityComponent vc = Mapper.velocityComponent.get(e);
            vc.velocityVec.setLength(0);
            int speed = 100;
            if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)){
                speed *= 2;
            }
            if(Gdx.input.isKeyPressed(Input.Keys.W)){
                vc.velocityVec.set(0,speed);
            } else if(Gdx.input.isKeyPressed(Input.Keys.S)){
                vc.velocityVec.set(0,-speed);
                Mapper.rotationComponent.get(e).movementAngle += -180;
            } else if(Gdx.input.isKeyPressed(Input.Keys.A)){
                vc.velocityVec.set(-speed,0);
                Mapper.rotationComponent.get(e).movementAngle += 90;
            } else if(Gdx.input.isKeyPressed(Input.Keys.D)) {
                vc.velocityVec.set(speed,0);
                Mapper.rotationComponent.get(e).movementAngle += -90;
            }
        }
    }
}
