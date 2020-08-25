package de.kswmd.bloodhunger.screens;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.Align;
import de.kswmd.bloodhunger.BloodHungerGame;
import de.kswmd.bloodhunger.components.*;
import de.kswmd.bloodhunger.factories.EntityFactory;
import de.kswmd.bloodhunger.ui.inventory.Inventory;
import de.kswmd.bloodhunger.ui.inventory.InventoryListener;
import de.kswmd.bloodhunger.ui.inventory.InventorySlot;
import de.kswmd.bloodhunger.utils.LevelManager;
import de.kswmd.bloodhunger.utils.Mapper;

public class GameScreen extends BaseScreen implements InventoryListener {

    private static final String TAG = GameScreen.class.getSimpleName();

    private static final float TIME_STEP = 1 / 60f;
    private static final int VELOCITY_ITERATIONS = 6;
    private static final int POSITION_ITERATIONS = 2;

    private static final Vector2 BULLET_OFFSET = new Vector2();

    private Window inventoryWindow;
    private Label fpsCounterLabel;

    private float accumulator = 0;

    public GameScreen(BloodHungerGame game) {
        super(game);
    }

    @Override
    protected void initialize() {
        Gdx.input.setCursorCatched(!game.debug);
        createHUD();
        Engine engine = game.engine;
        engine.addEntity(EntityFactory.createCrosshair(0, 0, 48 * BloodHungerGame.UNIT_SCALE, 48 * BloodHungerGame.UNIT_SCALE));
        engine.addEntity(EntityFactory.createPlayer(2, 2, game.playerComponent));
        engine.addEntity(EntityFactory.createPlayerLight(0, 0, game.rayHandler));
        for (int i = 0; i < 9; i++)
            engine.addEntity(EntityFactory.createItem(5 + i, 5, 32 * BloodHungerGame.UNIT_SCALE, 32 * BloodHungerGame.UNIT_SCALE, ItemComponent.ItemType.FLASHLIGHT));
        for (int i = 0; i < 9; i++)
            engine.addEntity(EntityFactory.createItem(5 + i, 4, 32 * BloodHungerGame.UNIT_SCALE, 32 * BloodHungerGame.UNIT_SCALE, ItemComponent.ItemType.HANDGUN));
        //engine.addEntity(EntityFactory.createWall(0, 0, 2000*BloodHungerGame.UNIT_SCALE, 64*BloodHungerGame.UNIT_SCALE, null));
        //int enemies = MathUtils.random(30) + 10;
        engine.addEntity(EntityFactory.createLevelExit(-0.5f, -0.5f, 1f, 1f, BloodHungerGame.SCREEN_INTRO, LevelManager.Level.EXAMPLE));
        game.playerComponent.inventory.addListener(this);
        game.setAmbientLight(0, 0, 0, 0.01f);
    }

    private void createHUD() {
        Skin skin = game.uiSkin;
        Table table = new Table(skin);
        table.setFillParent(true);
        fpsCounterLabel = new Label("aha", skin);
        table.add(fpsCounterLabel).left();
        table.row().expandY();
        table.add().colspan(3).expandY();
        table.row();
        Table inventoryTable = new Table(skin);
        inventoryTable.setBackground("inventory_list_box");
        table.add().expandX();
        inventoryWindow = new Window("Inventory", skin);
        inventoryWindow.setVisible(false);
        inventoryWindow.setMovable(false);
        inventoryWindow.setKeepWithinStage(true);
        float inventorySlotSizeWidth = ((float) Gdx.graphics.getWidth() * 0.05f);
        float inventorySlotSizeHeight = ((float) Gdx.graphics.getHeight() * 0.08f);

        for (int i = 0; i < game.playerComponent.inventory.size(); i++) {
            InventorySlot inventorySlot = game.playerComponent.inventory.get(i);
            if (i < 8) {
                inventoryTable.add(inventorySlot).size(inventorySlotSizeWidth, inventorySlotSizeHeight).expand().fill().align(Align.center).pad(Gdx.graphics.getWidth() * 0.001f);
                inventoryTable.pack();
            } else {
                if ((i - Inventory.SLOTS) % 5 == 0 && (i - Inventory.SLOTS) > 0) {
                    inventoryWindow.row();
                }
                inventoryWindow.add(inventorySlot).size(inventorySlotSizeWidth, inventorySlotSizeHeight).expand().fill().align(Align.center).pad(Gdx.graphics.getWidth() * 0.001f);
                inventoryWindow.pack();
            }
            inventorySlot.getChild(0).setSize(inventorySlot.getWidth(), inventorySlot.getHeight());
        }
        inventoryWindow.pack();
        table.add(inventoryTable).center();
        table.add().expandX();
        table.pack();
        uiStage.addActor(inventoryWindow);
        uiStage.addActor(table);
        inventoryWindow.setPosition(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f, Align.center);
        //uiStage.setDebugAll(game.debug);
    }

