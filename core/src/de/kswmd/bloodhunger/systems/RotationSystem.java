package de.kswmd.bloodhunger.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import de.kswmd.bloodhunger.components.BoundsComponent;
import de.kswmd.bloodhunger.components.DimensionComponent;
import de.kswmd.bloodhunger.components.RotationComponent;

public class RotationSystem extends IteratingSystem {

    private ComponentMapper<RotationComponent> cmrc = ComponentMapper.getFor(RotationComponent.class);
    private ComponentMapper<BoundsComponent> cmbc = ComponentMapper.getFor(BoundsComponent.class);

    public RotationSystem() {
        super(Family.all(RotationComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        RotationComponent rc = cmrc.get(entity);
        if(cmbc.has(entity)){
            cmbc.get(entity).rotate(rc.angle);
        }
    }
}
