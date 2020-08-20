package de.kswmd.bloodhunger.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
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
import de.kswmd.bloodhunger.utils.LevelManager;
import de.kswmd.bloodhunger.utils.Mapper;

import java.util.List;

public class RenderingSystem extends EntitySystem {

    public enum FeetAnimationType {
        IDLE(1f, "Top_Down_Survivor_custom/feet/idle/survivor-idle", Animation.PlayMode.LOOP),
        MOVE_FORWARD(1 / 24f, "Top_Down_Survivor_custom/feet/walk/survivor-walk", Animation.PlayMode.LOOP),
        MOVE_BACKWARD(1 / 24f, "Top_Down_Survivor_custom/feet/walk/survivor-walk", Animation.PlayMode.LOOP_REVERSED),
        MOVE_LEFT(1 / 24f, "Top_Down_Survivor_custom/feet/strafe_left/survivor-strafe_left", Animation.PlayMode.LOOP),
        MOVE_RIGHT(1 / 24f, "Top_Down_Survivor_custom/feet/strafe_right/survivor-strafe_right", Animation.PlayMode.LOOP),
        RUN_FORWARD(1 / 48f, "Top_Down_Survivor_custom/feet/walk/survivor-walk", Animation.PlayMode.LOOP);

        private final Animation<TextureRegion> animation;

        FeetAnimationType(float frameDuration, String resource, Animation.PlayMode playMode) {
            TextureAtlas atlas = BloodHungerGame.ASSET_MANAGER.get(Assets.TEXTURE_ATLAS_ANIMATIONS);
            this.animation = new Animation<>(frameDuration, atlas.findRegions(resource), playMode);
        }
    }

    public enum BodyAnimationType {
        IDLE_FLASHLIGHT(1 / 24f, "Top_Down_Survivor_custom/flashlight/idle/survivor-idle_flashlight", Animation.PlayMode.LOOP),
        MOVE_FLASHLIGHT(1 / 24f, "Top_Down_Survivor_custom/flashlight/move/survivor-move_flashlight", Animation.PlayMode.LOOP),
        MELEE_FLASHLIGHT(1 / 48f, "Top_Down_Survivor_custom/flashlight/meleeattack/survivor-meleeattack_flashlight", Animation.PlayMode.NORMAL),
        IDLE_HANDGUN(1 / 24f, "Top_Down_Survivor_custom/handgun/idle/survivor-idle_handgun", Animation.PlayMode.LOOP),
        MOVE_HANDGUN(1 / 24f, "Top_Down_Survivor_custom/handgun/move/survivor-move_handgun", Animation.PlayMode.LOOP),
        SHOOT_HANDGUN(1 / 48f, "Top_Down_Survivor_custom/handgun/shoot/survivor-shoot_handgun", Animation.PlayMode.NORMAL),
        MELEE_HANDGUN(1 / 48f, "Top_Down_Survivor_custom/handgun/meleeattack/survivor-meleeattack_handgun", Animation.PlayMode.NORMAL),
        RELOAD_HANDGUN(1 / 24f, "Top_Down_Survivor_custom/handgun/reload/survivor-reload_handgun", Animation.PlayMode.NORMAL);


        public final Animation<TextureRegion> animation;
        private final Array<float[]> polygonVertices = new Array<>();
        private final Array<float[]> polygonVerticesTransformed = new Array<>();

        BodyAnimationType(float frameDuration, String resource, Animation.PlayMode playMode) {
            TextureAtlas atlas = BloodHungerGame.ASSET_MANAGER.get(Assets.TEXTURE_ATLAS_ANIMATIONS);
            this.animation = new Animation<>(frameDuration, atlas.findRegions(resource), playMode);
            FileHandle handle = Gdx.files.internal("animation/" + resource + ".poly");
            if (handle.exists()) {
                String fileContent = handle.readString();
                String[] lines = fileContent.split("\\r?\\n");
                for (String line : lines) {
                    String[] array = line.replaceAll("[{}]", "").split(",");
                    float[] vertices = new float[array.length];
                    for (int j = 0; j < array.length; j++) {
                        vertices[j] = Float.parseFloat(array[j]);
                    }
                    polygonVertices.add(vertices);
                }
                polygonVertices.forEach(v -> polygonVerticesTransformed.add(new float[v.length]));
            }
        }

        public boolean hasPolygons() {
            return !polygonVertices.isEmpty();
        }

