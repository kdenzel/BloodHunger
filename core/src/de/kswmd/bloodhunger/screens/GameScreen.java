package de.kswmd.bloodhunger.screens;

import box2dLight.ConeLight;
import box2dLight.RayHandler;
import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import de.kswmd.bloodhunger.Assets;
import de.kswmd.bloodhunger.BloodHungerGame;
import de.kswmd.bloodhunger.components.*;
import de.kswmd.bloodhunger.factories.EntityFactory;
import de.kswmd.bloodhunger.systems.*;
import de.kswmd.bloodhunger.ui.inventory.Inventory;
import de.kswmd.bloodhunger.ui.inventory.InventoryListener;
import de.kswmd.bloodhunger.ui.inventory.InventorySlot;
import de.kswmd.bloodhunger.utils.LevelManager;
import de.kswmd.bloodhunger.utils.Mapper;

public class GameScreen extends BaseScreen implements EntityListener, InventoryListener {

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

    private Inventory inventory;
    private Window inventoryWindow;
    private Label fpsCounterLabel;

    private float accumulator = 0;

    public GameScreen(BloodHungerGame game) {
        super(game);
    }

    @Override
    protected void initialize() {
        createHUD();
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
        renderingSystem.setLevel(LevelManager.Level.EXAMPLE, inventory);

        engine.addEntity(EntityFactory.createCrosshair(0, 0, 48 * BloodHungerGame.UNIT_SCALE, 48 * BloodHungerGame.UNIT_SCALE));
        engine.addEntity(EntityFactory.createPlayer(2, 2, inventory));
        for (int i = 0; i < 9; i++)
            engine.addEntity(EntityFactory.createItem(5 + i, 5, 32 * BloodHungerGame.UNIT_SCALE, 32 * BloodHungerGame.UNIT_SCALE, ItemComponent.ItemType.FLASHLIGHT));
        for (int i = 0; i < 9; i++)
            engine.addEntity(EntityFactory.createItem(5 + i, 4, 32 * BloodHungerGame.UNIT_SCALE, 32 * BloodHungerGame.UNIT_SCALE, ItemComponent.ItemType.HANDGUN));
        keyUp(Input.Keys.NUM_1);
        //engine.addEntity(EntityFactory.createWall(0, 0, 2000*BloodHungerGame.UNIT_SCALE, 64*BloodHungerGame.UNIT_SCALE, null));
        int enemies = MathUtils.random(30) + 10;
        /*for (int i = 0; i < enemies; i++) {
            engine.addEntity(EntityFactory.createEnemey(100*BloodHungerGame.UNIT_SCALE, 100*BloodHungerGame.UNIT_SCALE,
                    64*BloodHungerGame.UNIT_SCALE, 64*BloodHungerGame.UNIT_SCALE, null));
        }*/
    }

