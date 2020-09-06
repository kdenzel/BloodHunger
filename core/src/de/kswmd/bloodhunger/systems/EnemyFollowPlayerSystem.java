package de.kswmd.bloodhunger.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.MathUtils;
import de.kswmd.bloodhunger.components.*;
import de.kswmd.bloodhunger.utils.Mapper;

public class EnemyFollowPlayerSystem extends EntitySystem {

    private ImmutableArray<Entity> enemieEntities;
    private ImmutableArray<Entity> playerEntities;

    @Override
    public void addedToEngine(Engine engine) {
        enemieEntities = engine.getEntitiesFor(Family.all(ZombieComponent.class, VelocityComponent.class, DimensionComponent.class,RotationComponent.class).get());
        playerEntities = engine.getEntitiesFor(Family.all(PlayerComponent.class, PositionComponent.class,DimensionComponent.class).get());
    }

    @Override
    public void removedFromEngine(Engine engine) {
        enemieEntities = null;
        playerEntities = null;
    }

    @Override
    public void update(float deltaTime) {
        Entity playerEntity = playerEntities.first();
        PositionComponent playerEntityPosition = Mapper.positionComponent.get(playerEntity);
        DimensionComponent playerDimensionComponent = Mapper.dimensionComponent.get(playerEntity);
        for(Entity enemyEntity : enemieEntities) {
            PositionComponent enemyEntityPosition = Mapper.positionComponent.get(enemyEntity);
            VelocityComponent enemyVelocity = Mapper.velocityComponent.get(enemyEntity);
            ZombieComponent zombieComponent = Mapper.zombieComponent.get(enemyEntity);
            DimensionComponent enemyDimensionComponent = Mapper.dimensionComponent.get(enemyEntity);
            enemyVelocity.velocityVec.setLength(0);
            float angleInRadians = MathUtils.atan2((enemyEntityPosition.y+enemyDimensionComponent.originY) - (playerEntityPosition.y+playerDimensionComponent.originY),
                    (enemyEntityPosition.x+enemyDimensionComponent.originX) - (playerEntityPosition.x+playerDimensionComponent.originX));
            float angleInDegrees = (angleInRadians * MathUtils.radiansToDegrees) +180;
            Mapper.rotationComponent.get(enemyEntity).movementAngle = angleInDegrees;
            enemyVelocity.velocityVec.set(zombieComponent.speed,0);
            zombieComponent.update(deltaTime);
        }
    }
}
