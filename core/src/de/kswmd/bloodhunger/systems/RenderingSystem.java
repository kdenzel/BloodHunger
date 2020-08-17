package de.kswmd.bloodhunger.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import de.kswmd.bloodhunger.Assets;
import de.kswmd.bloodhunger.BloodHungerGame;
import de.kswmd.bloodhunger.components.DimensionComponent;
import de.kswmd.bloodhunger.components.PlayerComponent;
import de.kswmd.bloodhunger.components.PositionComponent;
import de.kswmd.bloodhunger.components.RotationComponent;
import de.kswmd.bloodhunger.factories.EntityFactory;
import de.kswmd.bloodhunger.utils.LevelManager;
import de.kswmd.bloodhunger.utils.Mapper;

import java.util.List;

public class RenderingSystem extends EntitySystem {

    private static TextureAtlas atlas;

    public enum FeetAnimationType{
        IDLE(new Animation<TextureRegion>(1f, atlas.findRegions("Top_Down_Survivor_custom/feet/idle/survivor-idle"), Animation.PlayMode.LOOP)),
        MOVE_FORWARD(new Animation<TextureRegion>(0.05f, atlas.findRegions("Top_Down_Survivor_custom/feet/walk/survivor-walk"), Animation.PlayMode.LOOP)),
        MOVE_BACKWARD(new Animation<TextureRegion>(0.05f, atlas.findRegions("Top_Down_Survivor_custom/feet/walk/survivor-walk"), Animation.PlayMode.LOOP_REVERSED)),
        MOVE_LEFT(new Animation<TextureRegion>(0.05f, atlas.findRegions("Top_Down_Survivor_custom/feet/strafe_left/survivor-strafe_left"), Animation.PlayMode.LOOP)),
        MOVE_RIGHT(new Animation<TextureRegion>(0.05f, atlas.findRegions("Top_Down_Survivor_custom/feet/strafe_right/survivor-strafe_right"), Animation.PlayMode.LOOP)),
        RUN_FORWARD(new Animation<TextureRegion>(0.025f, atlas.findRegions("Top_Down_Survivor_custom/feet/walk/survivor-walk"), Animation.PlayMode.LOOP));

        private final Animation<TextureRegion> animation;

        FeetAnimationType(Animation<TextureRegion> animation){
            this.animation = animation;
        }
    }

    public enum BodyAnimationType{
        IDLE_FLASHLIGHT(new Animation<TextureRegion>(0.1f, atlas.findRegions("Top_Down_Survivor_custom/flashlight/idle/survivor-idle_flashlight"), Animation.PlayMode.LOOP)),
        MOVE_FLASHLIGHT(new Animation<TextureRegion>(0.1f, atlas.findRegions("Top_Down_Survivor_custom/flashlight/move/survivor-move_flashlight"), Animation.PlayMode.LOOP)),
        MELEE_FLASHLIGHT(new Animation<TextureRegion>(0.1f, atlas.findRegions("Top_Down_Survivor_custom/flashlight/meleeattack/survivor-meleeattack_flashlight"), Animation.PlayMode.NORMAL)),
        IDLE_HANDGUN(new Animation<TextureRegion>(0.1f, atlas.findRegions("Top_Down_Survivor_custom/handgun/idle/survivor-idle_handgun"), Animation.PlayMode.LOOP)),
        MOVE_HANDGUN(new Animation<TextureRegion>(0.1f, atlas.findRegions("Top_Down_Survivor_custom/handgun/move/survivor-move_handgun"), Animation.PlayMode.LOOP)),
        SHOOT_HANDGUN(new Animation<TextureRegion>(1/48f, atlas.findRegions("Top_Down_Survivor_custom/handgun/shoot/survivor-shoot_handgun"), Animation.PlayMode.NORMAL)),
        MELEE_HANDGUN(new Animation<TextureRegion>(1/48f, atlas.findRegions("Top_Down_Survivor_custom/handgun/meleeattack/survivor-meleeattack_handgun"), Animation.PlayMode.NORMAL)),
        RELOAD_HANDGUN(new Animation<TextureRegion>(1/24f, atlas.findRegions("Top_Down_Survivor_custom/handgun/reload/survivor-reload_handgun"), Animation.PlayMode.NORMAL));


        public final Animation<TextureRegion> animation;

        BodyAnimationType(Animation<TextureRegion> animation){
            this.animation = animation;
        }
    }

    private Batch batch;
    private OrthographicCamera camera;
    private ImmutableArray<Entity> playerAnimationEntities;
    private MapRenderer mapRenderer;
    private final AssetManager assetManager;

    public RenderingSystem(Batch batch, OrthographicCamera camera, AssetManager assetManager) {
        this.batch = batch;
        this.camera = camera;
        RenderingSystem.atlas = assetManager.get(Assets.BLOODHUNGER_TEXTURE_ATLAS);
        this.assetManager = assetManager;
    }

    public void setLevel(LevelManager.Level level){
        LevelManager.getInstance().setLevel(level);
        LevelManager.getInstance().setTiledMap(assetManager.get(level.getMap()));
        mapRenderer = new OrthogonalTiledMapRenderer(LevelManager.getInstance().getTiledMap(),BloodHungerGame.UNIT_SCALE,this.batch);
        List<Entity> entities = EntityFactory.createMapObjects(LevelManager.getInstance().getTiledMap().getLayers().get("object_layer"));
        entities.forEach(entity -> getEngine().addEntity(entity));
    }

    @Override
    public void addedToEngine(Engine engine) {
        playerAnimationEntities = engine.getEntitiesFor(Family.all(PlayerComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        renderLevel(deltaTime);
        batch.begin();
        batch.setProjectionMatrix(camera.combined);
        renderPlayers(deltaTime);
        batch.end();
    }

    private void renderLevel(float deltaTime){
        mapRenderer.setView(camera);
        mapRenderer.render();
    }

    private void renderPlayers(float deltaTime) {
        for (Entity entity : playerAnimationEntities) {
            PositionComponent positionComponent = Mapper.positionComponent.get(entity);
            DimensionComponent dimensionComponent = Mapper.dimensionComponent.get(entity);
            RotationComponent rotationComponent = Mapper.rotationComponent.get(entity);

            PlayerComponent playerComponent = Mapper.playerComponent.get(entity);
            playerComponent.timer+=deltaTime%10;

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
                    dimensionComponent.originX, dimensionComponent.originY,
                    dimensionComponent.width, dimensionComponent.height, dimensionComponent.scaleX, dimensionComponent.scaleY,
                    rotationComponent.lookingAngle);
        }
    }
}
