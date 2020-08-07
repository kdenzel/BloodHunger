package de.kswmd.bloodhunger.screens;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import de.kswmd.bloodhunger.BloodHungerGame;
import de.kswmd.bloodhunger.components.PlayerControlComponent;
import de.kswmd.bloodhunger.components.PositionComponent;
import de.kswmd.bloodhunger.components.RotationComponent;
import de.kswmd.bloodhunger.factories.EntityFactory;
import de.kswmd.bloodhunger.systems.*;

public class GameScreen extends BaseScreen {

    public static final int UNIT_SIZE = 32;

    private ComponentMapper<PositionComponent> cmpc = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<RotationComponent> cmrc = ComponentMapper.getFor(RotationComponent.class);

    private FollowMouseSystem followMouseSystem;
    private PlayerControlSystem playerControlSystem;
    private RotationSystem rotationSystem;
    private MovementSystem movementSystem;
    private BoundsCollisionSystem boundsCollisionSystem;
    private CenterCameraSystem centerCameraSystem;
    private DebugRenderSystem debugRenderSystem;
    private EnemyFollowPlayerSystem enemyFollowPlayerSystem;
    private BulletSystem bulletSystem;

    private Engine engine;
    private ShapeRenderer shapeRenderer;


    public GameScreen(BloodHungerGame game) {
        super(game);
    }

    @Override
    protected void initialize() {
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
        bulletSystem = new BulletSystem();

        engine.addSystem(followMouseSystem);
        engine.addSystem(playerControlSystem);
        engine.addSystem(enemyFollowPlayerSystem);
        engine.addSystem(rotationSystem);
        engine.addSystem(movementSystem);
        engine.addSystem(boundsCollisionSystem);
        engine.addSystem(bulletSystem);
        engine.addSystem(centerCameraSystem);
        engine.addSystem(debugRenderSystem);


        engine.addEntity(EntityFactory.createPlayer(null));
        engine.addEntity(EntityFactory.createWall(0, 0, 2000, UNIT_SIZE, null));
        int enemies = MathUtils.random(30) + 10;
        for (int i = 0; i < enemies; i++) {
            engine.addEntity(EntityFactory.createEnemey(100, 100, UNIT_SIZE, UNIT_SIZE, null));
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
        engine.update(delta);
        shapeRenderer.end();
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        Entity player = engine.getEntitiesFor(Family.all(PlayerControlComponent.class).get()).first();
        PositionComponent pc = cmpc.get(player);
        engine.addEntity(EntityFactory.createBullet(pc.x, pc.y, cmrc.get(player).lookingAngle));
        return false;
    }
}
