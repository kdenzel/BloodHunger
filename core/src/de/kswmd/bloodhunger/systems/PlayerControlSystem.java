package de.kswmd.bloodhunger.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import de.kswmd.bloodhunger.BloodHungerGame;
import de.kswmd.bloodhunger.components.BoundsComponent;
import de.kswmd.bloodhunger.components.DimensionComponent;
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
        for (Entity e : entities) {
            VelocityComponent vc = Mapper.velocityComponent.get(e);
            PlayerComponent pc = Mapper.playerComponent.get(e);
            BoundsComponent boundsComponent = Mapper.boundsComponent.get(e);
            DimensionComponent dimensionComponent = Mapper.dimensionComponent.get(e);

            pc.feetAnimationType = RenderingSystem.FeetAnimationType.IDLE;
            vc.velocityVec.setLength(0);
            float speed = 100* BloodHungerGame.UNIT_SCALE;
            if (Gdx.input.isKeyPressed(Input.Keys.W)) {
                if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                    speed *= 2;
                    pc.feetAnimationType = RenderingSystem.FeetAnimationType.RUN_FORWARD;
                } else {
                    pc.feetAnimationType = RenderingSystem.FeetAnimationType.MOVE_FORWARD;
                }
                vc.velocityVec.set(0, speed);
            } else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                vc.velocityVec.set(0, -speed);
                pc.feetAnimationType = RenderingSystem.FeetAnimationType.MOVE_BACKWARD;
                Mapper.rotationComponent.get(e).movementAngle += -180;
            } else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                vc.velocityVec.set(-speed, 0);
                pc.feetAnimationType = RenderingSystem.FeetAnimationType.MOVE_LEFT;
                Mapper.rotationComponent.get(e).movementAngle += 90;
            } else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                vc.velocityVec.set(speed, 0);
                pc.feetAnimationType = RenderingSystem.FeetAnimationType.MOVE_RIGHT;
                Mapper.rotationComponent.get(e).movementAngle += -90;
            }
            pc.timer += deltaTime % 10;
            //Set polygon depending on frame for bodyanimation
            RenderingSystem.BodyAnimationType bodyAnimationType = pc.getBodyAnimationType();
            if (bodyAnimationType.hasPolygons()) {
                boundsComponent.setPolygon(bodyAnimationType.getVertices(pc.timer, dimensionComponent), 1);
            }

        }
    }
}
