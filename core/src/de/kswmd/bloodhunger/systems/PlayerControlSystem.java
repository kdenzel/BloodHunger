package de.kswmd.bloodhunger.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import de.kswmd.bloodhunger.BloodHungerGame;
import de.kswmd.bloodhunger.components.*;
import de.kswmd.bloodhunger.utils.Mapper;

public class PlayerControlSystem extends EntitySystem {

    private ImmutableArray<Entity> entities;
    private ImmutableArray<Entity> flashLights;

    @Override
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(PlayerComponent.class, VelocityComponent.class).get());
        flashLights = engine.getEntitiesFor(Family.all(FlashLightComponent.class).get());
    }

    @Override
    public void removedFromEngine(Engine engine) {
        entities = null;
    }

    @Override
    public void update(float deltaTime) {
        for (Entity e : entities) {
            PositionComponent positionComponent = Mapper.positionComponent.get(e);
            VelocityComponent vc = Mapper.velocityComponent.get(e);
            PlayerComponent pc = Mapper.playerComponent.get(e);
            BoundsComponent boundsComponent = Mapper.boundsComponent.get(e);
            DimensionComponent dimensionComponent = Mapper.dimensionComponent.get(e);
            RotationComponent rotationComponent = Mapper.rotationComponent.get(e);

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
                rotationComponent.movementAngle += -180;
            } else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                vc.velocityVec.set(-speed, 0);
                pc.feetAnimationType = RenderingSystem.FeetAnimationType.MOVE_LEFT;
                rotationComponent.movementAngle += 90;
            } else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                vc.velocityVec.set(speed, 0);
                pc.feetAnimationType = RenderingSystem.FeetAnimationType.MOVE_RIGHT;
                rotationComponent.movementAngle += -90;
            }
            pc.timer += deltaTime % 10;
            //Set polygon depending on frame for bodyanimation
            RenderingSystem.BodyAnimationType bodyAnimationType = pc.getBodyAnimationType();
            if (bodyAnimationType.hasPolygons()) {
                boundsComponent.setPolygon(bodyAnimationType.getVertices(pc.timer, dimensionComponent), 1);
            }
            Vector2 weaponFront = pc.getTool().getTransformedToolPositionWithOffset(positionComponent,dimensionComponent,rotationComponent);
            flashLights.forEach(f -> {
                PositionComponent fpos = Mapper.positionComponent.get(f);
                RotationComponent frot = Mapper.rotationComponent.get(f);
                frot.lookingAngle = rotationComponent.lookingAngle;
                fpos.set(weaponFront);
            });
        }
    }
}
