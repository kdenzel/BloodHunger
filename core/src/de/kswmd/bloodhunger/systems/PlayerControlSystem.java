package de.kswmd.bloodhunger.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import de.kswmd.bloodhunger.BloodHungerGame;
import de.kswmd.bloodhunger.components.*;
import de.kswmd.bloodhunger.skins.SkinElement;
import de.kswmd.bloodhunger.utils.Mapper;

public class PlayerControlSystem extends EntitySystem {

    private ImmutableArray<Entity> entities;
    private ImmutableArray<Entity> flashLights;
    private ImmutableArray<Entity> playerLights;

    @Override
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(PlayerComponent.class, VelocityComponent.class).get());
        flashLights = engine.getEntitiesFor(Family.all(FlashLightComponent.class).get());
        playerLights = engine.getEntitiesFor(Family.all(PlayerLightComponent.class).get());
    }

    @Override
    public void removedFromEngine(Engine engine) {
        entities = null;
    }

    @Override
    public void update(float deltaTime) {
        for (Entity playerComponent : entities) {
            PositionComponent positionComponent = Mapper.positionComponent.get(playerComponent);
            VelocityComponent vc = Mapper.velocityComponent.get(playerComponent);
            PlayerComponent pc = Mapper.playerComponent.get(playerComponent);
            BoundsComponent boundsComponent = Mapper.boundsComponent.get(playerComponent);
            DimensionComponent dimensionComponent = Mapper.dimensionComponent.get(playerComponent);
            RotationComponent rotationComponent = Mapper.rotationComponent.get(playerComponent);

            pc.feetAnimationType = PlayerComponent.FeetAnimationType.IDLE;
            vc.velocityVec.setLength(0);
            float speed = 100* BloodHungerGame.UNIT_SCALE;
            boolean run = false;
            if (Gdx.input.isKeyPressed(Input.Keys.W)) {
                pc.feetAnimationType = PlayerComponent.FeetAnimationType.MOVE_FORWARD;
                SkinElement feetSkinElement = pc.getSkin().getFeetAnimationSkinElement(pc.feetAnimationType);
                if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                    speed *= 2;
                    feetSkinElement.animation.setFrameDuration(feetSkinElement.initialFrameDuration/2);
                    run = true;
                } else {
                    feetSkinElement.animation.setFrameDuration(feetSkinElement.initialFrameDuration);
                }
                vc.velocityVec.set(0, speed);
            } else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                vc.velocityVec.set(0, -speed);
                pc.feetAnimationType = PlayerComponent.FeetAnimationType.MOVE_BACKWARD;
                rotationComponent.movementAngle += -180;
            } else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                vc.velocityVec.set(-speed, 0);
                pc.feetAnimationType = PlayerComponent.FeetAnimationType.MOVE_LEFT;
                rotationComponent.movementAngle += 90;
            } else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                vc.velocityVec.set(speed, 0);
                pc.feetAnimationType = PlayerComponent.FeetAnimationType.MOVE_RIGHT;
                rotationComponent.movementAngle += -90;
            }
            pc.update(deltaTime);
            //Set polygon depending on frame for bodyanimation
            PlayerComponent.BodyAnimationType bodyAnimationType = pc.getBodyAnimationType();
            SkinElement bodySkinElement = pc.getSkin().getBodyAnimationSkinElement(bodyAnimationType);
            if(run){
                bodySkinElement.animation.setFrameDuration(bodySkinElement.initialFrameDuration/2);
            } else {
                bodySkinElement.animation.setFrameDuration(bodySkinElement.initialFrameDuration);
            }
            if (bodySkinElement.hasPolygons()) {
                boundsComponent.setPolygon(bodySkinElement.getPolygonInWorldSize(pc.timer, dimensionComponent), 1);
            }
            Vector2 weaponFront = pc.getSkin().getTransformedToolPositionWithOffset(positionComponent,dimensionComponent,rotationComponent);
            flashLights.forEach(f -> {
                FlashLightComponent flashLightComponent = Mapper.flashLightComponent.get(f);
                PositionComponent fpos = Mapper.positionComponent.get(f);
                RotationComponent frot = Mapper.rotationComponent.get(f);
                frot.lookingAngle = rotationComponent.lookingAngle;
                fpos.set(weaponFront);
                flashLightComponent.setPosition(weaponFront.x, weaponFront.y);
            });
            playerLights.forEach(pl -> {
                PlayerLightComponent playerLightComponent = Mapper.playerLightComponent.get(pl);
                PositionComponent plpos = Mapper.positionComponent.get(pl);
                plpos.x = positionComponent.x + dimensionComponent.originX;
                plpos.y = positionComponent.y + dimensionComponent.originY;
                playerLightComponent.setPosition(plpos.x, plpos.y);
            });
        }
    }
}
