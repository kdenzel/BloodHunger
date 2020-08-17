package de.kswmd.bloodhunger.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import de.kswmd.bloodhunger.components.*;
import de.kswmd.bloodhunger.utils.Mapper;

public class MovementSystem extends IteratingSystem {

    public MovementSystem() {
        super(Family.all(PositionComponent.class, VelocityComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PositionComponent pc = Mapper.positionComponent.get(entity);
        VelocityComponent vc = Mapper.velocityComponent.get(entity);
        pc.x += vc.velocityVec.x * deltaTime;
        pc.y += vc.velocityVec.y * deltaTime;
        if (Mapper.boundsComponent.has(entity)) {
            if (Mapper.dimensionComponent.has(entity)) {
                DimensionComponent dc = Mapper.dimensionComponent.get(entity);
                BoundsComponent bc = Mapper.boundsComponent.get(entity);
                //Move every polygon on all layers
                for (int z = 0; z < bc.size(); z++) {
                    float x = pc.x + dc.originX - bc.getPolygon(z).getOriginX();
                    float y = pc.y + dc.originY - bc.getPolygon(z).getOriginY();
                    bc.getPolygon(z).setPosition(x, y);
                }
            } else {
                //Move every polygon on all layers
                BoundsComponent bc = Mapper.boundsComponent.get(entity);
                for (int z = 0; z < bc.size(); z++) {
                    bc.getPolygon(z).setPosition(pc.x, pc.y);
                }
            }
        }
    }
}
