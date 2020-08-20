package de.kswmd.bloodhunger.screens;

import box2dLight.ConeLight;
import box2dLight.RayHandler;
import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import de.kswmd.bloodhunger.BloodHungerGame;
import de.kswmd.bloodhunger.components.*;
import de.kswmd.bloodhunger.factories.EntityFactory;
import de.kswmd.bloodhunger.systems.*;
import de.kswmd.bloodhunger.utils.LevelManager;
import de.kswmd.bloodhunger.utils.Mapper;

public class GameScreen extends BaseScreen implements EntityListener {

    private static final float TIME_STEP = 1 / 60f;
    private static final int VELOCITY_ITERATIONS = 6;
    private static final int POSITION_ITERATIONS = 2;

    private static final Vector2 BULLET_OFFSET = new Vector2();
    public static final World WORLD = new World(new Vector2(), true);

    private FollowMouseSystem followMouseSystem;
    private PlayerControlSystem playerControlSystem;
    private RotationSystem rotationSystem;
    private MovementSystem movementSystem;
    private BoundsCollisionSystem boundsCollisionSystem;
    private CenterCameraSystem centerCameraSystem;
    private DebugRenderSystem debugRenderSystem;
    private EnemyFollowPlayerSystem enemyFollowPlayerSystem;
    private RenderingSystem renderingSystem;
    private BulletSystem bulletSystem;

    private Engine engine;
    private ShapeRenderer shapeRenderer;
    private SpriteBatch spriteBatch;
    private RayHandler rayHandler;


    private float accumulator = 0;

    public GameScreen(BloodHungerGame game) {
        super(game);
    }

    @Override
    protected void initialize() {
        Gdx.input.setCursorCatched(!game.debug);
        spriteBatch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);
        rayHandler = new RayHandler(GameScreen.WORLD);
        engine = new Engine();
        engine.addEntityListener(this);

        followMouseSystem = new FollowMouseSystem(camera);
        playerControlSystem = new PlayerControlSystem();
        rotationSystem = new RotationSystem();
        movementSystem = new MovementSystem();
        boundsCollisionSystem = new BoundsCollisionSystem();
        centerCameraSystem = new CenterCameraSystem(camera);
        debugRenderSystem = new DebugRenderSystem(shapeRenderer, camera, WORLD);
        enemyFollowPlayerSystem = new EnemyFollowPlayerSystem();
        renderingSystem = new RenderingSystem(spriteBatch, camera, rayHandler);
        bulletSystem = new BulletSystem();

        engine.addSystem(followMouseSystem);
        engine.addSystem(playerControlSystem);
        engine.addSystem(enemyFollowPlayerSystem);
        engine.addSystem(rotationSystem);
        engine.addSystem(movementSystem);
        engine.addSystem(boundsCollisionSystem);
        engine.addSystem(bulletSystem);
        engine.addSystem(centerCameraSystem);
        engine.addSystem(renderingSystem);
        engine.addSystem(debugRenderSystem);
        debugRenderSystem.setProcessing(game.debug);
        //set level
        renderingSystem.setLevel(LevelManager.Level.EXAMPLE);

