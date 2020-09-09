package de.kswmd.bloodhunger.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import de.kswmd.bloodhunger.components.BoundsComponent;
import de.kswmd.bloodhunger.components.RotationComponent;
import de.kswmd.bloodhunger.components.VelocityComponent;
import de.kswmd.bloodhunger.utils.Mapper;

public class RotationSystem extends IteratingSystem {

    private static final String TAG = RotationSystem.class.getSimpleName();

    public RotationSystem() {
        super(Family.all(RotationComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        //Gdx.app.debug(TAG, "EXECUTE " + deltaTime);
        super.update(deltaTime);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        RotationComponent rc = Mapper.rotationComponent.get(entity);
        if(Mapper.boundsComponent.has(entity)){
            Mapper.boundsComponent.get(entity).rotate(rc.lookingAngle);
        }
        if(Mapper.velocityComponent.has(entity)){
            Mapper.velocityComponent.get(entity).velocityVec.setAngle(rc.movementAngle);
        }

        if(Mapper.flashLightComponent.has(entity)){
            Mapper.flashLightComponent.get(entity).getLightReference().setDirection(rc.lookingAngle);
        }
    }
}
