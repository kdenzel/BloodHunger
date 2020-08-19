package de.kswmd.bloodhunger.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import de.kswmd.bloodhunger.BloodHungerGame;
import de.kswmd.bloodhunger.components.BoundsComponent;
import de.kswmd.bloodhunger.components.DimensionComponent;
import de.kswmd.bloodhunger.components.PositionComponent;
import de.kswmd.bloodhunger.components.RotationComponent;
import de.kswmd.bloodhunger.utils.Mapper;
import org.w3c.dom.css.Rect;

public class DebugRenderSystem extends EntitySystem {

    private ShapeRenderer debugRenderer;
    private Camera camera;
    private Family family;
    private ImmutableArray<Entity> entities;

    public DebugRenderSystem(ShapeRenderer debugRenderer, Camera camera) {
        family = Family.all(PositionComponent.class).get();
        this.debugRenderer = debugRenderer;
        this.camera = camera;
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
                    debugRenderer.polygon(bc.getPolygon(z).getTransformedVertices());
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
    }
}
