package de.kswmd.bloodhunger.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.math.MathUtils;
import de.kswmd.bloodhunger.components.BoundsComponent;
import de.kswmd.bloodhunger.components.BulletComponent;
import de.kswmd.bloodhunger.components.EnemyComponent;
import de.kswmd.bloodhunger.factories.EntityFactory;
import de.kswmd.bloodhunger.screens.GameScreen;

public class BulletSystem extends EntitySystem {

    private ImmutableArray<Entity> bulletEntities;
    private ImmutableArray<Entity> boundsEntities;

    private ComponentMapper<BoundsComponent> cmbc = ComponentMapper.getFor(BoundsComponent.class);
    private ComponentMapper<EnemyComponent> cmec = ComponentMapper.getFor(EnemyComponent.class);

    @Override
    public void addedToEngine(Engine engine) {
        bulletEntities = engine.getEntitiesFor(Family.all(BulletComponent.class).get());
        boundsEntities = engine.getEntitiesFor(Family.all(BoundsComponent.class).exclude(BulletComponent.class).get());
    }

    @Override
    public void removedFromEngine(Engine engine) {
        bulletEntities = null;
        boundsEntities = null;
    }

    @Override
    public void update(float deltaTime) {
        for(Entity bullet : bulletEntities){
            BoundsComponent bulletBoundsComponent = cmbc.get(bullet);
            for(Entity otherBoundsEntity : boundsEntities){
                BoundsComponent otherBoundsComponent = cmbc.get(otherBoundsEntity);
                if(!bulletBoundsComponent.boundaryPolygon.getBoundingRectangle().overlaps(otherBoundsComponent.boundaryPolygon.getBoundingRectangle())){
                    continue;
                }
                this.getEngine().removeEntity(bullet);
                if(cmec.has(otherBoundsEntity)){
                    EnemyComponent enemyComponent = cmec.get(otherBoundsEntity);
                    enemyComponent.health -= MathUtils.random(20,40);
                    if(enemyComponent.health < 0){
                        this.getEngine().removeEntity(otherBoundsEntity);
                    }
                }
            }
        }
    }
}
