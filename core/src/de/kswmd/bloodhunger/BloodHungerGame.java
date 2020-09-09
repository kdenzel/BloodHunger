package de.kswmd.bloodhunger;

import box2dLight.RayHandler;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.*;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Logger;
import de.kswmd.bloodhunger.components.LevelExitComponent;
import de.kswmd.bloodhunger.components.PlayerComponent;
import de.kswmd.bloodhunger.factories.EntityFactory;
import de.kswmd.bloodhunger.screens.GameScreen;
import de.kswmd.bloodhunger.screens.InitialLoadingScreen;
import de.kswmd.bloodhunger.screens.IntroScreen;
import de.kswmd.bloodhunger.screens.LoadNextLevelScreen;
import de.kswmd.bloodhunger.systems.*;
import de.kswmd.bloodhunger.ui.inventory.Inventory;
import de.kswmd.bloodhunger.utils.LevelManager;
import de.kswmd.bloodhunger.utils.Mapper;

public final class BloodHungerGame extends Game implements EntityListener {

    private static final String TAG = BloodHungerGame.class.getSimpleName();

    public static Screen SCREEN_GAME;
    public static Screen SCREEN_INTRO;
    public static LoadNextLevelScreen SCREEN_LOAD_NEXT_LEVEL;

    public static final int UNIT = 64;
    public static final float UNIT_SCALE = 1f / UNIT;

    public static final AssetManager ASSET_MANAGER = new AssetManager();
    public final boolean debug;

    public static final World WORLD = new World(new Vector2(), true);

    public FollowMouseSystem followMouseSystem;
    public PlayerControlSystem playerControlSystem;
    public ZombieSystem zombieSystem;
    public RotationSystem rotationSystem;
    public MovementSystem movementSystem;
    public BoundsCollisionSystem boundsCollisionSystem;
    public BulletSystem bulletSystem;
    public CenterCameraSystem centerCameraSystem;
    public SunCycleSystem sunCycleSystem;
    public UpdateShadersSystem updateShadersSystem;
    public DebugRenderSystem debugRenderSystem;
    public RenderingSystem renderingSystem;

    public Engine engine;
    public ShapeRenderer shapeRenderer;
    public SpriteBatch spriteBatch;
    public RayHandler rayHandler;
    public OrthographicCamera camera;
    public PlayerComponent playerComponent;
    public Skin uiSkin;

    public ShaderProgram shaderProgram;


    public BloodHungerGame(boolean debug) {
        this.debug = debug;
    }

    @Override
    public void create() {
        Box2D.init();
        if (debug) {
            Gdx.app.setLogLevel(Application.LOG_DEBUG);
            ASSET_MANAGER.getLogger().setLevel(Logger.DEBUG);
        }
        InputMultiplexer im = new InputMultiplexer();
        Gdx.input.setInputProcessor(im);
        BloodHungerGame.SCREEN_GAME = new GameScreen(this);
        BloodHungerGame.SCREEN_LOAD_NEXT_LEVEL = new LoadNextLevelScreen(this);
        BloodHungerGame.SCREEN_INTRO = new IntroScreen(this);
        init();
        setScreen(new InitialLoadingScreen(this));
    }

