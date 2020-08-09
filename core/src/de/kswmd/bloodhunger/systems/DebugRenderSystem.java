package de.kswmd.bloodhunger.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import de.kswmd.bloodhunger.components.BoundsComponent;
import de.kswmd.bloodhunger.components.DimensionComponent;
import de.kswmd.bloodhunger.components.PositionComponent;
import de.kswmd.bloodhunger.components.RotationComponent;
import de.kswmd.bloodhunger.utils.Mapper;

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
        debugRenderer.set(ShapeRenderer.ShapeType.Line);
        for (int i = 0; i < entities.size(); ++i) {
            Entity entity = entities.get(i);
            PositionComponent pc = Mapper.positionComponent.get(entity);
            if(Mapper.dimensionComponent.has(entity)){
                debugRenderer.setColor(Color.YELLOW);
                DimensionComponent dc = Mapper.dimensionComponent.get(entity);
                debugRenderer.rect(pc.x,pc.y,dc.width,dc.height);

                debugRenderer.circle(pc.x+dc.originX,pc.y + dc.originY,2);
            }
            if(Mapper.boundsComponent.has(entity)){
                debugRenderer.setColor(Color.CYAN);
                BoundsComponent bc = Mapper.boundsComponent.get(entity);
                debugRenderer.polygon(bc.boundaryPolygon.getTransformedVertices());
            }
        }
        debugRenderer.end();
    }
}
