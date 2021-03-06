package de.kswmd.bloodhunger.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import de.kswmd.bloodhunger.components.*;
import de.kswmd.bloodhunger.utils.Mapper;

public class MovementSystem extends IteratingSystem {

    private static final String TAG = MovementSystem.class.getSimpleName();

    public MovementSystem() {
        super(Family.all(PositionComponent.class, VelocityComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        //Gdx.app.debug(TAG, "EXECUTE " + deltaTime);
        super.update(deltaTime);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PositionComponent pc = Mapper.positionComponent.get(entity);
        VelocityComponent vc = Mapper.velocityComponent.get(entity);
        pc.x += vc.velocityVec.x * deltaTime;
        pc.y += vc.velocityVec.y * deltaTime;
        if (Mapper.boundsComponent.has(entity)) {
            BoundsComponent bc = Mapper.boundsComponent.get(entity);
            bc.setPosition(pc.x, pc.y);
        }
        if (Mapper.lightComponent.has(entity)) {
            LightComponent lc = Mapper.lightComponent.get(entity);
            lc.setPosition(pc.x, pc.y);
        }
    }
}
