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
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Logger;
import de.kswmd.bloodhunger.components.LevelExitComponent;
import de.kswmd.bloodhunger.components.PlayerComponent;
import de.kswmd.bloodhunger.screens.GameScreen;
import de.kswmd.bloodhunger.screens.InitialLoadingScreen;
import de.kswmd.bloodhunger.screens.IntroScreen;
import de.kswmd.bloodhunger.screens.LoadNextLevelScreen;
import de.kswmd.bloodhunger.systems.*;
import de.kswmd.bloodhunger.ui.inventory.Inventory;
import de.kswmd.bloodhunger.utils.LevelManager;

public class BloodHungerGame extends Game implements EntityListener {

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
    public RotationSystem rotationSystem;
    public MovementSystem movementSystem;
    public BoundsCollisionSystem boundsCollisionSystem;
    public CenterCameraSystem centerCameraSystem;
    public DebugRenderSystem debugRenderSystem;
    public EnemyFollowPlayerSystem enemyFollowPlayerSystem;
    public RenderingSystem renderingSystem;
    public BulletSystem bulletSystem;

    public Engine engine;
    public ShapeRenderer shapeRenderer;
    public SpriteBatch spriteBatch;
    public RayHandler rayHandler;
    public OrthographicCamera camera;
    public PlayerComponent playerComponent;
    public Skin uiSkin;


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

    private void init(){
        camera = new OrthographicCamera(Gdx.graphics.getWidth()*BloodHungerGame.UNIT_SCALE,Gdx.graphics.getHeight()*BloodHungerGame.UNIT_SCALE);
        spriteBatch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);
        rayHandler = new RayHandler(BloodHungerGame.WORLD);
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
        debugRenderSystem.setProcessing(debug);
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

    /*
     * convenient method for switching screens with level
     * @param level The level
     */
    public void setLevel(LevelExitComponent levelExitComponent){
        rayHandler.removeAll();
        engine.removeAllEntities();
        LevelManager.Level level = levelExitComponent.level;
        if(level != LevelManager.getInstance().level){
            BloodHungerGame.ASSET_MANAGER.unload(LevelManager.getInstance().level.getMap());
        }
        LevelManager.getInstance().setLevel(level);
        BloodHungerGame.SCREEN_LOAD_NEXT_LEVEL.setNextScreen(levelExitComponent.nextScreen);
        setScreen(BloodHungerGame.SCREEN_LOAD_NEXT_LEVEL);
    }

    public void setAmbientLight(float r, float g, float b, float a){
        renderingSystem.setAmbientLight(r,g,b,a);
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