    private void init() {
        camera = new OrthographicCamera(Gdx.graphics.getWidth() * BloodHungerGame.UNIT_SCALE, Gdx.graphics.getHeight() * BloodHungerGame.UNIT_SCALE);

        shaderProgram = new ShaderProgram(Gdx.files.internal("shaders/BasicShadowVertex.glsl"),
                Gdx.files.internal("shaders/BasicShadowFragment.glsl"));
        if(!shaderProgram.isCompiled()){
            Gdx.app.debug(TAG,shaderProgram.getLog());
            throw new RuntimeException("Couldn't  compile shaders...");
        }

        spriteBatch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);
        RayHandler.useDiffuseLight(true);
        RayHandler.setGammaCorrection(true);
        rayHandler = new RayHandler(BloodHungerGame.WORLD);
        rayHandler.setShadows(true);
        rayHandler.setPseudo3dLight(true);
        engine = new Engine();
        engine.addEntityListener(this);
    }

    public void initAfterLoading() {
        playerComponent = new PlayerComponent(Inventory.create());
        TextureAtlas uiTextureAtlas = BloodHungerGame.ASSET_MANAGER.get(Assets.TEXTURE_ATLAS_UI);
        uiSkin = new Skin(Gdx.files.internal("ui/uiskin.json"), uiTextureAtlas);
        followMouseSystem = new FollowMouseSystem(camera);
        playerControlSystem = new PlayerControlSystem();
        rotationSystem = new RotationSystem();
        movementSystem = new MovementSystem();
        boundsCollisionSystem = new BoundsCollisionSystem(this);
        centerCameraSystem = new CenterCameraSystem(camera);
        debugRenderSystem = new DebugRenderSystem(shapeRenderer, camera, WORLD);
        zombieSystem = new ZombieSystem();
        updateShadersSystem = new UpdateShadersSystem(camera,shaderProgram);
        renderingSystem = new RenderingSystem(camera,spriteBatch, rayHandler, shaderProgram);
        bulletSystem = new BulletSystem();
        sunCycleSystem = new SunCycleSystem();

        //The executive order is the same as added
        //Control
        engine.addSystem(followMouseSystem);
        engine.addSystem(playerControlSystem);
        //update
        engine.addSystem(zombieSystem);
        engine.addSystem(rotationSystem);
        engine.addSystem(movementSystem);
        engine.addSystem(boundsCollisionSystem);
        engine.addSystem(bulletSystem);
        engine.addSystem(centerCameraSystem);
        engine.addSystem(sunCycleSystem);
        engine.addSystem(updateShadersSystem);
        //render
        engine.addSystem(renderingSystem);
        engine.addSystem(debugRenderSystem);
        //Debugsystems
        debugRenderSystem.setProcessing(debug);
        sunCycleSystem.setProcessing(debug);
    }

    public void setUpLightEnvironment() {
        engine.addEntity(EntityFactory.createPlayerLight(0, 0, rayHandler));
        engine.addEntity(EntityFactory.createFlashLight(0,0,rayHandler));
    }

    public void setDayLightOn() {
        rayHandler.setShadows(false);
    }

    public void setDayLightOff() {
        rayHandler.setShadows(true);
    }

    @Override
    public void entityAdded(Entity entity) {
        if(Mapper.boundsComponent.has(entity)){
            Mapper.boundsComponent.get(entity).addListener(boundsCollisionSystem);
        }
    }

    @Override
    public void entityRemoved(Entity entity) {
        entity.getComponents().forEach(component -> {
            if (component instanceof Disposable) {
                ((Disposable) component).dispose();
            }
        });
    }

    /*
     * convenient method for switching screens with level
     * @param level The level
     */
    public void setLevel(LevelExitComponent levelExitComponent) {
        rayHandler.removeAll();
        engine.removeAllEntities();
        LevelManager.Level level = levelExitComponent.level;
        if (level != LevelManager.getInstance().level) {
            if (BloodHungerGame.ASSET_MANAGER.isLoaded(LevelManager.getInstance().level.getMap()))
                BloodHungerGame.ASSET_MANAGER.unload(LevelManager.getInstance().level.getMap());
        }
        LevelManager.getInstance().setLevel(level);
        BloodHungerGame.SCREEN_LOAD_NEXT_LEVEL.setNextScreen(levelExitComponent.nextScreen);
        setScreen(BloodHungerGame.SCREEN_LOAD_NEXT_LEVEL);
    }

    public void setAmbientLight(float r, float g, float b, float a) {
        renderingSystem.setAmbientLight(r, g, b, a);
    }

    /**
     * calculates given float multiplied by world units
     * @param xy the value you want to convert multiplied by worldUnits, could be anything like x,y,width or height
     * @return the new calculated value
     */
    public static float worldUnits(float xy) {
        return xy * BloodHungerGame.UNIT * BloodHungerGame.UNIT_SCALE;
    }

    /**
     * calculates given float to world units
     * @param xy the value you want to convert to worldUnits, could be anything like x,y,width or height
     * @return the new calculated value
     */
    public static float toWorldUnits(float xy) {
        return xy * BloodHungerGame.UNIT_SCALE;
    }

    @Override
    public void dispose() {
        super.dispose();
        uiSkin.dispose();
        engine.removeAllEntities();
        ImmutableArray<EntitySystem> systems = engine.getSystems();
        systems.forEach(entitySystem -> engine.removeSystem(entitySystem));
        SCREEN_GAME.dispose();
        spriteBatch.dispose();
        rayHandler.dispose();
        shapeRenderer.dispose();
        ASSET_MANAGER.dispose();

    }
}
