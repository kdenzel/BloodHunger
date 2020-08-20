package de.kswmd.bloodhunger.screens;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import de.kswmd.bloodhunger.BloodHungerGame;
import de.kswmd.bloodhunger.components.*;
import de.kswmd.bloodhunger.factories.EntityFactory;
import de.kswmd.bloodhunger.systems.*;
import de.kswmd.bloodhunger.utils.LevelManager;
import de.kswmd.bloodhunger.utils.Mapper;

public class GameScreen extends BaseScreen {

    private static final Vector2 BULLET_OFFSET = new Vector2();

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


    public GameScreen(BloodHungerGame game) {
        super(game);
    }

    @Override
    protected void initialize() {
        Gdx.input.setCursorCatched(!game.debug);
        spriteBatch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);
        engine = new Engine();


        followMouseSystem = new FollowMouseSystem(camera);
        playerControlSystem = new PlayerControlSystem();
        rotationSystem = new RotationSystem();
        movementSystem = new MovementSystem();
        boundsCollisionSystem = new BoundsCollisionSystem();
        centerCameraSystem = new CenterCameraSystem(camera);
        debugRenderSystem = new DebugRenderSystem(shapeRenderer, camera);
        enemyFollowPlayerSystem = new EnemyFollowPlayerSystem();
        renderingSystem = new RenderingSystem(spriteBatch, camera);
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

        engine.addEntity(EntityFactory.createCrosshair(0,0,48*BloodHungerGame.UNIT_SCALE,48*BloodHungerGame.UNIT_SCALE));
        engine.addEntity(EntityFactory.createPlayer());
        keyUp(Input.Keys.NUM_1);
        //engine.addEntity(EntityFactory.createWall(0, 0, 2000*BloodHungerGame.UNIT_SCALE, 64*BloodHungerGame.UNIT_SCALE, null));
        int enemies = MathUtils.random(30) + 10;
        /*for (int i = 0; i < enemies; i++) {
            engine.addEntity(EntityFactory.createEnemey(100*BloodHungerGame.UNIT_SCALE, 100*BloodHungerGame.UNIT_SCALE,
                    64*BloodHungerGame.UNIT_SCALE, 64*BloodHungerGame.UNIT_SCALE, null));
        }*/
    }

    @Override
    protected void update(float delta) {
        drawBackgroundGrid();
        engine.update(delta);
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        Entity player = engine.getEntitiesFor(Family.all(PlayerComponent.class).get()).first();
        PlayerComponent playerComponent = Mapper.playerComponent.get(player);
        if (playerComponent.weapon.canShoot()) {
            playerComponent.shoot();
            PositionComponent pc = Mapper.positionComponent.get(player);
            DimensionComponent dc = Mapper.dimensionComponent.get(player);
            RotationComponent rc = Mapper.rotationComponent.get(player);
            Entity bullet = EntityFactory.createBullet(0, 0, rc.lookingAngle);
            PositionComponent bulletPos = Mapper.positionComponent.get(bullet);
            DimensionComponent bulletDim = Mapper.dimensionComponent.get(bullet);
            //Okay, get inital position
            Vector2 bulletPosition = playerComponent.weapon.getInitialBulletPosition(pc, dc, rc);
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
        float yOffset;
        switch (keycode) {
            case Input.Keys.NUM_1:
                player = engine.getEntitiesFor(Family.all(PlayerComponent.class).get()).first();
                Mapper.playerComponent.get(player).weapon = PlayerComponent.Weapon.FLASHLIGHT;
                keyPressed = true;
                break;
            case Input.Keys.NUM_2:
                player = engine.getEntitiesFor(Family.all(PlayerComponent.class).get()).first();
                Mapper.playerComponent.get(player).weapon = PlayerComponent.Weapon.HANDGUN;
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
}
