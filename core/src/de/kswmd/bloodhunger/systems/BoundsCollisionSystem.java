package de.kswmd.bloodhunger.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import de.kswmd.bloodhunger.components.*;
import de.kswmd.bloodhunger.utils.Mapper;

public class BoundsCollisionSystem extends EntitySystem {

    private ImmutableArray<Entity> boundEntitiesWithoutPlayerAndBullets;
    private ImmutableArray<Entity> playerEntities;

    @Override
    public void addedToEngine(Engine engine) {
        this.boundEntitiesWithoutPlayerAndBullets = engine.getEntitiesFor(Family.all(BoundsComponent.class).exclude(BulletComponent.class, PlayerComponent.class).get());
        this.playerEntities = engine.getEntitiesFor(Family.all(PlayerComponent.class).get());
    }

    @Override
    public void removedFromEngine(Engine engine) {
        this.boundEntitiesWithoutPlayerAndBullets = null;
    }

    @Override
    public void update(float deltaTime) {
        //Check if player collides with entities like enemies or static objects (Objects without velocity)
        for (Entity playerEntity : playerEntities) {
            BoundsComponent entityBounds = Mapper.boundsComponent.get(playerEntity);

            for (Entity otherBoundsEntity : boundEntitiesWithoutPlayerAndBullets) {
                BoundsComponent otherBounds = Mapper.boundsComponent.get(otherBoundsEntity);
                Polygon poly1 = entityBounds.getPolygon(0);
                Polygon poly2 = otherBounds.getPolygon(0);
                if (!poly1.getBoundingRectangle().overlaps(poly2.getBoundingRectangle())) {
                    continue;
                }
                Intersector.MinimumTranslationVector mtv = new Intersector.MinimumTranslationVector();
                boolean polygonOverlap = Intersector.overlapConvexPolygons(poly1, poly2, mtv);
                if (polygonOverlap && !Mapper.velocityComponent.has(otherBoundsEntity)) {
                    //If velocity is attached to the entity, it is a dynamic object that can be moved
                    if (Mapper.positionComponent.has(playerEntity) && Mapper.velocityComponent.has(playerEntity)) {
                        PositionComponent pc = Mapper.positionComponent.get(playerEntity);
                        pc.x += mtv.normal.x * mtv.depth;
                        pc.y += mtv.normal.y * mtv.depth;
                    }
                } //Otherwise the player overlaps with an enemy, take damage
                else if(polygonOverlap && Mapper.enemyComponent.has(otherBoundsEntity)) {
                    Gdx.app.debug("DAMAGE", "OUCH " + System.currentTimeMillis());
                } else {
                    Gdx.app.debug("COLLISION DETECTED", "WITH SOME MOVING OBJECT");
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
                BoundsComponent entityBounds = Mapper.boundsComponent.get(entity);
                BoundsComponent otherBounds = Mapper.boundsComponent.get(entityToCollideWith);
                Polygon poly1 = entityBounds.getPolygon(0);
                Polygon poly2 = otherBounds.getPolygon(0);
                if (!poly1.getBoundingRectangle().overlaps(poly2.getBoundingRectangle())) {
                    continue;
                }
                Intersector.MinimumTranslationVector mtv = new Intersector.MinimumTranslationVector();
                boolean polygonOverlap = Intersector.overlapConvexPolygons(poly1, poly2, mtv);

                if (polygonOverlap) {
                    //If velocity is attached to the entity, it is a dynamic object that can be moved
                    if (Mapper.positionComponent.has(entity) && Mapper.velocityComponent.has(entity)) {
                        PositionComponent pc = Mapper.positionComponent.get(entity);
                        pc.x += mtv.normal.x * mtv.depth;
                        pc.y += mtv.normal.y * mtv.depth;
                    }
                }
            }
        }
    }
}
