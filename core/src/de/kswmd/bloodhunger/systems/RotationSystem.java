package de.kswmd.bloodhunger.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import de.kswmd.bloodhunger.components.BoundsComponent;
import de.kswmd.bloodhunger.components.RotationComponent;
import de.kswmd.bloodhunger.components.VelocityComponent;
import de.kswmd.bloodhunger.utils.Mapper;

public class RotationSystem extends IteratingSystem {

    public RotationSystem() {
        super(Family.all(RotationComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        RotationComponent rc = Mapper.rotationComponent.get(entity);
        if(Mapper.boundsComponent.has(entity)){
            Mapper.boundsComponent.get(entity).rotate(rc.movementAngle);
        }
        if(Mapper.velocityComponent.has(entity)){
            Mapper.velocityComponent.get(entity).velocityVec.setAngle(rc.movementAngle);
        }
    }
}
