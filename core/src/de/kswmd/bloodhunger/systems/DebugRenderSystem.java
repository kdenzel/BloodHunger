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
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import de.kswmd.bloodhunger.BloodHungerGame;
import de.kswmd.bloodhunger.components.BoundsComponent;
import de.kswmd.bloodhunger.components.DimensionComponent;
import de.kswmd.bloodhunger.components.PositionComponent;
import de.kswmd.bloodhunger.utils.Mapper;

public class DebugRenderSystem extends EntitySystem {

    private final ShapeRenderer debugRenderer;
    private final Camera camera;
    private Family family;
    private ImmutableArray<Entity> entities;
    private final World world;
    private final Box2DDebugRenderer box2dDebugRenderer;


    public DebugRenderSystem(ShapeRenderer debugRenderer, Camera camera, World world) {
        family = Family.all(PositionComponent.class).get();
        this.debugRenderer = debugRenderer;
        this.camera = camera;
        this.world = world;
        this.box2dDebugRenderer = new Box2DDebugRenderer();
    }

    @Override
    public void addedToEngine(Engine engine) {
        this.entities = engine.getEntitiesFor(family);
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
                    if(poly == null)
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
        }
        debugRenderer.end();
        box2dDebugRenderer.render(world, camera.combined);
    }
}
