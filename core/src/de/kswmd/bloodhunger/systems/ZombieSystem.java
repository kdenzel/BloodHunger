package de.kswmd.bloodhunger.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import de.kswmd.bloodhunger.BloodHungerGame;
import de.kswmd.bloodhunger.components.*;
import de.kswmd.bloodhunger.utils.Mapper;

public class ZombieSystem extends EntitySystem {

    private ImmutableArray<Entity> zombieEntities;
    private ImmutableArray<Entity> playerEntities;
    private ImmutableArray<Entity> staticBoundsEntities;
    //For calculating lookingdirection of zombie
    private Vector2 tmpStartVec = new Vector2();
    private Vector2 tmpEndVec = new Vector2();
    private Vector2 intersectionVec = new Vector2();

    @Override
    public void addedToEngine(Engine engine) {
        zombieEntities = engine.getEntitiesFor(Family.all(ZombieComponent.class, VelocityComponent.class, DimensionComponent.class, RotationComponent.class).get());
        playerEntities = engine.getEntitiesFor(Family.all(PlayerComponent.class, PositionComponent.class, DimensionComponent.class).get());
        staticBoundsEntities = engine.getEntitiesFor(Family.all(BoundsComponent.class).exclude(VelocityComponent.class).get());
    }

    @Override
    public void removedFromEngine(Engine engine) {
        zombieEntities = null;
        playerEntities = null;
        staticBoundsEntities = null;
    }

    @Override
    public void update(float deltaTime) {
        Entity playerEntity = playerEntities.first();
        PositionComponent playerEntityPosition = Mapper.positionComponent.get(playerEntity);
        DimensionComponent playerDimensionComponent = Mapper.dimensionComponent.get(playerEntity);
        BoundsComponent playerBoundsComponent = Mapper.boundsComponent.get(playerEntity);
        for (Entity zombieEntity : zombieEntities) {
            PositionComponent zombiePosition = Mapper.positionComponent.get(zombieEntity);
            VelocityComponent zombieVelocity = Mapper.velocityComponent.get(zombieEntity);
            ZombieComponent zombieComponent = Mapper.zombieComponent.get(zombieEntity);
            DimensionComponent zombieDimensionComponent = Mapper.dimensionComponent.get(zombieEntity);
            RotationComponent zombieRotationComponent = Mapper.rotationComponent.get(zombieEntity);
            zombieComponent.update(deltaTime);
            zombieVelocity.velocityVec.setLength(0);
            zombieComponent.feetAnimationType = ZombieComponent.FeetAnimationType.IDLE;
            //if zombie can see the player, walk direct to the player
            if (canZombieSeePlayer(zombieEntity, playerEntity)) {
                float angleInRadians = MathUtils.atan2((zombiePosition.y + zombieDimensionComponent.originY) - (playerEntityPosition.y + playerDimensionComponent.originY),
                        (zombiePosition.x + zombieDimensionComponent.originX) - (playerEntityPosition.x + playerDimensionComponent.originX));
                float angleInDegrees = (angleInRadians * MathUtils.radiansToDegrees) + 180;
                zombieRotationComponent.movementAngle = angleInDegrees;
                zombieRotationComponent.lookingAngle = angleInDegrees;
                zombieVelocity.velocityVec.set(zombieComponent.speed, 0);
                zombieComponent.feetAnimationType = ZombieComponent.FeetAnimationType.MOVE;
            }
        }
    }

    private boolean canZombieSeePlayer(Entity zombieEntity, Entity playerEntity) {
        ZombieComponent zombieComponent = Mapper.zombieComponent.get(zombieEntity);
        PositionComponent zombiePosition = Mapper.positionComponent.get(zombieEntity);
        DimensionComponent zombieDimensionComponent = Mapper.dimensionComponent.get(zombieEntity);
        RotationComponent zombieRotationComponent = Mapper.rotationComponent.get(zombieEntity);
        BoundsComponent playerBoundsComponent = Mapper.boundsComponent.get(playerEntity);
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
                if(!Intersector.intersectSegmentPolygon(tmpStartVec, tmpEndVec, poly))
                    continue;
                if (de.kswmd.bloodhunger.math.Intersector.intersectSegmentPolygon(tmpStartVec, tmpEndVec, poly, intersectionVec)) {
                    tmpEndVec.set(intersectionVec);
                }
            }
            for (int z = 0; z < playerBoundsComponent.size(); z++) {
                Polygon poly = playerBoundsComponent.getPolygon(z);
                if (poly == null)
                    continue;

                if (Intersector.intersectSegmentPolygon(tmpStartVec, tmpEndVec, poly))
                    return true;
            }

        }
        return false;
    }
}
