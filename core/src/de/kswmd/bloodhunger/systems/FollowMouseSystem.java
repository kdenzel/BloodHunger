package de.kswmd.bloodhunger.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import de.kswmd.bloodhunger.components.*;
import de.kswmd.bloodhunger.utils.Mapper;

public class FollowMouseSystem extends EntitySystem {

    private Camera camera;
    private Vector3 screenVector = new Vector3();
    private ImmutableArray<Entity> followMouseEntities;
    private ImmutableArray<Entity> players;

    public FollowMouseSystem(Camera camera) {
        this.camera = camera;
    }

    @Override
    public void addedToEngine(Engine engine) {
        followMouseEntities = engine.getEntitiesFor(Family.all(PositionComponent.class, RotationComponent.class, FollowMouseComponent.class).get());
        players = engine.getEntitiesFor(Family.all(PlayerComponent.class).get());
    }

    @Override
    public void removedFromEngine(Engine engine) {
        followMouseEntities = null;
        players = null;
    }

    @Override
    public void update(float deltaTime) {
        float mouseScreenX = Gdx.input.getX();
        float mouseScreenY = Gdx.input.getY();
        screenVector.set(mouseScreenX, mouseScreenY, 0);
        camera.unproject(screenVector);
        Vector2 toolOffset = null;
        for (int i = 0; i < players.size(); i++) {
            Entity entity = players.get(i);

            PositionComponent pc = Mapper.positionComponent.get(entity);
            DimensionComponent dc = Mapper.dimensionComponent.get(entity);
            PlayerComponent playerComponent = Mapper.playerComponent.get(entity);

            float playerPosX = pc.x + dc.originX;
            float playerPosY = pc.y + dc.originY;

            float angle = (MathUtils.atan2(playerPosY - screenVector.y, playerPosX - screenVector.x) / MathUtils.PI) * 180;
            angle = 180 + angle;
            RotationComponent rc = Mapper.rotationComponent.get(entity);
            rc.movementAngle = angle;
            rc.lookingAngle = angle;
            toolOffset = playerComponent.getSkin().getTransformedToolOffset(dc, rc);
        }

        /*
         * sets all entities which follow the mouse for example the crosshair with the weapon offset, so aiming is accurate
         */
        if (toolOffset != null) {
            for (int i = 0; i < followMouseEntities.size(); i++) {
                Entity entity = followMouseEntities.get(i);
                PositionComponent pc = Mapper.positionComponent.get(entity);
                DimensionComponent dc = Mapper.dimensionComponent.get(entity);
                pc.set(screenVector.x - dc.originX + toolOffset.x, screenVector.y - dc.originY + toolOffset.y);
            }
        }
    }
}
