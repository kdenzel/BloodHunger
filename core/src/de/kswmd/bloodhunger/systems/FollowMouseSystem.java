package de.kswmd.bloodhunger.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import de.kswmd.bloodhunger.components.FollowMouseComponent;
import de.kswmd.bloodhunger.components.PositionComponent;
import de.kswmd.bloodhunger.components.RotationComponent;

public class FollowMouseSystem extends EntitySystem {

    private Camera camera;
    private Vector3 screenVector = new Vector3();
    private ImmutableArray<Entity> entities;

    private ComponentMapper<PositionComponent> cmpc = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<RotationComponent> cmrc = ComponentMapper.getFor(RotationComponent.class);

    public FollowMouseSystem(Camera camera) {
        this.camera = camera;
    }

    @Override
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(PositionComponent.class, RotationComponent.class, FollowMouseComponent.class).get());
    }

    @Override
    public void removedFromEngine(Engine engine) {
        entities = null;
    }

    @Override
    public void update(float deltaTime) {
        for (int i = 0; i < entities.size(); i++){

            Entity entity = entities.get(i);


            PositionComponent pc = cmpc.get(entity);
            float playerPosX = pc.x;
            float playerPosY = pc.y;

            float mouseScreenX = Gdx.input.getX();
            float mouseScreenY = Gdx.input.getY();
            screenVector.set(mouseScreenX, mouseScreenY, 0);
            camera.unproject(screenVector);
            float angle = (MathUtils.atan2(playerPosY - screenVector.y, playerPosX - screenVector.x) / MathUtils.PI) * 180;
            angle = 180 + angle;
            RotationComponent rc = cmrc.get(entity);
            rc.movementAngle = angle;
            rc.lookingAngle = angle;
        }
    }
}
