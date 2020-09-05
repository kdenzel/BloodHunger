package de.kswmd.bloodhunger.systems;

import box2dLight.DirectionalLight;
import box2dLight.Light;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import de.kswmd.bloodhunger.components.LightComponent;
import de.kswmd.bloodhunger.utils.Mapper;

public class SunCycleSystem extends IteratingSystem {

    private float degrees = -1;

    public SunCycleSystem(){
        super(Family.all(LightComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Light l = Mapper.lightComponent.get(entity).getLightReference();
        if(l instanceof DirectionalLight){
            degrees = degrees == - 1 ? l.getDirection() : degrees;
            l.setHeight(degrees);
            l.setDirection(degrees);
            degrees += 360-(deltaTime*4);
        }
    }
}