    @Override
    public void onItemAdded(InventorySlot slot, ItemComponent itemComponent) {
        if (slot.isSelected()) {
            Entity player = game.engine.getEntitiesFor(Family.all(PlayerComponent.class).get()).first();
            PlayerComponent playerComponent = Mapper.playerComponent.get(player);
            playerComponent.switchTool(itemComponent.itemType.tool);
        }
    }

    @Override
    public void onItemRemoved(InventorySlot slot, ItemComponent itemComponent) {
        if (slot.isSelected()) {
            Entity player = game.engine.getEntitiesFor(Family.all(PlayerComponent.class).get()).first();
            PlayerComponent playerComponent = Mapper.playerComponent.get(player);
            playerComponent.switchTool(PlayerComponent.Tool.NONE);
            turnFlashLightOff();
        }
    }

    private void drawBackgroundGrid() {
        game.shapeRenderer.begin();
        game.shapeRenderer.setProjectionMatrix(game.camera.combined);
        game.shapeRenderer.setColor(Color.WHITE);
        int gridSize = 100;
        for (int y = 0; y <= BloodHungerGame.UNIT_SCALE * gridSize; y += BloodHungerGame.UNIT * BloodHungerGame.UNIT_SCALE) {
            for (int x = 0; x <= BloodHungerGame.UNIT_SCALE * gridSize; x += BloodHungerGame.UNIT * BloodHungerGame.UNIT_SCALE) {
                game.shapeRenderer.line(x, 0, x, BloodHungerGame.UNIT_SCALE * gridSize);
            }
            game.shapeRenderer.line(0, y, BloodHungerGame.UNIT_SCALE * gridSize, y);
        }
        game.shapeRenderer.end();
    }

    @Override
    protected void update(float delta) {
        fpsCounterLabel.setText(Gdx.graphics.getFramesPerSecond());
        drawBackgroundGrid();
        game.engine.update(delta);
        doPhysicsStep(delta);
    }

