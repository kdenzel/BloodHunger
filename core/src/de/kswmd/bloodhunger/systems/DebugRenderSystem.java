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

public class DebugRenderSystem extends EntitySystem {

    private ShapeRenderer debugRenderer;
    private Camera camera;
    private Family family;
    private ImmutableArray<Entity> entities;

    private ComponentMapper<BoundsComponent> cmbc = ComponentMapper.getFor(BoundsComponent.class);
    private ComponentMapper<PositionComponent> cmpc = ComponentMapper.getFor(PositionComponent.class);

    public DebugRenderSystem(Camera camera) {
        family = Family.all(PositionComponent.class).get();
        debugRenderer = new ShapeRenderer();
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
        debugRenderer.setProjectionMatrix(camera.combined);
        debugRenderer.begin(ShapeRenderer.ShapeType.Line);
        debugRenderer.setColor(Color.CYAN);
        for (int i = 0; i < entities.size(); ++i) {
            Entity entity = entities.get(i);
            PositionComponent pc = cmpc.get(entity);
            if(cmbc.has(entity)){
                BoundsComponent bc = cmbc.get(entity);
                debugRenderer.polygon(bc.boundaryPolygon.getTransformedVertices());
            }

        }
        debugRenderer.end();
    }
}
