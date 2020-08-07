package de.kswmd.bloodhunger.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import de.kswmd.bloodhunger.components.*;

public class BoundsCollisionSystem extends EntitySystem {

    private ImmutableArray<Entity> boundEntitiesWithoutPlayerAndBullets;
    private ImmutableArray<Entity> playerEntities;

    private ComponentMapper<BoundsComponent> cmbc = ComponentMapper.getFor(BoundsComponent.class);
    private ComponentMapper<PositionComponent> cmpc = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<VelocityComponent> cmvc = ComponentMapper.getFor(VelocityComponent.class);
    private ComponentMapper<EnemyComponent> cmec = ComponentMapper.getFor(EnemyComponent.class);

    @Override
    public void addedToEngine(Engine engine) {
        this.boundEntitiesWithoutPlayerAndBullets = engine.getEntitiesFor(Family.all(BoundsComponent.class).exclude(BulletComponent.class, PlayerControlComponent.class).get());
        this.playerEntities = engine.getEntitiesFor(Family.all(PlayerControlComponent.class).get());
    }

    @Override
    public void removedFromEngine(Engine engine) {
        this.boundEntitiesWithoutPlayerAndBullets = null;
    }

    @Override
    public void update(float deltaTime) {
        //Check if player collides with entities like enemies or static objects (Objects without velocity)
        for (Entity playerEntity : playerEntities) {
            for (Entity otherBoundsEntity : boundEntitiesWithoutPlayerAndBullets) {
                BoundsComponent entityBounds = cmbc.get(playerEntity);
                BoundsComponent otherBounds = cmbc.get(otherBoundsEntity);
                Polygon poly1 = entityBounds.boundaryPolygon;
                Polygon poly2 = otherBounds.boundaryPolygon;
                if (!poly1.getBoundingRectangle().overlaps(poly2.getBoundingRectangle())) {
                    continue;
                }
                Intersector.MinimumTranslationVector mtv = new Intersector.MinimumTranslationVector();
                boolean polygonOverlap = Intersector.overlapConvexPolygons(poly1, poly2, mtv);
                if (polygonOverlap && !cmvc.has(otherBoundsEntity)) {
                    //If velocity is attached to the entity, it is a dynamic object that can be moved
                    if (cmpc.has(playerEntity) && cmvc.has(playerEntity)) {
                        PositionComponent pc = cmpc.get(playerEntity);
                        pc.x += mtv.normal.x * mtv.depth;
                        pc.y += mtv.normal.y * mtv.depth;
                    }
                } //Otherwise the player overlaps with an enemy, take damage
                else if(polygonOverlap && cmec.has(otherBoundsEntity)) {
                    Gdx.app.debug("DAMAGE", "OUCH " + System.currentTimeMillis());
                }

            }
        }
        //Check other objects to collide with each other like enemies with enemies and enemies with objects
        for (int i = 0; i < boundEntitiesWithoutPlayerAndBullets.size(); i++) {
            Entity entity = boundEntitiesWithoutPlayerAndBullets.get(i);
            for (int j = 0; j < boundEntitiesWithoutPlayerAndBullets.size(); j++) {
                Entity entityToCollideWith = boundEntitiesWithoutPlayerAndBullets.get(j);
                if (i == j) {
                    continue;
                }
                BoundsComponent entityBounds = cmbc.get(entity);
                BoundsComponent otherBounds = cmbc.get(entityToCollideWith);
                Polygon poly1 = entityBounds.boundaryPolygon;
                Polygon poly2 = otherBounds.boundaryPolygon;
                if (!poly1.getBoundingRectangle().overlaps(poly2.getBoundingRectangle())) {
                    continue;
                }
                Intersector.MinimumTranslationVector mtv = new Intersector.MinimumTranslationVector();
                boolean polygonOverlap = Intersector.overlapConvexPolygons(poly1, poly2, mtv);

                if (polygonOverlap) {
                    //If velocity is attached to the entity, it is a dynamic object that can be moved
                    if (cmpc.has(entity) && cmvc.has(entity)) {
                        PositionComponent pc = cmpc.get(entity);
                        pc.x += mtv.normal.x * mtv.depth;
                        pc.y += mtv.normal.y * mtv.depth;
                    }
                }
            }
        }
    }
}