    private void doPhysicsStep(float deltaTime) {
        // fixed time step
        // max frame time to avoid spiral of death (on slow devices)
        float frameTime = Math.min(deltaTime, 0.25f);
        accumulator += frameTime;
        while (accumulator >= TIME_STEP) {
            BloodHungerGame.WORLD.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
            accumulator -= TIME_STEP;
        }
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (inventoryWindow.isVisible()) {
            return false;
        }
        Entity player = game.engine.getEntitiesFor(Family.all(PlayerComponent.class).get()).first();
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
            game.engine.addEntity(bullet);
            //Create shoot effect
            game.renderingSystem.onShoot(playerComponent, pc, dc, rc);
            //Update so we do not shoot through walls
            game.movementSystem.update(0);
            game.bulletSystem.update(0);
            return true;
        } else if (playerComponent.getTool().equals(PlayerComponent.Tool.FLASHLIGHT)) {
            ImmutableArray<Entity> flashLights = game.engine.getEntitiesFor(Family.all(FlashLightComponent.class).get());
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
                player = game.engine.getEntitiesFor(Family.all(PlayerComponent.class).get()).first();
                game.playerComponent.inventory.setSelected(0);
                playerComponent = Mapper.playerComponent.get(player);
                keyPressed = true;
                break;
            case Input.Keys.NUM_2:
                player = game.engine.getEntitiesFor(Family.all(PlayerComponent.class).get()).first();
                game.playerComponent.inventory.setSelected(1);
                playerComponent = Mapper.playerComponent.get(player);
                keyPressed = true;
                break;
            case Input.Keys.NUM_3:
                player = game.engine.getEntitiesFor(Family.all(PlayerComponent.class).get()).first();
                game.playerComponent.inventory.setSelected(2);
                playerComponent = Mapper.playerComponent.get(player);
                keyPressed = true;
                break;
            case Input.Keys.NUM_4:
                player = game.engine.getEntitiesFor(Family.all(PlayerComponent.class).get()).first();
                game.playerComponent.inventory.setSelected(3);
                playerComponent = Mapper.playerComponent.get(player);
                keyPressed = true;
                break;
            case Input.Keys.NUM_5:
                player = game.engine.getEntitiesFor(Family.all(PlayerComponent.class).get()).first();
                game.playerComponent.inventory.setSelected(4);
                playerComponent = Mapper.playerComponent.get(player);
                keyPressed = true;
                break;
            case Input.Keys.NUM_6:
                player = game.engine.getEntitiesFor(Family.all(PlayerComponent.class).get()).first();
                game.playerComponent.inventory.setSelected(5);
                playerComponent = Mapper.playerComponent.get(player);
                keyPressed = true;
                break;
            case Input.Keys.NUM_7:
                player = game.engine.getEntitiesFor(Family.all(PlayerComponent.class).get()).first();
                game.playerComponent.inventory.setSelected(6);
                playerComponent = Mapper.playerComponent.get(player);
                keyPressed = true;
                break;
            case Input.Keys.NUM_8:
                player = game.engine.getEntitiesFor(Family.all(PlayerComponent.class).get()).first();
                game.playerComponent.inventory.setSelected(7);
                playerComponent = Mapper.playerComponent.get(player);
                keyPressed = true;
                break;
            case Input.Keys.F:
                player = game.engine.getEntitiesFor(Family.all(PlayerComponent.class).get()).first();
                Mapper.playerComponent.get(player).meeleAttack();
                keyPressed = true;
                break;
            case Input.Keys.R:
                player = game.engine.getEntitiesFor(Family.all(PlayerComponent.class).get()).first();
                Mapper.playerComponent.get(player).reload();
                keyPressed = true;
                break;
            case Input.Keys.I:
                if (game.playerComponent.inventory.hasBackpack()) {
                    Gdx.input.setCursorCatched(inventoryWindow.isVisible() && !game.debug);
                    game.followMouseSystem.setProcessing(inventoryWindow.isVisible());
                    game.playerControlSystem.setProcessing(inventoryWindow.isVisible());
                    inventoryWindow.setVisible(!inventoryWindow.isVisible());
                    Gdx.app.debug(TAG, "Inventory Window visible=" + inventoryWindow.isVisible());
                    keyPressed = true;
                }
                break;
        }
        if (playerComponent != null) {
            InventorySlot slot = game.playerComponent.inventory.getSelectedSlot();
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
        Entity flashLightEntity = EntityFactory.createFlashLight(0, 0, game.rayHandler);
        game.engine.addEntity(flashLightEntity);

    }

    /**
     * removes all cone flashlights on the field
     */
    private void turnFlashLightOff() {
        ImmutableArray<Entity> flashLights = game.engine.getEntitiesFor(Family.all(FlashLightComponent.class).get());
        flashLights.forEach(fl -> {
            game.engine.removeEntity(fl);
            Mapper.flashLightComponent.get(fl).getLightReference().remove();
        });
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
    }


    @Override
    public void hide() {
        super.hide();
        game.playerComponent.inventory.removeListener(this);
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
