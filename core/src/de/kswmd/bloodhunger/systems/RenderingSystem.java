package de.kswmd.bloodhunger.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import de.kswmd.bloodhunger.components.*;
import de.kswmd.bloodhunger.utils.Mapper;

public class RenderingSystem extends EntitySystem {

    private static TextureAtlas atlas;

    public enum FeetAnimationType{
        IDLE(new Animation<TextureRegion>(1f, atlas.findRegions("survivor-idle"), Animation.PlayMode.LOOP)),
        MOVE_FORWARD(new Animation<TextureRegion>(0.05f, atlas.findRegions("survivor-walk"), Animation.PlayMode.LOOP)),
        MOVE_BACKWARD(new Animation<TextureRegion>(0.05f, atlas.findRegions("survivor-walk"), Animation.PlayMode.LOOP_REVERSED)),
        MOVE_LEFT(new Animation<TextureRegion>(0.05f, atlas.findRegions("survivor-strafe_left"), Animation.PlayMode.LOOP)),
        MOVE_RIGHT(new Animation<TextureRegion>(0.05f, atlas.findRegions("survivor-strafe_right"), Animation.PlayMode.LOOP)),
        RUN_FORWARD(new Animation<TextureRegion>(0.05f, atlas.findRegions("survivor-run"), Animation.PlayMode.LOOP))
        ;

        private final Animation<TextureRegion> animation;

        FeetAnimationType(Animation<TextureRegion> animation){
            this.animation = animation;
        }
    }

    public enum BodyAnimationType{
        IDLE_FLASHLIGHT(new Animation<TextureRegion>(0.1f, atlas.findRegions("survivor-idle_flashlight"), Animation.PlayMode.LOOP)),
        MOVE_FLASHLIGHT(new Animation<TextureRegion>(0.1f, atlas.findRegions("survivor-move_flashlight"), Animation.PlayMode.LOOP)),
        MELEE_FLASHLIGHT(new Animation<TextureRegion>(0.1f, atlas.findRegions("survivor-meleeattack_flashlight"), Animation.PlayMode.NORMAL)),
        IDLE_HANDGUN(new Animation<TextureRegion>(0.1f, atlas.findRegions("survivor-idle_handgun"), Animation.PlayMode.LOOP)),
        MOVE_HANDGUN(new Animation<TextureRegion>(0.1f, atlas.findRegions("survivor-move_handgun"), Animation.PlayMode.LOOP)),
        SHOOT_HANDGUN(new Animation<TextureRegion>(0.1f, atlas.findRegions("survivor-shoot_handgun"), Animation.PlayMode.NORMAL)),
        MELEE_HANDGUN(new Animation<TextureRegion>(0.1f, atlas.findRegions("survivor-meleeattack_handgun"), Animation.PlayMode.NORMAL)),
        RELOAD_HANDGUN(new Animation<TextureRegion>(0.1f, atlas.findRegions("survivor-reload_handgun"), Animation.PlayMode.NORMAL));


        public final Animation<TextureRegion> animation;

        BodyAnimationType(Animation<TextureRegion> animation){
            this.animation = animation;
        }
    }

    private Batch batch;
    private Camera camera;
    private ImmutableArray<Entity> playerAnimationEntities;

    public RenderingSystem(Batch batch, Camera camera, AssetManager assetManager) {
        this.batch = batch;
        this.camera = camera;
        RenderingSystem.atlas = assetManager.get("bloodHunger.atlas");
    }

    @Override
    public void addedToEngine(Engine engine) {
        playerAnimationEntities = engine.getEntitiesFor(Family.all(PlayerComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        batch.begin();
        batch.setProjectionMatrix(camera.combined);
        renderPlayers(deltaTime);
        batch.end();
    }

    private void renderPlayers(float deltaTime) {
        for (Entity entity : playerAnimationEntities) {
            PositionComponent positionComponent = Mapper.positionComponent.get(entity);
            DimensionComponent dimensionComponent = Mapper.dimensionComponent.get(entity);
            RotationComponent rotationComponent = Mapper.rotationComponent.get(entity);

            PlayerComponent playerComponent = Mapper.playerComponent.get(entity);
            playerComponent.timer+=deltaTime;

            TextureRegion bodyRegion = playerComponent.getBodyAnimationType().animation.getKeyFrame(playerComponent.timer);
            TextureRegion feetRegion = playerComponent.feetAnimationType.animation.getKeyFrame(playerComponent.timer);

            float bodyWidthInDimensions = dimensionComponent.width/bodyRegion.getRegionWidth();
            float bodyHeightInDimensions = dimensionComponent.height/bodyRegion.getRegionHeight();

            batch.draw(feetRegion, positionComponent.x + (bodyRegion.getRegionWidth()/2-feetRegion.getRegionWidth()/2)*bodyWidthInDimensions,
                    positionComponent.y +(bodyRegion.getRegionHeight()/2-feetRegion.getRegionHeight()/2)*bodyHeightInDimensions,
                    (feetRegion.getRegionWidth()/2)*bodyWidthInDimensions, (feetRegion.getRegionHeight()/2)*bodyHeightInDimensions,
                    feetRegion.getRegionWidth()*bodyWidthInDimensions, feetRegion.getRegionHeight()*bodyHeightInDimensions, dimensionComponent.scaleX, dimensionComponent.scaleY,
                    rotationComponent.lookingAngle);

            batch.draw(bodyRegion, positionComponent.x, positionComponent.y,
                    bodyRegion.getRegionWidth()/2*bodyWidthInDimensions, bodyRegion.getRegionHeight()/2*bodyHeightInDimensions,
                    bodyRegion.getRegionWidth()*bodyWidthInDimensions, bodyRegion.getRegionHeight()*bodyHeightInDimensions, dimensionComponent.scaleX, dimensionComponent.scaleY,
                    rotationComponent.lookingAngle);
        }
    }
}
