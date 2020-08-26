package de.kswmd.bloodhunger.systems;

import box2dLight.RayHandler;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.maps.MapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import de.kswmd.bloodhunger.Assets;
import de.kswmd.bloodhunger.BloodHungerGame;
import de.kswmd.bloodhunger.components.*;
import de.kswmd.bloodhunger.factories.EntityFactory;
import de.kswmd.bloodhunger.skins.SkinElement;
import de.kswmd.bloodhunger.ui.inventory.Inventory;
import de.kswmd.bloodhunger.utils.LevelManager;
import de.kswmd.bloodhunger.utils.Mapper;

import java.util.List;

public class RenderingSystem extends EntitySystem {

    private final Batch batch;
    private final OrthographicCamera camera;
    private ImmutableArray<Entity> playerAnimationEntities;
    private ImmutableArray<Entity> crosshairEntities;
    private ImmutableArray<Entity> itemEntities;
    private OrthogonalTiledMapRenderer mapRenderer;
    private ParticleEffectPool shootEffectPool;
    private Array<ParticleEffectPool.PooledEffect> effects = new Array<>();
    private TextureAtlas images = BloodHungerGame.ASSET_MANAGER.get(Assets.TEXTURE_ATLAS_IMAGES);

    //Lights
    private RayHandler rayHandler;

    public RenderingSystem(Batch batch, OrthographicCamera camera, RayHandler rayHandler) {
        this.batch = batch;
        this.camera = camera;
        TextureAtlas particles = BloodHungerGame.ASSET_MANAGER.get(Assets.TEXTURE_ATLAS_PARTICLES);
        ParticleEffect shootEffect = new ParticleEffect();
        shootEffect.load(Gdx.files.internal("particles/shoot.p"), particles);
        shootEffect.scaleEffect(BloodHungerGame.UNIT_SCALE);
        this.shootEffectPool = new ParticleEffectPool(shootEffect, 1, 200);
        this.rayHandler = rayHandler;
    }

    public void updateLevel() {
        if (mapRenderer != null) {
            mapRenderer.dispose();
        }
        mapRenderer = new OrthogonalTiledMapRenderer(LevelManager.getInstance().getTiledMap(), BloodHungerGame.UNIT_SCALE, batch);
    }

    public void setAmbientLight(float r, float g, float b, float a) {
        rayHandler.setAmbientLight(r, g, b, a);
    }

    @Override
    public void addedToEngine(Engine engine) {
        playerAnimationEntities = engine.getEntitiesFor(Family.all(PlayerComponent.class).get());
        crosshairEntities = engine.getEntitiesFor(Family.all(FollowMouseComponent.class).exclude(PlayerComponent.class).get());
        itemEntities = engine.getEntitiesFor(Family.all(ItemComponent.class).get());
    }

    @Override
    public void removedFromEngine(Engine engine) {
        playerAnimationEntities = null;
        crosshairEntities = null;
        itemEntities = null;
    }

    @Override
    public void update(float deltaTime) {
        renderLevel(deltaTime);
        batch.begin();
        batch.setProjectionMatrix(camera.combined);
        renderItems(deltaTime);
        renderPlayers(deltaTime);
        batch.end();
        rayHandler.setCombinedMatrix(camera);
        rayHandler.updateAndRender();
        batch.begin();
        renderEffects(deltaTime);
        renderCrosshair(deltaTime);
        batch.end();

    }

    /**
     * FOR TEXTURE BLEEDING ADD PADDING BETWEEN THE TILES WITH DUPLICATE PADDING (COPY PIXELS TO BORDER)
     *
     * @param deltaTime
     */
    private void renderLevel(float deltaTime) {
        mapRenderer.setView(camera);
        mapRenderer.render();
    }

    private void renderItems(float deltaTime) {
        TextureAtlas images = BloodHungerGame.ASSET_MANAGER.get(Assets.TEXTURE_ATLAS_IMAGES);
        itemEntities.forEach(item -> {
            ItemComponent itemComponent = Mapper.itemComponent.get(item);
            PositionComponent positionComponent = Mapper.positionComponent.get(item);
            DimensionComponent dimensionComponent = Mapper.dimensionComponent.get(item);

            TextureRegion region = images.findRegion(itemComponent.itemType.resourceImage);
            batch.draw(region, positionComponent.x, positionComponent.y, dimensionComponent.originX, dimensionComponent.originY,
                    dimensionComponent.width, dimensionComponent.height, 1, 1, 0);
        });
    }

