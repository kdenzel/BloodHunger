package de.kswmd.bloodhunger.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import de.kswmd.bloodhunger.BloodHungerGame;
import de.kswmd.bloodhunger.components.*;
import de.kswmd.bloodhunger.math.Intersector;
import de.kswmd.bloodhunger.utils.Mapper;

public class DebugRenderSystem extends EntitySystem {

    private final ShapeRenderer debugRenderer;
    private final Camera camera;
    private Family family;
    private ImmutableArray<Entity> entities;
    private ImmutableArray<Entity> staticBoundsEntities;
    private final World world;
    private final Box2DDebugRenderer box2dDebugRenderer;
    //For drawing lookingdirection of zombie
    private Vector2 tmpStartVec = new Vector2();
    private Vector2 tmpEndVec = new Vector2();
    private Vector2 intersectorVector = new Vector2();


    public DebugRenderSystem(ShapeRenderer debugRenderer, Camera camera, World world) {
        this.debugRenderer = debugRenderer;
        this.camera = camera;
        this.world = world;
        this.box2dDebugRenderer = new Box2DDebugRenderer();
    }

    @Override
    public void addedToEngine(Engine engine) {
        this.entities = engine.getEntitiesFor(Family.all(PositionComponent.class).get());
        this.staticBoundsEntities = engine.getEntitiesFor(Family.all(BoundsComponent.class).exclude(VelocityComponent.class).get());
    }

    @Override
    public void removedFromEngine(Engine engine) {
        this.entities = null;
    }

    @Override
    public void update(float deltaTime) {
        debugRenderer.begin();
        debugRenderer.setProjectionMatrix(camera.combined);
        for (int i = 0; i < entities.size(); ++i) {
            debugRenderer.set(ShapeRenderer.ShapeType.Line);
            Entity entity = entities.get(i);
            PositionComponent pc = Mapper.positionComponent.get(entity);
            if (Mapper.dimensionComponent.has(entity)) {
                debugRenderer.setColor(Color.YELLOW);
                DimensionComponent dc = Mapper.dimensionComponent.get(entity);
                debugRenderer.rect(pc.x, pc.y, dc.width, dc.height);
                debugRenderer.circle(pc.x + dc.originX, pc.y + dc.originY, 2 * BloodHungerGame.UNIT_SCALE);
            }
            if (Mapper.boundsComponent.has(entity)) {
                debugRenderer.setColor(Color.CYAN);
                BoundsComponent bc = Mapper.boundsComponent.get(entity);
                //Draw every polygon on each layer
                for (int z = 0; z < bc.size(); z++) {
                    Polygon poly = bc.getPolygon(z);
                    if (poly == null)
                        continue;
                    debugRenderer.polygon(poly.getTransformedVertices());
                }

                /*Rectangle r = bc.boundaryPolygon.getBoundingRectangle();
                debugRenderer.rect(r.x,r.y,
                        r.width,r.height);*/
            }
            debugRenderer.set(ShapeRenderer.ShapeType.Filled);
            debugRenderer.setColor(Color.BLACK);
            debugRenderer.circle(pc.x, pc.y, 2 * BloodHungerGame.UNIT_SCALE);
            if (Mapper.zombieComponent.has(entity))
                drawZombieView(entity);
        }

        debugRenderer.end();
        box2dDebugRenderer.render(world, camera.combined);
    }

    /**
     * draws the view field of the zombie
     *
     * @param zombieEntity
     */
    private void drawZombieView(Entity zombieEntity) {
        ZombieComponent zombieComponent = Mapper.zombieComponent.get(zombieEntity);
        PositionComponent zombiePosition = Mapper.positionComponent.get(zombieEntity);
        DimensionComponent zombieDimensionComponent = Mapper.dimensionComponent.get(zombieEntity);
        RotationComponent zombieRotationComponent = Mapper.rotationComponent.get(zombieEntity);
        debugRenderer.set(ShapeRenderer.ShapeType.Line);
        debugRenderer.setColor(Color.GRAY);
        float startAngle = zombieRotationComponent.lookingAngle - zombieComponent.frustumAngle / 2;
        float endAngle = zombieRotationComponent.lookingAngle + zombieComponent.frustumAngle / 2;
        for (float angle = startAngle; angle <= endAngle; angle++) {
            tmpStartVec.set(zombiePosition.x + zombieDimensionComponent.originX,
                    zombiePosition.y + zombieDimensionComponent.originY);
            tmpEndVec.set(BloodHungerGame.worldUnits(10), 0);
            tmpEndVec.rotate(angle);
            tmpEndVec.add(tmpStartVec);
            for (Entity staticBound : staticBoundsEntities) {
                BoundsComponent staticBoundsComponent = Mapper.boundsComponent.get(staticBound);
                if (staticBoundsComponent.size() < 2)
                    continue;
                Polygon poly = staticBoundsComponent.getPolygon(1);
                if (poly == null)
                    continue;
                if (Intersector.intersectSegmentPolygon(tmpStartVec, tmpEndVec, poly, intersectorVector)) {
                    if (tmpStartVec.dst2(intersectorVector) < tmpStartVec.dst2(tmpEndVec))
                        tmpEndVec.set(intersectorVector);
                }
            }
            debugRenderer.line(tmpStartVec.x, tmpStartVec.y, tmpEndVec.x, tmpEndVec.y);
        }
    }

    /**
     * only used for debugging the debug function for zombies
     * @param player the player entity object
     */
    private void drawZombieViewForPlayer(Entity player) {
        debugRenderer.set(ShapeRenderer.ShapeType.Line);
        debugRenderer.setColor(Color.GRAY);
        PositionComponent positionComponent = Mapper.positionComponent.get(player);
        DimensionComponent dimensionComponent = Mapper.dimensionComponent.get(player);
        RotationComponent rotationComponent = Mapper.rotationComponent.get(player);
        float startAngle = rotationComponent.lookingAngle - 30;
        float endAngle = rotationComponent.lookingAngle + 30;
        for (float angle = startAngle; angle <= endAngle; angle++) {
            tmpStartVec.set(positionComponent.x + dimensionComponent.originX,
                    positionComponent.y + dimensionComponent.originY);
            tmpEndVec.set(BloodHungerGame.worldUnits(10), 0);
            tmpEndVec.rotate(angle);
            tmpEndVec.add(tmpStartVec);
            for (Entity staticBound : staticBoundsEntities) {
                BoundsComponent staticBoundsComponent = Mapper.boundsComponent.get(staticBound);
                if (staticBoundsComponent.size() < 2)
                    continue;
                Polygon poly = staticBoundsComponent.getPolygon(1);
                if (poly == null)
                    continue;
                if (Intersector.intersectSegmentPolygon(tmpStartVec, tmpEndVec, poly, intersectorVector)) {
                    tmpEndVec.set(intersectorVector);
                }
            }
            debugRenderer.line(tmpStartVec.x, tmpStartVec.y, tmpEndVec.x, tmpEndVec.y);
        }
    }
}