    private void createHUD() {
        this.inventory = Inventory.create();
        TextureAtlas uiTextureAtlas = BloodHungerGame.ASSET_MANAGER.get(Assets.TEXTURE_ATLAS_UI);
        Skin skin = new Skin(Gdx.files.internal("ui/uiskin.json"), uiTextureAtlas);
        Table table = new Table(skin);
        table.setFillParent(true);
        fpsCounterLabel = new Label("aha",skin);
        table.add().colspan(3).expand().row();
        Table inventoryTable = new Table(skin);
        inventoryTable.setBackground("inventory_list_box");
        table.add().pad(20);
        inventoryWindow = new Window("Inventory", skin);
        inventoryWindow.setVisible(false);
        inventoryWindow.setMovable(false);
        inventoryWindow.setKeepWithinStage(true);
        float inventorySlotSizeWidth = ((float) Gdx.graphics.getWidth() * 0.1f);
        float inventorySlotSizeHeight = ((float) Gdx.graphics.getHeight() * 0.1f);
        DragAndDrop dnd = new DragAndDrop();
        for (int i = 0; i < 33; i++) {
            InventorySlot inventorySlot = new InventorySlot(skin, "inventory_box");
            inventorySlot.addListener(new InputListener() {
                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    ((InventorySlot) event.getListenerActor()).hoverIn();
                }

                @Override
                public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                    ((InventorySlot) event.getListenerActor()).hoverOut();
                }
            });
            this.inventory.addInventorySlot(inventorySlot);
            if (i < 8) {
                inventoryTable.add(inventorySlot).size(inventorySlotSizeWidth, inventorySlotSizeHeight).expand().fill().align(Align.center).pad(1);
                inventoryTable.pack();
            } else {
                if ((i - 8) % 5 == 0 && (i-8)>0) {
                    inventoryWindow.row();
                }
                inventoryWindow.add(inventorySlot).size(inventorySlotSizeWidth, inventorySlotSizeHeight).expand().fill().align(Align.center).pad(1);
                inventoryWindow.pack();
            }
            inventorySlot.getChild(0).setSize(inventorySlot.getWidth(), inventorySlot.getHeight());
            dnd.addSource(new DragAndDrop.Source(inventorySlot) {


                @Override
                public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {
                    DragAndDrop.Payload payload = null;
                    InventorySlot slot = (InventorySlot) getActor();
                    if (slot.hasItem()) {
                        payload = new DragAndDrop.Payload();
                        payload.setObject(slot.getItemComponent());
                        payload.setDragActor(slot.getItemImage());
                        slot.removeItem();
                    }
                    Gdx.app.debug("Drag", "Start");
                    return payload;
                }

                @Override
                public void drag(InputEvent event, float x, float y, int pointer) {
                    super.drag(event, x, y, pointer);
                }

                @Override
                public void dragStop(InputEvent event, float x, float y, int pointer, DragAndDrop.Payload payload, DragAndDrop.Target target) {
                    super.dragStop(event, x, y, pointer, payload, target);
                    InventorySlot sourceSlot = (InventorySlot) getActor();
                    if (target == null) {
                        sourceSlot.setItem((ItemComponent) payload.getObject());
                    } else {
                        InventorySlot targetSlot = (InventorySlot) target.getActor();
                        if(targetSlot.hasItem()){
                            sourceSlot.setItem(targetSlot.getItemComponent());
                        }
                        targetSlot.setItem((ItemComponent) payload.getObject());
                    }
                    Gdx.app.debug("Drag", "Stop");
                }
            });
            dnd.addTarget(new DragAndDrop.Target(inventorySlot) {
                @Override
                public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                    return true;
                }

                @Override
                public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                    Gdx.app.debug("Drop", "DROP IT");
                }
            });
        }
        inventoryWindow.pack();
        table.add(inventoryTable).fill();
        table.add(fpsCounterLabel).size(20);
        table.pack();
        uiStage.addActor(inventoryWindow);
        uiStage.addActor(table);
        inventoryWindow.setPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2,Align.center);
        inventory.addListener(this);
        uiStage.setDebugAll(game.debug);
    }

    @Override
    public void onItemAdded(InventorySlot slot, ItemComponent itemComponent) {
        if (slot.isSelected()) {
            Entity player = engine.getEntitiesFor(Family.all(PlayerComponent.class).get()).first();
            PlayerComponent playerComponent = Mapper.playerComponent.get(player);
            playerComponent.switchTool(itemComponent.itemType.tool);
        }
    }

    @Override
    public void onItemRemoved(InventorySlot slot, ItemComponent itemComponent) {
        if(slot.isSelected()){
            Entity player = engine.getEntitiesFor(Family.all(PlayerComponent.class).get()).first();
            PlayerComponent playerComponent = Mapper.playerComponent.get(player);
            playerComponent.switchTool(PlayerComponent.Tool.NONE);
            turnFlashLightOff();
        }
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
        fpsCounterLabel.setText(Gdx.graphics.getFramesPerSecond());
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
        if (inventoryWindow.isVisible()) {
            return true;
        }
        Entity player = engine.getEntitiesFor(Family.all(PlayerComponent.class).get()).first();
        PlayerComponent playerComponent = Mapper.playerComponent.get(player);
        if (playerComponent.getTool().canShoot()) {
            playerComponent.shoot();
            PositionComponent pc = Mapper.positionComponent.get(player);
            DimensionComponent dc = Mapper.dimensionComponent.get(player);
            RotationComponent rc = Mapper.rotationComponent.get(player);
            Entity bullet = EntityFactory.createBullet(0, 0, rc.lookingAngle);
            PositionComponent bulletPos = Mapper.positionComponent.get(bullet);
            DimensionComponent bulletDim = Mapper.dimensionComponent.get(bullet);
            //Okay, get inital position
            Vector2 bulletPosition = playerComponent.getTool().getTransformedToolPositionWithOffset(pc, dc, rc);
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
        } else if (playerComponent.getTool().equals(PlayerComponent.Tool.FLASHLIGHT)) {
            ImmutableArray<Entity> flashLights = engine.getEntitiesFor(Family.all(FlashLightComponent.class).get());
            if (flashLights.size() == 0) {
                turnFlashLightOn();
            } else {
                turnFlashLightOff();
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        boolean keyPressed = false;
        Entity player;
        PlayerComponent playerComponent = null;
        switch (keycode) {
            case Input.Keys.NUM_1:
                player = engine.getEntitiesFor(Family.all(PlayerComponent.class).get()).first();
                inventory.setSelected(0);
                playerComponent = Mapper.playerComponent.get(player);
                keyPressed = true;
                break;
            case Input.Keys.NUM_2:
                player = engine.getEntitiesFor(Family.all(PlayerComponent.class).get()).first();
                inventory.setSelected(1);
                playerComponent = Mapper.playerComponent.get(player);
                keyPressed = true;
                break;
            case Input.Keys.NUM_3:
                player = engine.getEntitiesFor(Family.all(PlayerComponent.class).get()).first();
                inventory.setSelected(2);
                playerComponent = Mapper.playerComponent.get(player);
                keyPressed = true;
                break;
            case Input.Keys.NUM_4:
                player = engine.getEntitiesFor(Family.all(PlayerComponent.class).get()).first();
                inventory.setSelected(3);
                playerComponent = Mapper.playerComponent.get(player);
                keyPressed = true;
                break;
            case Input.Keys.NUM_5:
                player = engine.getEntitiesFor(Family.all(PlayerComponent.class).get()).first();
                inventory.setSelected(4);
                playerComponent = Mapper.playerComponent.get(player);
                keyPressed = true;
                break;
            case Input.Keys.NUM_6:
                player = engine.getEntitiesFor(Family.all(PlayerComponent.class).get()).first();
                inventory.setSelected(5);
                playerComponent = Mapper.playerComponent.get(player);
                keyPressed = true;
                break;
            case Input.Keys.NUM_7:
                player = engine.getEntitiesFor(Family.all(PlayerComponent.class).get()).first();
                inventory.setSelected(6);
                playerComponent = Mapper.playerComponent.get(player);
                keyPressed = true;
                break;
            case Input.Keys.NUM_8:
                player = engine.getEntitiesFor(Family.all(PlayerComponent.class).get()).first();
                inventory.setSelected(7);
                playerComponent = Mapper.playerComponent.get(player);
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
            case Input.Keys.I:
                Gdx.input.setCursorCatched(inventoryWindow.isVisible() && !game.debug);
                followMouseSystem.setProcessing(inventoryWindow.isVisible());
                playerControlSystem.setProcessing(inventoryWindow.isVisible());
                inventoryWindow.setVisible(!inventoryWindow.isVisible());
                keyPressed = true;
                break;
        }
        if (playerComponent != null) {
            InventorySlot slot = inventory.getSelectedSlot();
            if (!slot.isEmpty()) {
                playerComponent.switchTool(slot.getItemComponent().itemType.tool);
            } else {
                playerComponent.switchTool(PlayerComponent.Tool.NONE);
                turnFlashLightOff();
            }
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
    public void resize(int width, int height) {
        super.resize(width, height);
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
        super.dispose();
        engine.removeAllEntities();
        ImmutableArray<EntitySystem> systems = engine.getSystems();
        systems.forEach(entitySystem -> engine.removeSystem(entitySystem));
        spriteBatch.dispose();
        rayHandler.dispose();
        shapeRenderer.dispose();
    }
}
