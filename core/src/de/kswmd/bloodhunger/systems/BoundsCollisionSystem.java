package de.kswmd.bloodhunger.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import de.kswmd.bloodhunger.components.BoundsComponent;
import de.kswmd.bloodhunger.components.PositionComponent;

public class BoundsCollisionSystem extends EntitySystem {

    private ImmutableArray<Entity> entities;

    private ComponentMapper<BoundsComponent> cmbc = ComponentMapper.getFor(BoundsComponent.class);
    private ComponentMapper<PositionComponent> cmpc = ComponentMapper.getFor(PositionComponent.class);

    @Override
    public void addedToEngine(Engine engine) {
        this.entities = engine.getEntitiesFor(Family.all(BoundsComponent.class).get());
    }

    @Override
    public void removedFromEngine(Engine engine) {
        this.entities = null;
    }

    @Override
    public void update(float deltaTime) {
        for (int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);
            for (int j = 0; j < entities.size(); j++) {
                Entity entityToCollideWith = entities.get(j);
                if (entity.equals(entityToCollideWith)) {
                    continue;
                }
                BoundsComponent entityBounds = cmbc.get(entity);
                BoundsComponent otherBounds = cmbc.get(entityToCollideWith);
                Polygon poly1 = entityBounds.boundaryPolygon;
                Polygon poly2 = otherBounds.boundaryPolygon;
                if (!poly1.getBoundingRectangle().overlaps(poly2.getBoundingRectangle())){
                    continue;
                }
                Intersector.MinimumTranslationVector mtv = new Intersector.MinimumTranslationVector();
                boolean polygonOverlap = Intersector.overlapConvexPolygons(poly1, poly2, mtv);
                if (polygonOverlap){
                    if(cmpc.has(entity)){
                        PositionComponent pc = cmpc.get(entity);
                        pc.x += mtv.normal.x * mtv.depth;
                        pc.y += mtv.normal.y * mtv.depth;
                    }
                }
            }
        }
    }
}
