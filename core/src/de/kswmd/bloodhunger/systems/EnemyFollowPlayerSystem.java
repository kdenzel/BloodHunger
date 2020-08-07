package de.kswmd.bloodhunger.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.MathUtils;
import de.kswmd.bloodhunger.components.*;

public class EnemyFollowPlayerSystem extends EntitySystem {

    private ImmutableArray<Entity> enemieEntities;
    private ImmutableArray<Entity> playerEntities;

    private ComponentMapper<PositionComponent> cmpc = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<VelocityComponent> cmvc = ComponentMapper.getFor(VelocityComponent.class);
    private ComponentMapper<RotationComponent> cmrc = ComponentMapper.getFor(RotationComponent.class);
    private ComponentMapper<EnemyComponent> cmec = ComponentMapper.getFor(EnemyComponent.class);

    @Override
    public void addedToEngine(Engine engine) {
        enemieEntities = engine.getEntitiesFor(Family.all(EnemyComponent.class, VelocityComponent.class, RotationComponent.class).get());
        playerEntities = engine.getEntitiesFor(Family.all(PlayerControlComponent.class, PositionComponent.class).get());
    }

    @Override
    public void removedFromEngine(Engine engine) {
        enemieEntities = null;
        playerEntities = null;
    }

    @Override
    public void update(float deltaTime) {
        Entity playerEntity = playerEntities.first();
        PositionComponent playerEntityPosition = cmpc.get(playerEntity);
        for(Entity enemyEntity : enemieEntities) {
            PositionComponent enemyEntityPosition = cmpc.get(enemyEntity);
            VelocityComponent enemyVelocity = cmvc.get(enemyEntity);
            EnemyComponent enemyComponent = cmec.get(enemyEntity);
            enemyVelocity.velocityVec.setLength(0);
            float angleInRadians = MathUtils.atan2(enemyEntityPosition.y - playerEntityPosition.y, enemyEntityPosition.x - playerEntityPosition.x);
            float angleInDegrees = (angleInRadians * MathUtils.radiansToDegrees) +180;
            cmrc.get(enemyEntity).movementAngle = angleInDegrees;
            enemyVelocity.velocityVec.set(enemyComponent.speed,0).setAngle(angleInDegrees);

        }
    }
}
