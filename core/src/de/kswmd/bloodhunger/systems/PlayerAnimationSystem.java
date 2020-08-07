package de.kswmd.bloodhunger.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import de.kswmd.bloodhunger.components.DimensionComponent;
import de.kswmd.bloodhunger.components.PlayerAnimationComponent;
import de.kswmd.bloodhunger.components.PositionComponent;
import de.kswmd.bloodhunger.components.RotationComponent;
import de.kswmd.bloodhunger.utils.Mapper;

public class PlayerAnimationSystem extends EntitySystem {

    private Batch batch;
    private Camera camera;
    private ImmutableArray<Entity> playerAnimationEntities;

    public PlayerAnimationSystem(Batch batch, Camera camera) {
        this.batch = batch;
        this.camera = camera;
    }

    @Override
    public void addedToEngine(Engine engine) {
        playerAnimationEntities = engine.getEntitiesFor(Family.all(PlayerAnimationComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        batch.begin();
        batch.setProjectionMatrix(camera.combined);
        for (Entity entity : playerAnimationEntities) {
            PositionComponent positionComponent = Mapper.positionComponent.get(entity);
            DimensionComponent dimensionComponent = Mapper.dimensionComponent.get(entity);
            RotationComponent rotationComponent = Mapper.rotationComponent.get(entity);
            PlayerAnimationComponent animationComponent = Mapper.playerAnimationComponent.get(entity);
            TextureRegion bodyRegion = animationComponent.animationBody.getKeyFrame(deltaTime);
            TextureRegion feetRegion = animationComponent.animationFeet.getKeyFrame(deltaTime);

            batch.draw(feetRegion, positionComponent.x, positionComponent.y,
                    dimensionComponent.getOriginX(), dimensionComponent.getOriginY(),
                    dimensionComponent.width, dimensionComponent.height, dimensionComponent.scaleX, dimensionComponent.scaleY,
                    rotationComponent.lookingAngle);

            batch.draw(bodyRegion, positionComponent.x, positionComponent.y,
                    dimensionComponent.getOriginX(), dimensionComponent.getOriginY(),
                    dimensionComponent.width, dimensionComponent.height, dimensionComponent.scaleX, dimensionComponent.scaleY,
                    rotationComponent.lookingAngle);
        }
        batch.end();
    }
}