        engine.addEntity(EntityFactory.createCrosshair(0, 0, 48 * BloodHungerGame.UNIT_SCALE, 48 * BloodHungerGame.UNIT_SCALE));
        engine.addEntity(EntityFactory.createPlayer());
        keyUp(Input.Keys.NUM_1);
        //engine.addEntity(EntityFactory.createWall(0, 0, 2000*BloodHungerGame.UNIT_SCALE, 64*BloodHungerGame.UNIT_SCALE, null));
        int enemies = MathUtils.random(30) + 10;
        /*for (int i = 0; i < enemies; i++) {
            engine.addEntity(EntityFactory.createEnemey(100*BloodHungerGame.UNIT_SCALE, 100*BloodHungerGame.UNIT_SCALE,
                    64*BloodHungerGame.UNIT_SCALE, 64*BloodHungerGame.UNIT_SCALE, null));
        }*/
    }

    private void drawBackgroundGrid() {
        shapeRenderer.begin();
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.setColor(Color.WHITE);
        int gridSize = 100;
        for (int y = 0; y <= BloodHungerGame.UNIT_SCALE * gridSize; y += 64 * BloodHungerGame.UNIT_SCALE) {
            for (int x = 0; x <= BloodHungerGame.UNIT_SCALE * gridSize; x += 64 * BloodHungerGame.UNIT_SCALE) {
                shapeRenderer.line(x, 0, x, BloodHungerGame.UNIT_SCALE * gridSize);
            }
            shapeRenderer.line(0, y, BloodHungerGame.UNIT_SCALE * gridSize, y);
        }
        shapeRenderer.end();
    }

    @Override
    protected void update(float delta) {
        drawBackgroundGrid();
        engine.update(delta);
        doPhysicsStep(delta);
    }

    private void doPhysicsStep(float deltaTime) {
        // fixed time step
        // max frame time to avoid spiral of death (on slow devices)
        float frameTime = Math.min(deltaTime, 0.25f);
        accumulator += frameTime;
        while (accumulator >= TIME_STEP) {
            GameScreen.WORLD.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
            accumulator -= TIME_STEP;
        }
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        Entity player = engine.getEntitiesFor(Family.all(PlayerComponent.class).get()).first();
        PlayerComponent playerComponent = Mapper.playerComponent.get(player);
        if (playerComponent.getWeapon().canShoot()) {
            playerComponent.shoot();
            PositionComponent pc = Mapper.positionComponent.get(player);
            DimensionComponent dc = Mapper.dimensionComponent.get(player);
            RotationComponent rc = Mapper.rotationComponent.get(player);
            Entity bullet = EntityFactory.createBullet(0, 0, rc.lookingAngle);
            PositionComponent bulletPos = Mapper.positionComponent.get(bullet);
            DimensionComponent bulletDim = Mapper.dimensionComponent.get(bullet);
            //Okay, get inital position
            Vector2 bulletPosition = playerComponent.getWeapon().getInitialBulletPosition(pc, dc, rc);
            BULLET_OFFSET
                    .setZero()
                    .set(bulletDim.originX, 0)
                    .setAngle(rc.lookingAngle);
            bulletPos.set(bulletPosition.x - bulletDim.originX + BULLET_OFFSET.x,
                    bulletPosition.y - bulletDim.originY + BULLET_OFFSET.y);
            engine.addEntity(bullet);
            //Create shoot effect
            renderingSystem.onShoot(playerComponent, pc, dc, rc);
            //Update so we do not shoot through walls
            movementSystem.update(0);
            bulletSystem.update(0);
            return true;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        boolean keyPressed = false;
        Entity player;
        Vector2 offset;
        BoundsComponent bc;
        DimensionComponent dc;
        PositionComponent pc;
        RotationComponent rc;
        PlayerComponent playerComponent;
        ImmutableArray<Entity> flashLights;
        float yOffset;
        switch (keycode) {
            case Input.Keys.NUM_1:
                player = engine.getEntitiesFor(Family.all(PlayerComponent.class).get()).first();
                playerComponent = Mapper.playerComponent.get(player);
                flashLights = engine.getEntitiesFor(Family.all(FlashLightComponent.class).get());
                if (playerComponent.getWeapon().equals(PlayerComponent.Weapon.FLASHLIGHT)) {
                    if (flashLights.size() == 0) {
                        turnFlashLightOn();
                        keyPressed = true;
                        break;
                    } else {
                        turnFlashLightOff();
                    }
                }
                playerComponent.switchWeapon(PlayerComponent.Weapon.FLASHLIGHT);
                keyPressed = true;
                break;
            case Input.Keys.NUM_2:
                player = engine.getEntitiesFor(Family.all(PlayerComponent.class).get()).first();
                Mapper.playerComponent.get(player).switchWeapon(PlayerComponent.Weapon.HANDGUN);
                keyPressed = true;
                break;
            case Input.Keys.F:
                player = engine.getEntitiesFor(Family.all(PlayerComponent.class).get()).first();
                Mapper.playerComponent.get(player).meeleAttack();
                keyPressed = true;
                break;
            case Input.Keys.R:
                player = engine.getEntitiesFor(Family.all(PlayerComponent.class).get()).first();
                Mapper.playerComponent.get(player).reload();
                keyPressed = true;
                break;
        }
        return keyPressed;
    }

    /**
     * adds a new flashlight to the scene
     */
    private void turnFlashLightOn() {
        Entity flashLightEntity = EntityFactory.createFlashLight(0, 0);
        engine.addEntity(flashLightEntity);
        LightComponent lc = Mapper.flashLightComponent.get(flashLightEntity);
        lc.setLightReference(new ConeLight(rayHandler, 4, null, 10 * 64 * BloodHungerGame.UNIT_SCALE, 0, 0, 0, 45));
    }

    /**
     * removes all cone flashlights on the field
     */
    private void turnFlashLightOff() {
        ImmutableArray<Entity> flashLights = engine.getEntitiesFor(Family.all(FlashLightComponent.class).get());
        flashLights.forEach(fl -> {
            engine.removeEntity(fl);
            Mapper.flashLightComponent.get(fl).getLightReference().remove();
        });
    }

    @Override
    public void entityAdded(Entity entity) {
    }

    @Override
    public void entityRemoved(Entity entity) {
        entity.getComponents().forEach(component -> {
            if (component instanceof Disposable) {
                ((Disposable) component).dispose();
            }
        });
    }

    @Override
    public void dispose() {
        engine.removeAllEntities();
        ImmutableArray<EntitySystem> systems = engine.getSystems();
        systems.forEach(entitySystem -> engine.removeSystem(entitySystem));
        spriteBatch.dispose();
        rayHandler.dispose();
        shapeRenderer.dispose();

    }
}
