package de.kswmd.bloodhunger.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import de.kswmd.bloodhunger.components.*;

public class MovementSystem extends IteratingSystem {

    private ComponentMapper<VelocityComponent> cmvc = ComponentMapper.getFor(VelocityComponent.class);
    private ComponentMapper<PositionComponent> cmpc = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<BoundsComponent> cmbc = ComponentMapper.getFor(BoundsComponent.class);

    public MovementSystem() {
        super(Family.all(PositionComponent.class, VelocityComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PositionComponent pc = cmpc.get(entity);
        VelocityComponent vc = cmvc.get(entity);
        pc.x += vc.velocityVec.x * deltaTime;
        pc.y += vc.velocityVec.y * deltaTime;
        if(cmbc.has(entity)){
            cmbc.get(entity).boundaryPolygon.setPosition(pc.x, pc.y);
        }
    }
}
