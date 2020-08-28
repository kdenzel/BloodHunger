package de.kswmd.bloodhunger.components;

import box2dLight.Light;
import com.badlogic.ashley.core.Component;

public class LightComponent implements Component {

    private Light lightReference;

    public LightComponent(){}

    public LightComponent(Light lightReference){
        this.lightReference = lightReference;
    }

    public void setLightReference(Light lightReference) {
        this.lightReference = lightReference;
    }

    public Light getLightReference() {
        return lightReference;
    }

    public void setPosition(float x, float y){
        lightReference.setPosition(x,y);
    }
}
