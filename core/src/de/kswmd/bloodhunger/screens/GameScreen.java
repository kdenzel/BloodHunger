package de.kswmd.bloodhunger.screens;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import de.kswmd.bloodhunger.BloodHungerGame;
import de.kswmd.bloodhunger.components.*;
import de.kswmd.bloodhunger.factories.EntityFactory;
import de.kswmd.bloodhunger.systems.*;

public class GameScreen extends BaseScreen{

    private FollowMouseSystem followMouseSystem;
    private PlayerControlSystem playerControlSystem;
    private RotationSystem rotationSystem;
    private MovementSystem movementSystem;
    private BoundsCollisionSystem boundsCollisionSystem;
    private CenterCameraSystem centerCameraSystem;
    private DebugRenderSystem debugRenderSystem;

    private Engine engine;
    private ShapeRenderer batch;


    public GameScreen(BloodHungerGame game) {
        super(game);
    }

    @Override
    protected void initialize() {
        batch = new ShapeRenderer();
        batch.setAutoShapeType(true);
        engine = new Engine();


        followMouseSystem = new FollowMouseSystem(camera);
        playerControlSystem = new PlayerControlSystem();
        rotationSystem = new RotationSystem();
        movementSystem = new MovementSystem();
        boundsCollisionSystem = new BoundsCollisionSystem();
        centerCameraSystem = new CenterCameraSystem(camera);
        debugRenderSystem = new DebugRenderSystem(camera);

        engine.addSystem(followMouseSystem);
        engine.addSystem(playerControlSystem);
        engine.addSystem(rotationSystem);
        engine.addSystem(movementSystem);
        engine.addSystem(centerCameraSystem);
        engine.addSystem(debugRenderSystem);
        engine.addSystem(boundsCollisionSystem);

        engine.addEntity(EntityFactory.createPlayer());
        engine.addEntity(EntityFactory.createWall(0,0,2000,20));
    }

    @Override
    protected void update(float delta) {
        batch.begin();
        batch.setProjectionMatrix(camera.combined);
        for(int y = 0; y < 2000; y+=20){
            for(int x = 0; x < 2000; x+=20){
                batch.line(x,0,x,2000);
            }
            batch.line(0,y,2000,y);
        }
        batch.end();
        engine.update(delta);
    }
}