    private void renderPlayers(float deltaTime) {
        for (Entity entity : playerAnimationEntities) {
            PositionComponent positionComponent = Mapper.positionComponent.get(entity);
            DimensionComponent dimensionComponent = Mapper.dimensionComponent.get(entity);
            RotationComponent rotationComponent = Mapper.rotationComponent.get(entity);

            PlayerComponent playerComponent = Mapper.playerComponent.get(entity);

            PlayerComponent.BodyAnimationType bodyAnimationType = playerComponent.getBodyAnimationType();
            SkinElement bodySkinElement = playerComponent.getSkin().getBodyAnimationSkinElement(bodyAnimationType);
            SkinElement feetSkinElement = playerComponent.getSkin().getFeetAnimationSkinElement(playerComponent.feetAnimationType);

            TextureRegion bodyRegion = bodySkinElement.animation.getKeyFrame(playerComponent.timer);
            TextureRegion feetRegion = feetSkinElement.animation.getKeyFrame(playerComponent.timer);

            float bodyWidthInDimensions = dimensionComponent.width / bodyRegion.getRegionWidth();
            float bodyHeightInDimensions = dimensionComponent.height / bodyRegion.getRegionHeight();

            batch.draw(feetRegion, positionComponent.x + (bodyRegion.getRegionWidth() / 2f - feetRegion.getRegionWidth() / 2f) * bodyWidthInDimensions,
                    positionComponent.y + (bodyRegion.getRegionHeight() / 2f - feetRegion.getRegionHeight() / 2f) * bodyHeightInDimensions,
                    (feetRegion.getRegionWidth() / 2f) * bodyWidthInDimensions, (feetRegion.getRegionHeight() / 2f) * bodyHeightInDimensions,
                    feetRegion.getRegionWidth() * bodyWidthInDimensions, feetRegion.getRegionHeight() * bodyHeightInDimensions, dimensionComponent.scaleX, dimensionComponent.scaleY,
                    rotationComponent.lookingAngle);

            batch.draw(bodyRegion, positionComponent.x, positionComponent.y,
                    dimensionComponent.originX, dimensionComponent.originY,
                    dimensionComponent.width, dimensionComponent.height, dimensionComponent.scaleX, dimensionComponent.scaleY,
                    rotationComponent.lookingAngle);
        }
    }

    private void renderEffects(float deltaTime) {
        // Update and draw effects:
        for (int i = effects.size - 1; i >= 0; i--) {
            ParticleEffectPool.PooledEffect effect = effects.get(i);
            effect.draw(batch, deltaTime);
            if (effect.isComplete()) {
                effect.free();
                effects.removeIndex(i);
            }
        }
    }

    private void renderCrosshair(float deltaTime) {
        for (Entity entity : crosshairEntities) {
            PositionComponent positionComponent = Mapper.positionComponent.get(entity);
            DimensionComponent dimensionComponent = Mapper.dimensionComponent.get(entity);
            RotationComponent rotationComponent = Mapper.rotationComponent.get(entity);
            batch.draw(images.findRegion("crosshair"), positionComponent.x, positionComponent.y,
                    dimensionComponent.originX, dimensionComponent.originY,
                    dimensionComponent.width, dimensionComponent.height, dimensionComponent.scaleX, dimensionComponent.scaleY,
                    rotationComponent.lookingAngle);
        }
    }

    public void onShoot(PlayerComponent playerComponent, PositionComponent positionComponent, DimensionComponent dimensionComponent, RotationComponent rotationComponent) {
        if (playerComponent.getTool().getStatus().equals(PlayerComponent.ToolStatus.SHOOT)) {
            // Create effect:
            ParticleEffectPool.PooledEffect effect = shootEffectPool.obtain();
            Vector2 bulletPosition = playerComponent.getSkin().getTransformedToolPositionWithOffset(positionComponent, dimensionComponent, rotationComponent);
            float x = bulletPosition.x;
            float y = bulletPosition.y;
            effect.setPosition(x, y);
            effect.getEmitters().forEach(e -> {
                ParticleEmitter.ScaledNumericValue val = e.getAngle();
                float amplitude = (val.getHighMax() - val.getHighMin()) / 2f;
                float h1 = rotationComponent.lookingAngle + amplitude;
                float h2 = rotationComponent.lookingAngle - amplitude;
                val.setHigh(h1, h2);
                val.setLow(rotationComponent.lookingAngle);
            });
            effects.add(effect);
        }
    }
}
