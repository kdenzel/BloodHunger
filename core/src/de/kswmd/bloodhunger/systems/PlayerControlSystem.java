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
            //updates the animation timer
            pc.update(deltaTime);
            pc.feetAnimationType = PlayerComponent.FeetAnimationType.IDLE;
            vc.velocityVec.setLength(0);
            //Speed of player per default 100 units per second
            float speed = 100* BloodHungerGame.UNIT_SCALE;
            boolean run = false;

            SkinElement feetSkinElement = pc.getSkin().getFeetAnimationSkinElement(pc.feetAnimationType);
            if (Gdx.input.isKeyPressed(Input.Keys.W) && rotationComponent.dst2 > BloodHungerGame.worldUnits(0.25f)) {
                pc.feetAnimationType = PlayerComponent.FeetAnimationType.MOVE_FORWARD;

                if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                    speed *= 2;
                    feetSkinElement.getAnimation().setFrameDuration(feetSkinElement.initialFrameDuration/2);
                    run = true;
                } else {
                    feetSkinElement.getAnimation().setFrameDuration(feetSkinElement.initialFrameDuration);
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

            PlayerComponent.BodyAnimationType bodyAnimationType = pc.getBodyAnimationType();
            SkinElement bodySkinElement = pc.getSkin().getBodyAnimationSkinElement(bodyAnimationType);
            //Set speed of animation 2 times higher if the player is running
            if(run){
                bodySkinElement.getAnimation().setFrameDuration(bodySkinElement.initialFrameDuration/2);
            } else {
                bodySkinElement.getAnimation().setFrameDuration(bodySkinElement.initialFrameDuration);
            }
            //update Polygons for each frame defined in skinelement
            if(feetSkinElement.hasPolygons()){
                boundsComponent.setPolygon(feetSkinElement.getPolygonInWorldSize(pc.timer, dimensionComponent), 0);
            }

            if (bodySkinElement.hasPolygons()) {
                boundsComponent.setPolygon(bodySkinElement.getPolygonInWorldSize(pc.timer, dimensionComponent), 1);
            }

            //Set flashlights depending on Position of Player
            Vector2 weaponFront = pc.getSkin().getTransformedToolPositionWithOffset(positionComponent,dimensionComponent,rotationComponent);
            flashLights.forEach(f -> {
                FlashLightComponent flashLightComponent = Mapper.flashLightComponent.get(f);
                PositionComponent fpos = Mapper.positionComponent.get(f);
                RotationComponent frot = Mapper.rotationComponent.get(f);
                frot.lookingAngle = rotationComponent.lookingAngle;
                fpos.set(weaponFront);
                flashLightComponent.setPosition(weaponFront.x, weaponFront.y);
            });
            //Set player lights depending on Position of Player
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
