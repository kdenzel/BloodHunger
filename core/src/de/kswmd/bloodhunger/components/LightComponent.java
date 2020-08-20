package de.kswmd.bloodhunger.components;

import box2dLight.Light;
import com.badlogic.ashley.core.Component;

public class LightComponent implements Component {

    public enum Type{
        POINT,
        CONE
    }

    public Type type = Type.POINT;
    private Light lightReference;

    public LightComponent(){}

    public LightComponent(Type type){
        this.type = type;
    }

    public void setLightReference(Light lightReference) {
        this.lightReference = lightReference;
    }

    public Light getLightReference() {
        return lightReference;
    }
}
