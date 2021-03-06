package de.kswmd.bloodhunger.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import de.kswmd.bloodhunger.components.BoundsComponent;
import de.kswmd.bloodhunger.components.BulletComponent;
import de.kswmd.bloodhunger.components.ZombieComponent;
import de.kswmd.bloodhunger.utils.Mapper;

public class BulletSystem extends EntitySystem {

    private static final String TAG = BulletSystem.class.getSimpleName();

    private ImmutableArray<Entity> bulletEntities;
    private ImmutableArray<Entity> boundsEntities;

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
        //Gdx.app.debug(TAG, "EXECUTE " + deltaTime);
        for (Entity bullet : bulletEntities) {
            BoundsComponent bulletBoundsComponent = Mapper.boundsComponent.get(bullet);
            for (Entity otherBoundsEntity : boundsEntities) {
                BoundsComponent otherBoundsComponent = Mapper.boundsComponent.get(otherBoundsEntity);
                for (int z = 0; z < otherBoundsComponent.size(); z++) {
                    boolean overlaps = bulletBoundsComponent.intersects(otherBoundsComponent,z);
                    if (overlaps) {
                        this.getEngine().removeEntity(bullet);
                        if (Mapper.zombieComponent.has(otherBoundsEntity)) {
                            ZombieComponent zombieComponent = Mapper.zombieComponent.get(otherBoundsEntity);
                            zombieComponent.health -= MathUtils.random(20, 40);
                            if (zombieComponent.health < 0) {
                                this.getEngine().removeEntity(otherBoundsEntity);
                            }
                        }
                    }
                }
            }
            BulletComponent bc = Mapper.bulletComponent.get(bullet);
            if (bc.lifeTime >= BulletComponent.LIFE_TIME) {
                getEngine().removeEntity(bullet);
            }
            bc.lifeTime += deltaTime;
        }
    }
}
