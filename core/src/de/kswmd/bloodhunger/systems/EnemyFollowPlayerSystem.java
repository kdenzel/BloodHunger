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
        enemieEntities = engine.getEntitiesFor(Family.all(EnemyComponent.class, VelocityComponent.class, RotationComponent.class).get());
        playerEntities = engine.getEntitiesFor(Family.all(PlayerComponent.class, PositionComponent.class).get());
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
        for(Entity enemyEntity : enemieEntities) {
            PositionComponent enemyEntityPosition = Mapper.positionComponent.get(enemyEntity);
            VelocityComponent enemyVelocity = Mapper.velocityComponent.get(enemyEntity);
            EnemyComponent enemyComponent = Mapper.enemyComponent.get(enemyEntity);
            enemyVelocity.velocityVec.setLength(0);
            float angleInRadians = MathUtils.atan2(enemyEntityPosition.y - playerEntityPosition.y, enemyEntityPosition.x - playerEntityPosition.x);
            float angleInDegrees = (angleInRadians * MathUtils.radiansToDegrees) +180;
            Mapper.rotationComponent.get(enemyEntity).movementAngle = angleInDegrees;
            enemyVelocity.velocityVec.set(enemyComponent.speed,0).setAngle(angleInDegrees);

        }
    }
}
