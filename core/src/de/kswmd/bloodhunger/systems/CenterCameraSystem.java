package de.kswmd.bloodhunger.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.Camera;
import de.kswmd.bloodhunger.components.CenterCameraComponent;
import de.kswmd.bloodhunger.components.DimensionComponent;
import de.kswmd.bloodhunger.components.PositionComponent;

public class CenterCameraSystem extends EntitySystem {

    private ImmutableArray<Entity> entities;
    private Camera camera;

    private ComponentMapper<PositionComponent> cmpc = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<DimensionComponent> cmdc = ComponentMapper.getFor(DimensionComponent.class);

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
            if (cmpc.has(e) && cmdc.has(e)) {
                PositionComponent pc = cmpc.get(e);
                DimensionComponent dc = cmdc.get(e);
                camera.position.set(pc.x, pc.y, 0);
                camera.update();
            }
        }
    }
}