        public float[] getVertices(float time, float width, float height) {
            if (polygonVertices.isEmpty()) {
                return null;
            }
            float scale = ((float) animation.getKeyFrameIndex(time) / animation.getKeyFrames().length);
            int polygonFrame = (int) (polygonVertices.size * scale);


            float[] v = polygonVertices.get(polygonFrame);
            float[] tv = polygonVerticesTransformed.get(polygonFrame);
            for (int i = 0; i < v.length; i++) {
                if (i % 2 == 0) {
                    tv[i] = v[i] * width;
                } else {
                    tv[i] = v[i] * height;
                }
            }
            return tv;
        }

        public float[] getVertices(float time, DimensionComponent dimensionComponent) {
            return getVertices(time, dimensionComponent.width, dimensionComponent.height);
        }
    }

    private final Batch batch;
    private final OrthographicCamera camera;
    private ImmutableArray<Entity> playerAnimationEntities;
    private ImmutableArray<Entity> crosshairEntities;
    private MapRenderer mapRenderer;
    private ParticleEffectPool shootEffectPool;
    private Array<ParticleEffectPool.PooledEffect> effects = new Array<>();
    private TextureAtlas images = BloodHungerGame.ASSET_MANAGER.get(Assets.TEXTURE_ATLAS_IMAGES);

    public RenderingSystem(Batch batch, OrthographicCamera camera) {
        this.batch = batch;
        this.camera = camera;
        TextureAtlas particles = BloodHungerGame.ASSET_MANAGER.get(Assets.TEXTURE_ATLAS_PARTICLES);
        ParticleEffect shootEffect = new ParticleEffect();
        shootEffect.load(Gdx.files.internal("particles/shoot.p"), particles);
        shootEffect.scaleEffect(BloodHungerGame.UNIT_SCALE);
        this.shootEffectPool = new ParticleEffectPool(shootEffect, 1, 200);
    }

    public void setLevel(LevelManager.Level level) {
        LevelManager.getInstance().setLevel(level);
        LevelManager.getInstance().setTiledMap(BloodHungerGame.ASSET_MANAGER.get(level.getMap()));
        mapRenderer = new OrthogonalTiledMapRenderer(LevelManager.getInstance().getTiledMap(), BloodHungerGame.UNIT_SCALE, batch);
        List<Entity> entities = EntityFactory.createMapObjects(LevelManager.getInstance().getTiledMap().getLayers().get("objects"));
        entities.forEach(entity -> getEngine().addEntity(entity));
    }

    @Override
    public void addedToEngine(Engine engine) {
        playerAnimationEntities = engine.getEntitiesFor(Family.all(PlayerComponent.class).get());
        crosshairEntities = engine.getEntitiesFor(Family.all(FollowMouseComponent.class).exclude(PlayerComponent.class).get());
    }

    @Override
    public void removedFromEngine(Engine engine) {
        playerAnimationEntities = null;
    }

    @Override
    public void update(float deltaTime) {
        renderLevel(deltaTime);
        batch.begin();
        batch.setProjectionMatrix(camera.combined);
        renderPlayers(deltaTime);
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

    private void renderPlayers(float deltaTime) {
        for (Entity entity : playerAnimationEntities) {
            PositionComponent positionComponent = Mapper.positionComponent.get(entity);
            DimensionComponent dimensionComponent = Mapper.dimensionComponent.get(entity);
            RotationComponent rotationComponent = Mapper.rotationComponent.get(entity);

            PlayerComponent playerComponent = Mapper.playerComponent.get(entity);

            BodyAnimationType bodyAnimationType = playerComponent.getBodyAnimationType();
            TextureRegion bodyRegion = bodyAnimationType.animation.getKeyFrame(playerComponent.timer);
            TextureRegion feetRegion = playerComponent.feetAnimationType.animation.getKeyFrame(playerComponent.timer);

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
            batch.draw(images.findRegion("cursor"), positionComponent.x, positionComponent.y,
                    dimensionComponent.originX,dimensionComponent.originY,
                    dimensionComponent.width,dimensionComponent.height,dimensionComponent.scaleX,dimensionComponent.scaleY,
                    rotationComponent.lookingAngle);
        }
    }

    public void onShoot(PlayerComponent playerComponent, PositionComponent positionComponent, DimensionComponent dimensionComponent, RotationComponent rotationComponent) {
        if (playerComponent.weapon.getStatus().equals(PlayerComponent.WeaponStatus.SHOOT)) {
            // Create effect:
            ParticleEffectPool.PooledEffect effect = shootEffectPool.obtain();
            Vector2 bulletPosition = playerComponent.weapon.getInitialBulletPosition(positionComponent, dimensionComponent, rotationComponent);
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
