package de.kswmd.bloodhunger.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import de.kswmd.bloodhunger.BloodHungerGame;
import de.kswmd.bloodhunger.components.*;
import de.kswmd.bloodhunger.skins.PlayerSkin;
import de.kswmd.bloodhunger.utils.Mapper;

public class BoundsCollisionSystem extends EntitySystem {

    private final BloodHungerGame game;
    private ImmutableArray<Entity> boundEntitiesWithoutPlayerAndBullets;
    private ImmutableArray<Entity> playerEntities;
    private Intersector.MinimumTranslationVector mtv = new Intersector.MinimumTranslationVector();

    public BoundsCollisionSystem(BloodHungerGame game) {
        this.game = game;
    }

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
            BoundsComponent playerBounds = Mapper.boundsComponent.get(playerEntity);

            for (Entity otherBoundsEntity : boundEntitiesWithoutPlayerAndBullets) {
                BoundsComponent otherBounds = Mapper.boundsComponent.get(otherBoundsEntity);
                //Check all layers of the player
                for (int z = 0; z < otherBounds.size(); z++) {
                    if (otherBounds.size() <= z || playerBounds.size() <= z)
                        continue;
                    Polygon poly1 = playerBounds.getPolygon(z);
                    Polygon poly2 = otherBounds.getPolygon(z);
                    if (poly1 == null || poly2 == null || !poly1.getBoundingRectangle().overlaps(poly2.getBoundingRectangle())) {
                        continue;
                    }
                    //BE AWARE THE POLYGONS MUST BE COUNTER CLOCKWISE OTHERWISE GLITCHES APPEAR!!!!!!!1111!!!!!!!!!!!!!!!!
                    boolean polygonOverlap = Intersector.overlapConvexPolygons(poly1, poly2, mtv);
                    if (polygonOverlap) {
                        onPlayerCollidesWithObject(playerEntity, otherBoundsEntity);
                    }
                }
            }
        }
        //Check other objects to collide with each other like enemies with enemies and enemies with objects
        for (int i = 0; i < boundEntitiesWithoutPlayerAndBullets.size(); i++) {
            Entity entity = boundEntitiesWithoutPlayerAndBullets.get(i);
            for (int j = 0; j < boundEntitiesWithoutPlayerAndBullets.size(); j++) {
                Entity entityToCollideWith = boundEntitiesWithoutPlayerAndBullets.get(j);
                if (i == j)
                    continue;

                BoundsComponent entityBounds = Mapper.boundsComponent.get(entity);
                BoundsComponent otherBounds = Mapper.boundsComponent.get(entityToCollideWith);
                //Check all layers of the moving entity
                for (int z = 0; z < entityBounds.size(); z++) {
                    if (otherBounds.size() <= z || entityBounds.size() <= z)
                        continue;
                    Polygon poly1 = entityBounds.getPolygon(0);
                    Polygon poly2 = otherBounds.getPolygon(0);
                    if (poly1 == null || poly2 == null || !poly1.getBoundingRectangle().overlaps(poly2.getBoundingRectangle())) {
                        continue;
                    }
                    boolean polygonOverlap = Intersector.overlapConvexPolygons(poly1, poly2, mtv);

                    if (polygonOverlap) {
                        //If velocity is attached to the entity, it is a dynamic object that can be moved
                        if (Mapper.positionComponent.has(entity) && Mapper.velocityComponent.has(entity)) {
                            PositionComponent pc = Mapper.positionComponent.get(entity);
                            pc.moveBy(mtv.normal.x * mtv.depth, mtv.normal.y * mtv.depth);
                        }
                    }
                }
            }
        }
    }

    /**
     * @param playerEntity      the player
     * @param otherBoundsEntity the entity he collides with
     */
    private void onPlayerCollidesWithObject(Entity playerEntity, Entity otherBoundsEntity) {
        if (!Mapper.velocityComponent.has(otherBoundsEntity)) {
            //If it is an item, collect it
            if (Mapper.itemComponent.has(otherBoundsEntity)) {
                ItemComponent itemComponent = Mapper.itemComponent.get(otherBoundsEntity);
                //If item was added to inventory remove the entity
                if (Mapper.playerComponent.get(playerEntity).inventory.addItem(itemComponent))
                    getEngine().removeEntity(otherBoundsEntity);
            }
            //If next level was reached, set next screen
            else if (Mapper.levelExitComponent.has(otherBoundsEntity)) {
                game.setLevel(Mapper.levelExitComponent.get(otherBoundsEntity));
            } else if (Mapper.playerSkinComponent.has(otherBoundsEntity)) {
                PlayerComponent pc = Mapper.playerComponent.get(playerEntity);
                PlayerSkin skin = Mapper.playerSkinComponent.get(otherBoundsEntity).skin;
                pc.setSkin(skin);
            }
            //If velocity is attached to the playerentity, it is a dynamic object that can be moved
            else if (Mapper.positionComponent.has(playerEntity) && Mapper.velocityComponent.has(playerEntity)) {
                PositionComponent pc = Mapper.positionComponent.get(playerEntity);
                float x = mtv.depth * mtv.normal.x;
                float y = mtv.depth * mtv.normal.y;
                pc.moveBy(x, y);
            }
        } //Otherwise the player overlaps with an enemy, take damage
        else if (Mapper.enemyComponent.has(otherBoundsEntity)) {
            Gdx.app.debug("DAMAGE", "OUCH " + System.currentTimeMillis());
        } else {
            Gdx.app.debug("COLLISION DETECTED", "WITH SOME MOVING OBJECT");
        }
    }
}
