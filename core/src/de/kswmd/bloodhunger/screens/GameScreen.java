package de.kswmd.bloodhunger.screens;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import de.kswmd.bloodhunger.BloodHungerGame;
import de.kswmd.bloodhunger.components.DimensionComponent;
import de.kswmd.bloodhunger.components.PlayerComponent;
import de.kswmd.bloodhunger.components.PositionComponent;
import de.kswmd.bloodhunger.components.RotationComponent;
import de.kswmd.bloodhunger.factories.EntityFactory;
import de.kswmd.bloodhunger.systems.*;
import de.kswmd.bloodhunger.utils.Mapper;

public class GameScreen extends BaseScreen {

    public static final int UNIT_SIZE = 32;
    private Vector2 bulletOffset = new Vector2();

    private FollowMouseSystem followMouseSystem;
    private PlayerControlSystem playerControlSystem;
    private RotationSystem rotationSystem;
    private MovementSystem movementSystem;
    private BoundsCollisionSystem boundsCollisionSystem;
    private CenterCameraSystem centerCameraSystem;
    private DebugRenderSystem debugRenderSystem;
    private EnemyFollowPlayerSystem enemyFollowPlayerSystem;
    private PlayerAnimationSystem playerAnimationSystem;
    private BulletSystem bulletSystem;

    private Engine engine;
    private ShapeRenderer shapeRenderer;
    private SpriteBatch spriteBatch;


    public GameScreen(BloodHungerGame game) {
        super(game);
    }

    @Override
    protected void initialize() {
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
        playerAnimationSystem = new PlayerAnimationSystem(spriteBatch,camera);
        bulletSystem = new BulletSystem();

        engine.addSystem(followMouseSystem);
        engine.addSystem(playerControlSystem);
        engine.addSystem(enemyFollowPlayerSystem);
        engine.addSystem(rotationSystem);
        engine.addSystem(movementSystem);
        engine.addSystem(boundsCollisionSystem);
        engine.addSystem(bulletSystem);
        engine.addSystem(centerCameraSystem);
        engine.addSystem(playerAnimationSystem);
        engine.addSystem(debugRenderSystem);


        engine.addEntity(EntityFactory.createPlayer(game.assetManager));
        engine.addEntity(EntityFactory.createWall(0, 0, 2000, UNIT_SIZE, null));
        int enemies = MathUtils.random(30) + 10;
        for (int i = 0; i < enemies; i++) {
            //engine.addEntity(EntityFactory.createEnemey(100, 100, UNIT_SIZE, UNIT_SIZE, null));
        }
    }

    @Override
    protected void update(float delta) {
        shapeRenderer.begin();
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.setColor(Color.WHITE);
        int gridSize = 100;
        for (int y = 0; y <= UNIT_SIZE * gridSize; y += UNIT_SIZE) {
            for (int x = 0; x <= UNIT_SIZE * gridSize; x += UNIT_SIZE) {
                shapeRenderer.line(x, 0, x, UNIT_SIZE * gridSize);
            }
            shapeRenderer.line(0, y, UNIT_SIZE * gridSize, y);
        }
        shapeRenderer.end();
        engine.update(delta);

    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        Entity player = engine.getEntitiesFor(Family.all(PlayerComponent.class).get()).first();
        PositionComponent pc = Mapper.positionComponent.get(player);
        DimensionComponent dc = Mapper.dimensionComponent.get(player);
        Entity bullet = EntityFactory.createBullet(0, 0, Mapper.rotationComponent.get(player).lookingAngle);
        PositionComponent bulletPos = Mapper.positionComponent.get(bullet);
        DimensionComponent bulletDim = Mapper.dimensionComponent.get(bullet);
        bulletOffset.set(dc.getOriginX()*2,dc.getOriginY()*2);
        bulletOffset.setAngle(Mapper.rotationComponent.get(player).lookingAngle);
        bulletPos.x = -bulletDim.getOriginX() + dc.getOriginX() + pc.x + bulletOffset.x;
        bulletPos.y = -bulletDim.getOriginY() + dc.getOriginY() + pc.y + bulletOffset.y;
        engine.addEntity(bullet);
        return false;
    }
}
