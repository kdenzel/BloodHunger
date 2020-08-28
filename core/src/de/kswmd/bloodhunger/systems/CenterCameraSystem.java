package de.kswmd.bloodhunger.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.Camera;
import de.kswmd.bloodhunger.components.CenterCameraComponent;
import de.kswmd.bloodhunger.components.DimensionComponent;
import de.kswmd.bloodhunger.components.PositionComponent;
import de.kswmd.bloodhunger.utils.Mapper;

public class CenterCameraSystem extends EntitySystem {

    private ImmutableArray<Entity> entities;
    private Camera camera;

    public CenterCameraSystem(Camera camera) {
        this.camera = camera;
    }

    @Override
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(CenterCameraComponent.class).get());
    }

    @Override
    public void removedFromEngine(Engine engine) {
        entities = null;
    }

    @Override
    public void update(float deltaTime) {
        for (Entity e : entities) {
            if (Mapper.positionComponent.has(e) && Mapper.dimensionComponent.has(e)) {
                PositionComponent pc = Mapper.positionComponent.get(e);
                DimensionComponent dc = Mapper.dimensionComponent.get(e);
                camera.position.set(pc.x+dc.originX, pc.y+dc.originY, 0);
                camera.update();
            }
        }
    }
}
